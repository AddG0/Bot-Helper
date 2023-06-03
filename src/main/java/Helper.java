package discord.bot;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import discord.bot.InviteLoggerSystems.InviteListSQL;
import discord.bot.LevelSystems.LevelHelper;
import discord.bot.LevelSystems.UserLevelSQL;
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

public class Helper extends ListenerAdapter {

    public static Message getMessageFromLink(Guild guild, String messageLink) {
        String[] splitLink = messageLink.split("/");
        if (splitLink.length != 7) {
            return null;
        }
        String messageId = splitLink[splitLink.length - 1];
        TextChannel channel = guild.getTextChannelById( "" + splitLink[splitLink.length - 2]);
        if (channel == null) {
            return null;
        }
        return channel.retrieveMessageById("" + messageId).complete();
    }

    public static void sendErrorMessage(Guild guild, String message) {
        TextChannel errorChannel = guild
                .getTextChannelById("" + ServerSQL.get(guild.getIdLong(), ServerOptions.BOT_ERROR_CHANNEL));
        if (errorChannel != null && errorChannel.canTalk()) {
            errorChannel.sendMessage("" + message).queue();
        }
    }

    public static void sendErrorMessage(SlashCommandInteractionEvent event, String message) {
        long errorId = Helper.generateId();
        System.err.println("Error id: " + errorId);
        event.reply("" + message + " Error id: `" + errorId + "`. Keep this if choose to contact us!").setEphemeral(true).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }
        switch (event.getComponentId()) {
            case "leaderboard_left":
            case "leaderboard_right":
            case "leaderboard_switch":
                break;
            default:
                return;
        }
        long guildId = guild.getIdLong();
        MessageEmbed oldEmbed = event.getMessage().getEmbeds().get(0);
        String title = oldEmbed.getTitle();
        if (title == null) {
            return;
        }
        String description = oldEmbed.getDescription();
        if (description == null) {
            return;
        }
        int starIndex = description.indexOf("*", 2);
        int start = 0;
        if (starIndex != -1) {
            start = Integer.parseInt(description.substring(2, starIndex - 1)) - 1;
        }
        switch (event.getComponentId()) {
            case "leaderboard_left":
                start -= 10;
                break;
            case "leaderboard_right":
                start += 10;
                break;
            case "leaderboard_switch":
                switch (title) {
                    case "Level Leaderboard":
                        title = "Invite Leaderboard";
                        start = 0;
                        break;
                    case "Invite Leaderboard":
                        title = "Level Leaderboard";
                        start = 0;
                        break;
                    default: // This should never happen
                        return;
                }
                break;
            default: // This should never happen
                return;
        }
        List<Button> buttons = new ArrayList<>();
        if (start - 10 >= 0) {
            buttons.add(Button.primary("leaderboard_left", Emoji.fromUnicode("◀")));
        }
        buttons.add(Button.primary("leaderboard_switch", Emoji.fromUnicode("🔄")));

        EmbedBuilder embed = null;
        String serverIcon = guild.getIconUrl();
        switch (title) {
            case "Level Leaderboard":
                if (start + 10 < UserLevelSQL.getUserCount(guildId)) {
                    buttons.add(Button.primary("leaderboard_right", Emoji.fromUnicode("▶")));
                }
                embed = getLeaderBoardEmbed(guild, "level", serverIcon, start, start + 10);
                break;
            case "Invite Leaderboard":
                if (start + 10 < InviteListSQL.getInviteUserCounts(guildId)) {
                    buttons.add(Button.primary("leaderboard_right", Emoji.fromUnicode("▶")));
                }
                embed = getLeaderBoardEmbed(guild, "invite", serverIcon, start, start + 10);
                break;
            default: // This should never happen
                return;
        }
        event.editMessageEmbeds(embed.build()).setActionRow(buttons).queue();
    }

    public static EmbedBuilder getLeaderBoardEmbed(Guild guild,
            String leaderBoard,
            String serverIcon,
            int start, int end) {
        long guildId = guild.getIdLong();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(serverIcon);
        embed.setTimestamp(Instant.now());
        StringBuilder description = new StringBuilder();
        switch (leaderBoard) {
            case "level":
                embed.setTitle("Level Leaderboard");
                List<List<Object>> sortedXp = UserLevelSQL.getSortedUserLevels(guildId);
                System.out.println("start");
                for (int i = start; i < end && i < sortedXp.size(); i++) {
                    System.out.println(
                            sortedXp.get(i) + " " + sortedXp.get(i).get(0) + " " + sortedXp.get(i).get(1) + " " + i);
                    long userId = (long) sortedXp.get(i).get(0);
                    BigInteger userXp = new BigInteger((String) sortedXp.get(i).get(1));
                    description.append(
                            "**" + (i + 1) + ":** "
                                    + SQLHelper.get(guildId, UserLevelSQL.NAME, UserLevelSQL.USERID, userId)
                                    + " - Level "
                                    + LevelHelper.getLevel(userXp) + "\n");
                }
                embed.setFooter("Page " + (start / 10 + 1) + "/" + (sortedXp.size() / 10 + 1));
                break;
            case "invite":
                embed.setTitle("Invite Leaderboard");
                List<Long> sortedUsers = InviteListSQL.getSortedInvites(guildId);
                for (int i = start; i < end && i < sortedUsers.size(); i++) {
                    long userId = sortedUsers.get(i);
                    int uses = 0;
                    int bonus = 0;
                    int fake = 0;
                    int leaves = 0;
                    for (String inviteCode : InviteListSQL.getInviteCodesFromUser(guildId, userId)) {
                        uses += (int) SQLHelper.get(guildId, InviteListSQL.USES, InviteListSQL.INVITECODE, inviteCode);
                        bonus += (int) SQLHelper.get(guildId, InviteListSQL.BONUS, InviteListSQL.INVITECODE,
                                inviteCode);
                        fake += (int) SQLHelper.get(guildId, InviteListSQL.FAKE, InviteListSQL.INVITECODE, inviteCode);
                        leaves += (int) SQLHelper.get(guildId, InviteListSQL.LEAVES, InviteListSQL.INVITECODE,
                                inviteCode);
                    }
                    int total = uses + bonus;
                    description.append("**" + (i + 1) + ":** "
                            + SQLHelper.get(guildId, InviteListSQL.INVITERNAME, InviteListSQL.INVITERID, userId) + " - "
                            + total
                            + " invites (" + uses + " uses, " + bonus + " bonus, " + fake + " fake, " + leaves
                            + " leaves)\n");
                    embed.setFooter("Page " + (start / 10 + 1) + "/" + (sortedUsers.size() / 10 + 1));
                }
                break;
        }
        if (description.length() == 0) {
            description.append("No users found");
        }
        embed.setDescription(description);
        return embed;
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
