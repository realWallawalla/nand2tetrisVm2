package com.timonsarakinis.codewriter;

import com.timonsarakinis.commands.Command;
import com.timonsarakinis.io.FileReaderWriter;

import java.nio.file.Path;
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
    public static final int POINT_TO_THIS = 0;
    /*public static final String GLOBAL_STACK_POINTER_BASE_ADDRESS = "@256";
    public static final int STATIC_SEGMENT_BASE_ADDRESS = 16; // 16 to 255
    public static final String LOCAL_BASE_ADDRESS = "@300";
    public static final String ARG_BASE_ADDRESS = "@400";
    public static final String THIS_BASE_ADDRESS = "@3030";
    public static final String THAT_BASE_ADDRESS = "@3040";*/

    private static int uniquePrefix;
    private final String fileName;
    private Map<String, String> segmentPointerMapping;
    private final Path outPutPath;
    private final StringJoiner output = new StringJoiner(System.lineSeparator());

    public HackAssemblyTranslator(String fileName) {
        this.fileName = fileName;
        this.outPutPath = FileReaderWriter.getOutputPath(fileName + ".asm");
        segmentPointerMapping = initMapping();
/*        initSegmentBaseAddress(GLOBAL_STACK_POINTER_BASE_ADDRESS, segmentPointerMapping.get("constant"));
        initSegmentBaseAddress(LOCAL_BASE_ADDRESS, segmentPointerMapping.get("local"));
        initSegmentBaseAddress(ARG_BASE_ADDRESS, segmentPointerMapping.get("argument"));
        initSegmentBaseAddress(THIS_BASE_ADDRESS, segmentPointerMapping.get("this"));
        initSegmentBaseAddress(THAT_BASE_ADDRESS, segmentPointerMapping.get("that"));*/
    }

    private void initSegmentBaseAddress(String baseAddress, String pointer) {
        output.add("// setup address for pointer");
        output.add(baseAddress);
        output.add("D=A");
        output.add(pointer);
        output.add("M=D" + System.lineSeparator());
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

    public void writeArithmetic(String operator) {
        //write to outputfile the assembly code that implements the given arithmetic command
        ArithmaticType arithmaticType = ArithmaticType.valueOf(operator.toUpperCase());
        uniquePrefix = uniquePrefix + 1;
        switch (arithmaticType) {
            case ADD:
                buildAdd();
                break;
            case SUB:
                buildSub();
                break;
            case NEG:
                buildNeg();
                break;
            case EQ:
                buildEq();
                break;
            case LT:
                buildLessThen();
                break;
            case GT:
                buildGreaterThen();
                break;
            case AND:
                buildAnd();
                break;
            case OR:
                buildOr();
                break;
            case NOT:
                buildNot();
                break;
        }
        incrementStackPointer();
    }

    private void buildAdd() {
        //pops arguments from stack then push result to stack
        output.add("//ADD");
        popTwoFromStack(SP);
        output.add("M=M+D");
    }

    private void popTwoFromStack(String vmSegment) {
        popFromStack(vmSegment);
        output.add(vmSegment);
        output.add("M=M-1");
        output.add("A=M");
    }

    private void popFromStack(String stackPointer) {
        output.add(stackPointer);
        output.add("M=M-1");
        output.add("A=M");
        output.add("D=M");
    }

    private void incrementStackPointer() {
        output.add(SP);
        output.add("M=M+1" + System.lineSeparator());
    }

    private void buildSub() {
        output.add("//SUB");
        popTwoFromStack(SP);
        output.add("M=M-D");
    }

    private void buildNeg() {
        output.add("//NEG");
        popFromStack(SP);
        output.add("M=-M");
    }

    private void buildEq() {
        output.add("//EQ");
        popTwoFromStack(SP);
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

    private void buildLessThen() {
        output.add("//LT");
        popTwoFromStack(SP);
        logicOperationTemplate("IS_LT" + uniquePrefix, LESS_THEN.getLogicExpression());
    }

    private void buildGreaterThen() {
        output.add("//GT");
        popTwoFromStack(SP);
        logicOperationTemplate("IS_GT" + uniquePrefix, GREATER_THEN.getLogicExpression());
    }

    private void buildAnd() {
        output.add("//AND");
        popTwoFromStack(SP);
        output.add("M=M&D");
    }

    private void buildOr() {
        output.add("//OR");
        popTwoFromStack(SP);
        output.add("M=M|D");
    }

    private void buildNot() {
        output.add("//NOT");
        output.add(SP);
        output.add("M=M-1");
        output.add("A=M");
        output.add("M=!M");
    }

    public void writePush(Command command) {
        //write to outputfile the assembly code that implements the given push or pop command
        String vmSegment = segmentPointerMapping.get(command.getSegment());
        int index = command.getIndex();
        output.add("//PUSH");
        if (vmSegment.equals(SP)) {
            output.add("@" + index);
            output.add("D=A");
            pushToStack();
            incrementStackPointer();
        } else if (vmSegment.equals(LCL) || vmSegment.equals(ARG) || vmSegment.equals(THIS) || vmSegment.equals(THAT)) {
            output.add(vmSegment);
            output.add("D=M");
            output.add("@" + index);
            output.add("A=D+A");
            output.add("D=M");
            pushToStack();
            incrementStackPointer();
        } else if (vmSegment.equals(STATIC)) {
            output.add("@" + fileName + "." + command.getIndex());
            output.add("D=M");
            pushToStack();
            incrementStackPointer();
        } else if (vmSegment.equals(TEMP)) {
            output.add(TEMP_BASE_ADDRESS);
            output.add("D=A");
            output.add("@" + index);
            output.add("A=D+A");
            output.add("D=M");
            pushToStack();
            incrementStackPointer();
        }  else if (vmSegment.equals(POINTER)) {
            if (index == POINT_TO_THIS) {
                output.add(THIS);
                output.add("D=M");
                pushToStack();
                incrementStackPointer();
            } else {
                //point to that
                output.add(THAT);
                output.add("D=M");
                pushToStack();
                incrementStackPointer();
            }
        }
    }

    private void pushToStack() {
        output.add(SP);
        output.add("A=M");
        output.add("M=D");
    }

    public void writePop(Command command) {
        //write to outputfile the assembly code that implements the given push or pop command
        String vmSegment = segmentPointerMapping.get(command.getSegment());
        int index = command.getIndex();
        output.add("//POP");
        if (vmSegment.equals(SP)) {
            popFromStack(SP);
            output.add(System.lineSeparator());
        } else if (vmSegment.equals(LCL) || vmSegment.equals(ARG) || vmSegment.equals(THIS) || vmSegment.equals(THAT)) {
            output.add(vmSegment);
            output.add("D=M");
            output.add("@" + index);
            output.add("D=D+A"); //sum of segment base and index
            output.add("@R13");
            output.add("M=D"); // save address to temp ram-address
            popFromStack(SP);
            output.add("@R13");
            output.add("A=M"); //set address to segment-address
            output.add("M=D" + System.lineSeparator()); //save value from stack to segment-address
        } else if (vmSegment.equals(STATIC)) {
            popFromStack(SP);
            output.add("@" + fileName + "." + index);
            output.add("M=D");
        } else if (vmSegment.equals(TEMP)) {
            output.add(TEMP_BASE_ADDRESS);
            output.add("D=A");
            output.add("@" + index);
            output.add("D=D+A");
            output.add("@R13");
            output.add("M=D");
            popFromStack(SP);
            output.add("@R13");
            output.add("A=M");
            output.add("M=D" + System.lineSeparator());
        } else if (vmSegment.equals(POINTER)) {
            if (index == POINT_TO_THIS) {
                popFromStack(SP);
                output.add(THIS);
                output.add("A=M");
                output.add("M=D");
            } else {
                //point to that
                popFromStack(SP);
                output.add(THAT);
                output.add("A=M");
                output.add("M=D");
            }
        }
    }

    public void close() {
        FileReaderWriter.writeToFile(output.toString().getBytes(), outPutPath);
        System.out.println("wrote to file successfully");
    }
}
