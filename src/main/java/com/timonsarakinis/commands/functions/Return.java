package com.timonsarakinis.commands.functions;

import com.timonsarakinis.commands.Command;
import com.timonsarakinis.commands.CommandType;

import static com.timonsarakinis.commands.CommandType.RETURN;

public class Return implements Command {
    private String operator;

    public Return(String operator) {
        this.operator = operator;
    }

    @Override
    public CommandType getCommandType() {
        return RETURN;
    }

    @Override
    public String getOperator() {
        return operator;
    }
}
