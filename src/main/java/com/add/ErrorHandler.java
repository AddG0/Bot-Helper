package com.add;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ErrorHandler {
    private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class.getName());
    private static ErrorHandlerInterface errorHandler = new ErrorHandlerInterface() {
    };

    private ErrorHandler() {
    }

    public interface ErrorHandlerInterface {
        public default void onError(Guild guild, String message, Throwable e) {
        }

        public default void onError(Guild guild, String message) {
        }

        public default void onError(SlashCommandInteractionEvent event, String message, Throwable e) {
            long errorId = Helper.generateId();
            logger.error("ErrorId: ", errorId);
            logger.error("Error: {}", message, e);
            event.getChannel()
                    .sendMessage("" + message + " Error id: `" + errorId + "`. Keep this if choose to contact us!")
                    .queue();
        }

        public default void onError(SlashCommandInteractionEvent event, String message) {
            long errorId = Helper.generateId();
            logger.error("ErrorId: ", errorId);
            logger.error("Error: {}", message);
            event.getChannel()
                    .sendMessage("" + message + " Error id: `" + errorId + "`. Keep this if choose to contact us!")
                    .queue();
        }
    }

    public static void setErrorHandler(ErrorHandlerInterface errorHandler) {
        ErrorHandler.errorHandler = errorHandler;
    }

    public static void sendErrorMessage(Guild guild, String message, Throwable e) {
        errorHandler.onError(guild, message, e);
    }

    public static void sendErrorMessage(Guild guild, String message) {
        errorHandler.onError(guild, message);
    }

    public static void sendErrorMessage(SlashCommandInteractionEvent event, String message, Throwable e) {
        errorHandler.onError(event, message, e);
    }

    public static void sendErrorMessage(SlashCommandInteractionEvent event, String message) {
        errorHandler.onError(event, message);
    }

}
