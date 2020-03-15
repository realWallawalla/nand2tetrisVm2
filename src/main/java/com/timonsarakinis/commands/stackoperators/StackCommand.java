package com.timonsarakinis.commands.stackoperators;

import com.timonsarakinis.commands.Command;

public interface StackCommand extends Command {
    String getSegment();

    int getIndex();
}
