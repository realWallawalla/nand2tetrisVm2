package com.timonsarakinis.commands;

import static com.timonsarakinis.commands.CommandType.ARITHMETIC;

public class Arithmetic implements Command {
    private String operation;

    public Arithmetic(String operation) {
        this.operation = operation;
    }

    @Override
    public CommandType getCommandType() {
        return ARITHMETIC;
    }

    @Override
    public String getOperator() {
        return operation;
    }
}
