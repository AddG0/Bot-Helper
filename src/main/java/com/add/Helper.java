package com.add;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Helper {

    public static Message getMessageFromLink(Guild guild, String messageLink) {
        String[] splitLink = messageLink.split("/");
        if (splitLink.length != 7) {
            return null;
        }
        String messageId = splitLink[splitLink.length - 1];
        TextChannel channel = guild.getTextChannelById("" + splitLink[splitLink.length - 2]);
        if (channel == null) {
            return null;
        }
        return channel.retrieveMessageById("" + messageId).complete();
    }

    public static long generateId() {
        return Long.parseLong(String.format("%019d", Math.abs(new Random().nextLong())));
    }

    public static Guild getGuildOnCommandEvent(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("This command can only be used in a server").queue();
            return null;
        }
        return guild;
    }

    public static String getDurationFromMilli(long timeInMilli) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMilli);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMilli);
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMilli);
        long days = TimeUnit.MILLISECONDS.toDays(timeInMilli);
        long months = days / 30;
        long years = days / 365;
        long decades = years / 10;
        long centuries = decades / 10;
        String output = "";
        if (seconds < 60) {
            output = seconds + (seconds == 1 ? " second" : " seconds");
        } else if (minutes < 60) {
            output = minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (hours < 24) {
            output = hours + (hours == 1 ? " hour" : " hours");
        } else if (days < 30) {
            output = days + (days == 1 ? " day" : " days");
        } else if (months < 12) {
            output = months + (months == 1 ? " month" : " months");
        } else if (years < 10) {
            output = years + (years == 1 ? " year" : " years");
        } else if (decades < 10) {
            output = decades + (decades == 1 ? " decade" : " decades");
        } else {
            output = centuries + (centuries == 1 ? " century" : " centuries");
        }
        return output;
    }

    public static String getJoinDuration(long timeInMilli) {
        long timeDifference = System.currentTimeMillis() - timeInMilli;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifference);
        long months = days / 30;
        long years = days / 365;
        long decades = years / 10;
        long centuries = decades / 10;
        String output = "";
        if (seconds < 60) {
            output = seconds + (seconds == 1 ? " second" : " seconds");
        } else if (minutes < 60) {
            output = minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (hours < 24) {
            output = hours + (hours == 1 ? " hour" : " hours");
        } else if (days < 30) {
            output = days + (days == 1 ? " day" : " days");
        } else if (months < 12) {
            output = months + (months == 1 ? " month" : " months");
        } else if (years < 10) {
            output = years + (years == 1 ? " year" : " years");
        } else if (decades < 10) {
            output = decades + (decades == 1 ? " decade" : " decades");
        } else {
            output = centuries + (centuries == 1 ? " century" : " centuries");
        }
        return output;
    }

    public static String getJoinDurationAgo(long timeInMilli) {
        return getJoinDuration(timeInMilli) + " ago";
    }

    public static long getTimeInMilliFromDruation(String duration) {
        long time = Long.parseLong(duration.substring(0, duration.length() - 1));
        long timeInMilli = 0;
        String[] timeArray = duration.split(" ");
        if (timeArray.length > 1) {
            for (String timeString : timeArray) {
                timeInMilli += getTimeInMilliFromDruation(timeString);
            }
            return timeInMilli;
        }
        switch (duration.substring(duration.length() - 1)) {
            case "d":
                return TimeUnit.DAYS.toMillis(time);
            case "h":
                return TimeUnit.HOURS.toMillis(time);
            case "m":
                return TimeUnit.MINUTES.toMillis(time);
            case "w":
                return TimeUnit.DAYS.toMillis(time * 7);
            case "y":
                return TimeUnit.DAYS.toMillis(time * 365);
            default:
                return TimeUnit.SECONDS.toMillis(time);
        }
    }
}
