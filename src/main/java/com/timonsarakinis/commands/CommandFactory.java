package com.timonsarakinis.commands;

import com.timonsarakinis.commands.programflow.Label;
import com.timonsarakinis.commands.stackoperators.Pop;
import com.timonsarakinis.commands.stackoperators.Push;

import java.util.List;

import static java.lang.Integer.parseInt;

public class CommandFactory {
    public static final int OPERATOR = 0;
    public static final int SEGMENT = 1;
    public static final int INDEX = 2;

    public static Command createCommand(List<String> parsedCommand) {
        Command command = null;
        if (parsedCommand.isEmpty()) {
            return null;
        }
        String operation = parsedCommand.get(OPERATOR);
        if (parsedCommand.size() == SEGMENT) {
            command = new Arithmetic(operation);
        } else if (operation.equals("push")) {
            command = new Push(operation, parsedCommand.get(SEGMENT), parseInt(parsedCommand.get(INDEX)));
        } else if (operation.equals("pop")) {
            command = new Pop(operation, parsedCommand.get(SEGMENT), parseInt(parsedCommand.get(INDEX)));
        } else if (operation.equals("label")) {
            command = new Label(operation, parsedCommand.get(SEGMENT));
        }
        return command;
    }
}