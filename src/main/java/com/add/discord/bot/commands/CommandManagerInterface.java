package com.add.discord.bot.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.add.discord.bot.addonmanagers.BotAddon;
import com.add.discord.bot.addonmanagers.annotations.RunOnStart;
import com.add.discord.bot.helper.ShardHelper;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface CommandManagerInterface {
    Map<String, Command> commands = new HashMap<>();

    default void addCommand(Command cmd) {
        String commandName = cmd.getCommand().getName();
        if (commands.containsKey(commandName)) {
            throw new IllegalArgumentException("Command already exists: " + commandName);
        }
        commands.put(commandName, cmd);
    }

    default CommandData[] getCommands() {
        return commands.values().stream().map(Command::getCommand).toArray(CommandData[]::new);
    }

    default void updateCommands(Guild guild) {
        CommandData[] commands = getCommands();
        if (commands.length != 0)
            guild.updateCommands().addCommands(commands).queue();
    }

    default void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        Command command = commands.get(commandName);
        if (command == null) {
            return;
        }
        Guild guild = event.getGuild();
        command.onCommand(guild, event);
    }

    @RunOnStart
    default void upsertCommands() {
        CommandData[] commands = getCommands();
        if (commands != null) {
            ShardHelper.getShardManager().getShards().forEach(shard -> {
                shard.updateCommands().addCommands(commands).queue();
            });
        }
    }

}
