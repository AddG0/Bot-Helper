package com.add;

import net.dv8tion.jda.api.JDA;

public interface BotAddon {
    public void upsertCommands(JDA jda);

    public default void onReady(JDA jda) {
    }
}
