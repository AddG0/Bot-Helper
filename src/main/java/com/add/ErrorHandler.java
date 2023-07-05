package com.add;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ErrorHandler {
    private static ErrorHandlerInterface errorHandler = new ErrorHandlerInterface() {
    };

    private ErrorHandler() {
    }

    public interface ErrorHandlerInterface {
        public default void onError(Guild guild, String message) {
            TextChannel errorChannel = guild
                    .getTextChannelById("" + ServerSQL.get(guild.getIdLong(), ServerOptions.BOT_ERROR_CHANNEL));
            if (errorChannel != null && errorChannel.canTalk()) {
                errorChannel.sendMessage("" + message).queue();
            }
        }

        public default void onError(SlashCommandInteractionEvent event, String message) {
            long errorId = Helper.generateId();
            System.err.println("Error id: " + errorId);
            event.reply("" + message + " Error id: `" + errorId + "`. Keep this if choose to contact us!")
                    .setEphemeral(true).queue();
        }
    }

    public static void setErrorHandler(ErrorHandlerInterface errorHandler) {
        ErrorHandler.errorHandler = errorHandler;
    }

    public static void sendErrorMessage(Guild guild, String message) {
        errorHandler.onError(guild, message);
    }

    public static void sendErrorMessage(SlashCommandInteractionEvent event, String message) {
        errorHandler.onError(event, message);
    }

}
