package com.add;


public interface SQLTable {
    String getName();

    Class<?> getType();

    String getTableName();

    static void createTable(long guildId) {
    }
}
