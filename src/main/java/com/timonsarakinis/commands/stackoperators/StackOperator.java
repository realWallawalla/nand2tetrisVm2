package com.timonsarakinis.commands.stackoperators;

import com.timonsarakinis.commands.Command;

public interface StackOperator extends Command {
    String getSegment();

    int getIndex();
}
