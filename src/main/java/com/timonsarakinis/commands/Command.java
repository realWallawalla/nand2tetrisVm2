package com.timonsarakinis.commands;

public interface Command {
    CommandType getCommandType();

    String getOperator();
}
