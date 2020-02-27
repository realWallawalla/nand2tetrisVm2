package com.timonsarakinis.commands.programflow;

import com.timonsarakinis.commands.Command;
import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.IF_GOTO;

public class IfGoto implements ProgramFlow {
    private String operator;
    private String variabelName;

    public IfGoto(String operator, String variabelName) {
        this.operator = operator;
        this.variabelName = variabelName;
    }

    @Override
    public CommandType getCommandType() {
        return IF_GOTO;
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