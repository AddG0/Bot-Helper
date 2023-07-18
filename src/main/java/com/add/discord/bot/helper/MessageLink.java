package com.add.discord.bot.helper;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessageLink {
    private String link;

    public MessageLink(String link) {
        this.link = link;
    }

    public boolean isValid() {
        String regex = "https://discord\\.com/channels/\\d+/\\d+/\\d+";
        return this.link.matches(regex);
    }

    public Message getMessage() {
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid Discord message link");
        }

        String[] parts = link.split("/");
        long guildId = Long.parseLong(parts[4]);
        String channelId = parts[5];
        String messageId = parts[6];

        Guild guild = ShardHelper.getGuildById(guildId);
        if (guild == null) {
            return null;
        }

        TextChannel channel = guild.getTextChannelById("" + channelId);
        if (channel == null) {
            return null;
        }

        return channel.retrieveMessageById("" + messageId).complete();
    }
}
