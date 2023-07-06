package com.add;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

public abstract class BotAddon extends ListenerAdapter {
    public void upsertCommands(ShardManager shardManager) {
    }

    public void upsertGlobalCommands(JDA shard) {
    }
}