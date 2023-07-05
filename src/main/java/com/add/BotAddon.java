package com.add;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class BotAddon extends ListenerAdapter {
    public abstract void upsertCommands(JDA jda);
}
