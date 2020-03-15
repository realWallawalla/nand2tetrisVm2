package com.timonsarakinis.commands.programflow;

import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.LABEL;

public class Label implements BranchingCommand {
    private String operator;
    private String variabelName;

    public Label(String operator, String variabelName) {
        this.operator = operator;
        this.variabelName = variabelName;
    }

    @Override
    public CommandType getCommandType() {
        return LABEL;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public String getVariabelName() {
        return variabelName;
    }
}
