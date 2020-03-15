package com.timonsarakinis.commands.functions;

import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.FUNCTION;

public class Function implements FunctionCommand {

    private String functionName;
    private int nArgs;
    private String operator;

    public Function(String operator, String functionName, int nArgs) {
        this.functionName = functionName;
        this.nArgs = nArgs;
        this.operator = operator;
    }

    @Override
    public String getFunctionName() {
        return functionName;
    }

    @Override
    public int getNArgs() {
        return nArgs;
    }

    @Override
    public CommandType getCommandType() {
        return FUNCTION;
    }

    @Override
    public String getOperator() {
        return operator;
    }
}
