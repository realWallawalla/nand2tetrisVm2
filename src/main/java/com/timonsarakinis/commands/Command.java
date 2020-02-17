package com.timonsarakinis.commands;

public interface Command {
    CommandType getCommandType();

    String getOperation();

    String getSegment();

    int getIndex();
}
