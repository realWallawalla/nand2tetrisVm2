package com.timonsarakinis.commands;

import com.timonsarakinis.commands.programflow.Goto;
import com.timonsarakinis.commands.programflow.IfGoto;
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
        String operator = parsedCommand.get(OPERATOR);
        if (parsedCommand.size() == SEGMENT) {
            command = new Arithmetic(operator);
        } else if (operator.equals("push")) {
            command = new Push(operator, parsedCommand.get(SEGMENT), parseInt(parsedCommand.get(INDEX)));
        } else if (operator.equals("pop")) {
            command = new Pop(operator, parsedCommand.get(SEGMENT), parseInt(parsedCommand.get(INDEX)));
        } else if (operator.equals("label")) {
            command = new Label(operator, parsedCommand.get(SEGMENT));
        } else if (operator.equals("if-goto")) {
            command = new IfGoto(operator, parsedCommand.get(SEGMENT));
        } else if (operator.equals("goto")) {
            command = new Goto(operator, parsedCommand.get(SEGMENT));
        }
        return command;
    }
}