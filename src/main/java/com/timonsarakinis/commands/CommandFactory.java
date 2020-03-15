package com.timonsarakinis.commands;

import com.timonsarakinis.commands.functions.Call;
import com.timonsarakinis.commands.functions.Function;
import com.timonsarakinis.commands.functions.Return;
import com.timonsarakinis.commands.programflow.Goto;
import com.timonsarakinis.commands.programflow.IfGoto;
import com.timonsarakinis.commands.programflow.Label;
import com.timonsarakinis.commands.stackoperators.Pop;
import com.timonsarakinis.commands.stackoperators.Push;

import java.util.List;

import static java.lang.Integer.parseInt;

public class CommandFactory {
    public static final int OPERATOR = 0;
    public static final int NAME = 1;
    public static final int VALUE = 2;

    public static Command createCommand(List<String> parsedCommand) {
        Command command = null;
        if (parsedCommand.isEmpty() || parsedCommand.get(0).equals("//")) {
            return null;
        }
        String operator = parsedCommand.get(OPERATOR);
        if (operator.equals("return")) {
            command = new Return(operator);
        } else if (parsedCommand.size() == 1) {
            command = new Arithmetic(operator);
        } else if (operator.equals("push")) {
            command = new Push(operator, parsedCommand.get(NAME), parseInt(parsedCommand.get(VALUE)));
        } else if (operator.equals("pop")) {
            command = new Pop(operator, parsedCommand.get(NAME), parseInt(parsedCommand.get(VALUE)));
        } else if (operator.equals("label")) {
            command = new Label(operator, parsedCommand.get(NAME));
        } else if (operator.equals("if-goto")) {
            command = new IfGoto(operator, parsedCommand.get(NAME));
        } else if (operator.equals("goto")) {
            command = new Goto(operator, parsedCommand.get(NAME));
        } else if (operator.equals("call")) {
            command = new Call(operator, parsedCommand.get(NAME), parseInt(parsedCommand.get(VALUE)));
        } else if (operator.equals("function")) {
            command = new Function(operator, parsedCommand.get(NAME), parseInt(parsedCommand.get(VALUE)));
        }
        return command;
    }
}