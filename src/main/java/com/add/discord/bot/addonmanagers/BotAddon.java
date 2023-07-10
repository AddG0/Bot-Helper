package com.add.discord.bot.addonmanagers;


import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.add.discord.bot.helper.ShardHelper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;

public abstract class BotAddon extends ListenerAdapter {
    private static Logger logger = LoggerFactory.getLogger(BotAddon.class.getName());

    public void upsertCommands(ShardManager shardManager) {
    }

    public void upsertGlobalCommands(@Nonnull CommandData... commands) {
        for (JDA shard : ShardHelper.getShardManager().getShards()) {
            for (@Nonnull CommandData command : commands) {
                try {
                shard.upsertCommand(command).queue();
                } catch (Exception e) {
                    logger.error("Error upserting global command: " + command.getName(), e);
                }
            }
        }
    }
}