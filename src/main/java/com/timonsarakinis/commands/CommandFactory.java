package com.timonsarakinis.commands;

import java.util.List;

import static java.lang.Integer.parseInt;

public class CommandFactory {
    public static final int OPERATOR = 0;
    public static final int SEGMENT = 1;
    public static final int ADDRESS = 2;
    public static final String ARG_1_NO_VALUE = "";
    public static final int ARG_2_NO_VALUE = 0;

    public static Command createCommand(List<String> parsedCommand) {
        Command command = null;
        if (parsedCommand.isEmpty()) {
            return null;
        }
        String operation = parsedCommand.get(OPERATOR);
        if (parsedCommand.size() == 1) {
            command = new Arithmetic(operation, ARG_1_NO_VALUE, ARG_2_NO_VALUE);
        } else if (operation.equals("push")) {
            command = new Push(operation, parsedCommand.get(SEGMENT), parseInt(parsedCommand.get(ADDRESS)));
        } else if (operation.equals("pop")) {
            command = new Pop(operation, parsedCommand.get(SEGMENT), parseInt(parsedCommand.get(ADDRESS)));
        }
        return command;
    }
}