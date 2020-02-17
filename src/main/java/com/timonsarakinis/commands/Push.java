package com.timonsarakinis.commands;

public class Push implements Command {
    private String operation;
    private String arg1;
    private int arg2;

    public Push(String operation, String arg1, int arg2) {
        this.operation = operation;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PUSH;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public String getSegment() {
        return arg1;
    }

    @Override
    public int getIndex() {
        return arg2;
    }
}