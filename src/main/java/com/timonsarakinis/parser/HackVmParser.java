package com.timonsarakinis.parser;

import com.google.common.base.Splitter;
import com.timonsarakinis.commands.Command;
import com.timonsarakinis.commands.CommandFactory;
import com.timonsarakinis.commands.CommandType;
import com.timonsarakinis.io.FileReaderWriter;

import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;

/**
 * Handles the parsing of a single .vm file.
 * Reads a VM command, parses the command into its lexical components, and provides convenient access to these components
 * Ignores all white space and comments
 */
public class HackVmParser {
    private ListIterator<String> iterator;
    private Command currentCommand;

    public HackVmParser(Path inputFilePath) {
        this.iterator = FileReaderWriter.readFile(inputFilePath).listIterator();
    }

    public boolean hasMoreCommands() {
        return iterator.hasNext();
    }

    public void advance() {
        String next = iterator.next();
        List<String> resultList = Splitter.on(" ")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(next);

        this.currentCommand = CommandFactory.createCommand(resultList);
    }

    public CommandType commandType() {
        return currentCommand.getCommandType();
    }

    public Command getCurrentCommand() {
        return currentCommand;
    }

    public String operator() {
        return currentCommand.getOperation();
    }

    public String arg1() {
        return currentCommand.getSegment();
    }

    public int arg2() {
        return currentCommand.getIndex();
    }
}