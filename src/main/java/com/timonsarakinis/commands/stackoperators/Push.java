package com.timonsarakinis.commands.stackoperators;

import com.timonsarakinis.commands.Command;
import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.PUSH;

public class Push implements Command, StackCommand {
    private String operation;
    private String segment;
    private int index;

    public Push(String operation, String segment, int index) {
        this.operation = operation;
        this.segment = segment;
        this.index = index;
    }

    @Override
    public CommandType getCommandType() {
        return PUSH;
    }

    @Override
    public String getOperator() {
        return operation;
    }

    @Override
    public String getSegment() {
        return segment;
    }

    @Override
    public int getIndex() {
        return index;
    }
}