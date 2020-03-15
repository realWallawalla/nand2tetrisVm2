package com.timonsarakinis.commands.functions;

import com.timonsarakinis.commands.Command;

public interface FunctionCommand extends Command {
    String getFunctionName();

    int getNArgs();
}
