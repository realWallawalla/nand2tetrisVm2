package com.timonsarakinis.commands.programflow;

import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.GOTO;

public class Goto implements BranchingCommand {
    private String operator;
    private String variabelName;

    public Goto(String operator, String variabelName) {
        this.operator = operator;
        this.variabelName = variabelName;
    }

    @Override
    public CommandType getCommandType() {
        return GOTO;
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
