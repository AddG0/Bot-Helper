package com.add.discord.bot.addonmanagers;


public interface SQLTable {
    String getName();

    Class<?> getType();

    String getTableName();
}
