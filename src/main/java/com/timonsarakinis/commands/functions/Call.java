package com.timonsarakinis.commands.functions;

import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.CALL;

public class Call implements FunctionCommand {
    private String operator;
    private String functionName;
    private int nArgs;

    public Call(String operator, String functionName, int nArgs) {
        this.operator = operator;
        this.functionName = functionName;
        this.nArgs = nArgs;
    }

    @Override
    public CommandType getCommandType() {
        return CALL;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public String getFunctionName() {
        return functionName;
    }

    @Override
    public int getNArgs() {
        return nArgs;
    }
}
