package com.timonsarakinis.commands.stackoperators;

import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.POP;

public class Pop implements StackCommand {
    private String operator;
    private String segment;
    private int index;

    public Pop(String operator, String segment, int index) {
        this.operator = operator;
        this.segment = segment;
        this.index = index;
    }

    @Override
    public CommandType getCommandType() {
        return POP;
    }

    @Override
    public String getOperator() {
        return operator;
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
