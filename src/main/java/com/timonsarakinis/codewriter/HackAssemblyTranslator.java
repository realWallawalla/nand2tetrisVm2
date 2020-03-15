package com.timonsarakinis.codewriter;

import com.timonsarakinis.commands.Command;
import com.timonsarakinis.commands.functions.Function;
import com.timonsarakinis.commands.functions.FunctionCommand;
import com.timonsarakinis.commands.programflow.BranchingCommand;
import com.timonsarakinis.commands.stackoperators.StackCommand;
import com.timonsarakinis.io.FileReaderWriter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static com.timonsarakinis.codewriter.JumpExpressionType.*;

/**
 *
 * Translator module: Translates hackVmCide code to hackAssemblyCode.
 * Initalizes the different stackPointers in each segment.
 *
 * Hack assembly has 3 memories. D = dataMemory. A = addressMemory. M = memoryRam, address of A -> M = RAM[A]
 * Read more documentation on hackAssymbly code on nand2tetris.org.
 *
 **/

public class HackAssemblyTranslator {
    private static final String SP = "@SP";
    private static final String LCL = "@LCL";
    private static final String ARG = "@ARG";
    private static final String THIS = "@THIS";
    private static final String THAT = "@THAT";
    public static final String STATIC = "static";
    public static final String TEMP = "temp";
    public static final String POINTER = "pointer";
    public static final String TEMP_BASE_ADDRESS = "@5"; //5-12
    public static final String RETURN_LABEL = "RETURN_LABEL";
    public static final int POINT_TO_THIS = 0;
    public static final int SEGMENTS_PLUS_RETURN_ADDRESS = 5;
    private static final String SP_BASE_ADDRESS = "@256";

    private int uniquePrefix;
    private int retLblPrefix;
    private String fileName;
    private Map<String, String> segmentPointerMapping;
    private final StringJoiner output = new StringJoiner(System.lineSeparator());

    public HackAssemblyTranslator() {
        segmentPointerMapping = initMapping();
    }

    private Map<String, String> initMapping() {
        HashMap<String, String> segmentRamAddressMapping = new HashMap<>();
        segmentRamAddressMapping.put("constant", SP);
        segmentRamAddressMapping.put("local", LCL);
        segmentRamAddressMapping.put("argument", ARG);
        segmentRamAddressMapping.put("this", THIS);
        segmentRamAddressMapping.put("that", THAT);
        segmentRamAddressMapping.put(STATIC, STATIC);
        segmentRamAddressMapping.put(TEMP, TEMP);
        segmentRamAddressMapping.put(POINTER, POINTER);
        return segmentRamAddressMapping;
    }

    // writes assembly code that effects VM initialization - bootstrap code at beginning of output file
    public void writeInit() {
        output.add(SP_BASE_ADDRESS);
        output.add("D=A");
        output.add(SP);
        output.add("M=D");
        writeCall(new Function("function", "Sys.init", 0));
    }

    public void writeArithmetic(String operator) {
        //write to outputfile the assembly code that implements the given arithmetic command
        ArithmaticType arithmaticType = ArithmaticType.valueOf(operator.toUpperCase());
        uniquePrefix += 1;
        switch (arithmaticType) {
            case ADD:
                writeAdd();
                break;
            case SUB:
                writeSub();
                break;
            case NEG:
                writeNeg();
                break;
            case EQ:
                writeEq();
                break;
            case LT:
                writeLessThen();
                break;
            case GT:
                writeGreaterThen();
                break;
            case AND:
                writeAnd();
                break;
            case OR:
                writeOr();
                break;
            case NOT:
                writeNot();
                break;
        }
        incrementStackPointer();
        output.add(System.lineSeparator());
    }

    private void writeAdd() {
        output.add("//ADD");
        popTwoFromStack();
        output.add("M=M+D");
    }

    private void popTwoFromStack() {
        popFromStack();
        output.add(SP);
        output.add("M=M-1");
        output.add("A=M");
    }

    private void popFromStack() {
        output.add(SP);
        output.add("M=M-1");
        output.add("A=M");
        output.add("D=M");
    }

    private void incrementStackPointer() {
        output.add(SP);
        output.add("M=M+1");
    }

    private void writeSub() {
        output.add("//SUB");
        popTwoFromStack();
        output.add("M=M-D");
    }

    private void writeNeg() {
        output.add("//NEG");
        popFromStack();
        output.add("M=-M");
    }

    private void writeEq() {
        output.add("//EQ");
        popTwoFromStack();
        logicOperationTemplate("IS_EQ" + uniquePrefix, EQUALS.getLogicExpression());
    }

