package com.timonsarakinis;

import com.timonsarakinis.codewriter.HackAssemblyTranslator;
import com.timonsarakinis.io.FileReaderWriter;
import com.timonsarakinis.parser.HackVmParser;

import java.nio.file.Path;
import java.util.List;

import static com.timonsarakinis.commands.CommandType.*;

/**
 *
 * Parser module: parses each VM command into its lexical elements
 • CodeWriter module: writes the assembly code that implements the parsed command
 • Main: drives the process (VMTranslator)
 *
 **/
public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/resources";
        //List<Path> filePaths = FileReaderWriter.getPaths(args[0]);
        FileReaderWriter.createDirectory();
        List<Path> filePaths = FileReaderWriter.getPaths(filePath);
        filePaths.forEach(p -> parseVmFile(p, removeFileNameExtension(p.getFileName().toString())));
    }

    private static String removeFileNameExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private static void parseVmFile(Path filePath, String fileName) {
        HackVmParser parser = new HackVmParser(filePath);
        HackAssemblyTranslator translator = new HackAssemblyTranslator(fileName);
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.getCurrentCommand() == null) {
                continue;
            } else if (parser.commandType() == PUSH) {
                translator.writePush(parser.getCurrentCommand());
            } else if (parser.commandType() == ARITHMETIC) {
                translator.writeArithmetic(parser.operator());
            } else if (parser.commandType() == POP) {
                translator.writePop(parser.getCurrentCommand());
            }
        }
        translator.close();
    }
}
