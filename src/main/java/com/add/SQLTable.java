package com.add;


public interface SQLTable {
    String getName();

    Class<?> getType();

    String getTableName();

    default void createTable(long guildId) {
    }
}