    private void logicOperationTemplate(String symbol, String jumpExpression) {
        output.add("D=M-D"); // check if zero
        output.add("M=-1"); //true
        output.add("@" + symbol);
        output.add(jumpExpression);
        output.add(SP);
        output.add("A=M");
        output.add("M=0");
        output.add("(" + symbol + ")");
        output.add(SP);
    }

    private void writeLessThen() {
        output.add("//LT");
        popTwoFromStack();
        logicOperationTemplate("IS_LT" + uniquePrefix, LESS_THEN.getLogicExpression());
    }

    private void writeGreaterThen() {
        output.add("//GT");
        popTwoFromStack();
        logicOperationTemplate("IS_GT" + uniquePrefix, GREATER_THEN.getLogicExpression());
    }

    private void writeAnd() {
        output.add("//AND");
        popTwoFromStack();
        output.add("M=M&D");
    }

    private void writeOr() {
        output.add("//OR");
        popTwoFromStack();
        output.add("M=M|D");
    }

    private void writeNot() {
        output.add("//NOT");
        output.add(SP);
        output.add("M=M-1");
        output.add("A=M");
        output.add("M=!M");
    }

    public void writePush(StackCommand command) {
        String vmSegment = segmentPointerMapping.get(command.getSegment());
        int index = command.getIndex();
        output.add("//PUSH");
        if (vmSegment.equals(SP)) {
            pushConstant(index);
        } else if (vmSegment.equals(LCL) || vmSegment.equals(ARG) || vmSegment.equals(THIS) || vmSegment.equals(THAT)) {
            pushToStackFromVmSegment(vmSegment, index);
        } else if (vmSegment.equals(STATIC)) {
            pushToStatic(command);
        } else if (vmSegment.equals(TEMP)) {
            pushToTempSegment(index);
        }  else if (vmSegment.equals(POINTER)) {
            pushToPointer(index);
        }
        output.add(System.lineSeparator());
    }

    private void pushConstant(int value) {
        output.add("@" + value);
        output.add("D=A");
        pushToStack();
        incrementStackPointer();
    }

    private void pushToStack() {
        output.add(SP);
        output.add("A=M");
        output.add("M=D");
    }

    private void pushToStackFromVmSegment(String vmSegment, int index) {
        output.add(vmSegment);
        output.add("D=M");
        output.add("@" + index);
        output.add("A=D+A");
        output.add("D=M");
        pushToStack();
        incrementStackPointer();
    }

    private void pushToStatic(StackCommand command) {
        output.add("@" + fileName + "." + command.getIndex());
        output.add("D=M");
        pushToStack();
        incrementStackPointer();
    }

    private void pushToTempSegment(int index) {
        output.add(TEMP_BASE_ADDRESS);
        output.add("D=A");
        output.add("@" + index);
        output.add("A=D+A");
        output.add("D=M");
        pushToStack();
        incrementStackPointer();
    }

    private void pushToPointer(int index) {
        if (index == POINT_TO_THIS) {
            output.add(THIS);
        } else {
            //point to that
            output.add(THAT);
        }
        output.add("D=M");
        pushToStack();
        incrementStackPointer();
    }


    public void writePop(StackCommand command) {
        //write to outputfile the assembly code that implements the given push or pop command
        String vmSegment = segmentPointerMapping.get(command.getSegment());
        int index = command.getIndex();
        output.add("//POP");
        if (vmSegment.equals(SP)) {
            popConstant();
        } else if (vmSegment.equals(LCL) || vmSegment.equals(ARG) || vmSegment.equals(THIS) || vmSegment.equals(THAT)) {
            popToVmSegment(vmSegment, index);
        } else if (vmSegment.equals(STATIC)) {
            popToStatic(index);
        } else if (vmSegment.equals(TEMP)) {
            popToTempSegment(index);
        } else if (vmSegment.equals(POINTER)) {
            popToThisOrThat(index);
        }
        output.add(System.lineSeparator());
    }

    private void popConstant() {
        popFromStack();
    }

    private void popToVmSegment(String vmSegment, int index) {
        output.add(vmSegment);
        output.add("D=M");
        output.add("@" + index);
        output.add("D=D+A"); //sum of segment base and index
        output.add("@R13");
        output.add("M=D"); // save address to temp ram-address
        popFromStack();
        output.add("@R13");
        output.add("A=M"); //set address to segment-address
        output.add("M=D"); //save value from stack to segment-address
    }

    private void popToStatic(int index) {
        popFromStack();
        output.add("@" + fileName + "." + index);
        output.add("M=D");
    }

    private void popToTempSegment(int index) {
        output.add(TEMP_BASE_ADDRESS);
        output.add("D=A");
        output.add("@" + index);
        output.add("D=D+A");
        output.add("@R13");
        output.add("M=D");
        popFromStack();
        output.add("@R13");
        output.add("A=M");
        output.add("M=D");
    }


