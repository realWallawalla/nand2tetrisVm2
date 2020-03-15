package com.timonsarakinis;

import com.timonsarakinis.codewriter.HackAssemblyTranslator;
import com.timonsarakinis.commands.functions.FunctionCommand;
import com.timonsarakinis.commands.programflow.BranchingCommand;
import com.timonsarakinis.commands.stackoperators.StackCommand;
import com.timonsarakinis.io.FileReaderWriter;
import com.timonsarakinis.parser.HackVmParser;

import java.nio.file.Path;
import java.util.List;

/**
 * Parser module: parses each VM command into its lexical elements
 * • CodeWriter module: writes the assembly code that implements the parsed command
 * • Main: drives the process (VMTranslator)
 **/
public class Main {

    private static HackAssemblyTranslator translator = new HackAssemblyTranslator();

    public static void main(String[] args) {
        //String filePath = "src/main/resources/";
        String filePath = "src/main/resources/";
        //List<Path> filePaths = FileReaderWriter.getPaths(args[0]);
        FileReaderWriter.createDirectory();
        List<Path> filePaths = FileReaderWriter.getPaths(filePath);
        translator.writeInit();
        filePaths.forEach(Main::parseVmFile);
        translator.close();
    }

    private static void parseVmFile(Path filePath) {
        HackVmParser parser = new HackVmParser(filePath);
        translator.extractFileName(filePath.getFileName().toString());
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.getCurrentCommand() == null || parser.commandType() == null) {
                continue;
            }
            switch (parser.commandType()) {
                case PUSH:
                    translator.writePush((StackCommand) parser.getCurrentCommand());
                    break;
                case ARITHMETIC:
                    translator.writeArithmetic(parser.getOperator());
                    break;
                case POP:
                    translator.writePop((StackCommand) parser.getCurrentCommand());
                    break;
                case LABEL:
                    translator.writeLabel((BranchingCommand) parser.getCurrentCommand());
                    break;
                case IF_GOTO:
                    translator.writeIfGoto((BranchingCommand) parser.getCurrentCommand());
                    break;
                case GOTO:
                    translator.writeGoto((BranchingCommand) parser.getCurrentCommand());
                    break;
                case CALL:
                    translator.writeCall((FunctionCommand) parser.getCurrentCommand());
                    break;
                case FUNCTION:
                    translator.writeFunction((FunctionCommand) parser.getCurrentCommand());
                    break;
                case RETURN:
                    translator.writeReturn(parser.getCurrentCommand());
                    break;
            }
        }
    }
}
