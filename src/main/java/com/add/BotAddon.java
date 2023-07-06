package com.add;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;

public abstract class BotAddon extends ListenerAdapter {
    public void upsertCommands(ShardManager shardManager) {
    }

    public void upsertGlobalCommands(ShardManager shardManager, CommandData commands) {
        if (commands == null)
            return;
        for (JDA jda : shardManager.getShards()) {
            jda.upsertCommand(commands).queue();
        }
    }
}
