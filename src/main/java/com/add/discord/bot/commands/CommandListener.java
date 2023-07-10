package com.add.discord.bot.commands;

import java.util.HashSet;
import java.util.Set;

public interface CommandListener {
    static Set<Command> commands = new HashSet<>();
    
    default void registerCommand(Command command) {
        commands.add(command);
    }

    default void unregisterCommand(Command command) {
        commands.remove(command);
    }

    default Set<Command> getCommands() {
        return commands;
    }

    void registerCommands();
}