    private void popToThisOrThat(int index) {
        if (index == POINT_TO_THIS) {
            popFromStack();
            output.add(THIS);
        } else {
            //point to that
            popFromStack();
            output.add(THAT);
        }
        output.add("M=D");
    }

    public void writeLabel(BranchingCommand command) {
        output.add("(" + command.getVariabelName() + ")");
        output.add(System.lineSeparator());
    }

    public void writeIfGoto(BranchingCommand command) {
        popFromStack();
        output.add("@"+command.getVariabelName());
        output.add(NOT_EQUAL.getLogicExpression());
        output.add(System.lineSeparator());
    }

    public void writeGoto(BranchingCommand command) {
        output.add("@" + command.getVariabelName());
        output.add(JUMP.getLogicExpression());
        output.add(System.lineSeparator());
    }

    public void writeCall(FunctionCommand command) {
        retLblPrefix += 1;
        String returnLabel = RETURN_LABEL + retLblPrefix;

        output.add("//WRITE_CALL");
        loadReturnAddr(returnLabel);
        pushToStack();
        incrementStackPointer();
        pushMemorySegmentAddress(LCL, ARG, THIS, THAT);
        rePositionArg(command.getNArgs());
        rePositionLcl();
        output.add("@" + command.getFunctionName());
        output.add(JUMP.getLogicExpression());
        output.add("(" + returnLabel + ")");
        output.add(System.lineSeparator());
    }

    private void loadReturnAddr(String returnLabel) {
        output.add("@"+returnLabel);
        output.add("D=A");
    }

    private void pushMemorySegmentAddress(String... segment) {
        Arrays.stream(segment).forEach(memSegment -> {
            output.add(memSegment);
            output.add("D=M");
            pushToStack();
            incrementStackPointer();
        });
    }

    private void rePositionArg(int nArgs) {
        //local ARG = SP-5-nArgs
        int savedFrameSize = SEGMENTS_PLUS_RETURN_ADDRESS + nArgs;
        output.add(SP);
        output.add("D=M");
        output.add("@" + savedFrameSize);
        output.add("D=D-A");
        output.add(ARG);
        output.add("M=D");
    }

    private void rePositionLcl() {
        //LCL = SP
        output.add(SP);
        output.add("D=M");
        output.add(LCL);
        output.add("M=D");
    }

    public void close() {
        FileReaderWriter.writeToFile(output.toString().getBytes());
        System.out.println("wrote to file successfully");
    }

    public void writeFunction(FunctionCommand command) {
        output.add("//WRITE_FUNCTION");
        output.add("(" + command.getFunctionName() + ")");
        for (int index = 0; index < command.getNArgs(); index++) {
            //initilize local variables to zero
            pushConstant(0);
            popToVmSegment(LCL, index);
            incrementStackPointer();
        }
    }

    public void writeReturn(Command command) {
        /*calculate return address retAddr = LCL-5. becuase 5 that is the saved frame of the caller and base lcl pointer is right after saved frame.
         save lcl to temp variable and retAddr to temp variable. pop return value to ARG 0. (By contract return value is alwaus at the top of the stack when returning)
         caller SP should point ARG+1 -> SP = ARG+1
         THAT, THIS, ARG AND LCL is reinstated by taking callee LCL-1..LCL-4 (reverse how they were pushed to stack)
        */
        output.add("//WRITE_RETURN");
        saveCalleeLocalBaseAddress();
        getReturnAddress();
        //pop return value to ARG 0
        popToVmSegment(ARG, 0);
        reInstateStackPointer();
        reInstateMemorySegmentAddresses(THAT, THIS, ARG, LCL);
        jumpToReturnAddress();
        output.add(System.lineSeparator());
    }

    private void saveCalleeLocalBaseAddress() {
        output.add(LCL);
        output.add("D=M");
        output.add("@LCL_CALLEE");
        output.add("M=D");
    }

    private void getReturnAddress() {
        output.add("@LCL");
        output.add("D=M");
        output.add("@5");
        output.add("A=D-A");
        output.add("D=M");
        output.add("@RETURN_ADR");
        output.add("M=D");
    }

    private void reInstateStackPointer() {
        output.add(ARG);
        output.add("D=M");
        output.add(SP);
        output.add("M=D+1"); //SP = ARG+1
    }

    private void reInstateMemorySegmentAddresses(String... segment) {
        for (int n = 1; n < segment.length+1; n++) {
            output.add("@LCL_CALLEE");
            output.add("MD=M-1");
            output.add("A=D");
            output.add("D=M");
            output.add(segment[n-1]);
            output.add("M=D");
        }
    }

    private void jumpToReturnAddress() {
        output.add("@RETURN_ADR");
        output.add("A=M");
        output.add(JUMP.getLogicExpression());
    }

    public void extractFileName(String fileName) {
        this.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
