package com.add;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSQL {
    private static Logger logger = LoggerFactory.getLogger(ServerSQL.class.getName());

    private ServerSQL() {
    }

    public static void createTable(long guildId) {
        try {
            SQLHelper.executeSQL(guildId,
                    "CREATE TABLE IF NOT EXISTS serverInfo (name TEXT, info TEXT)");
        } catch (SQLException e) {
            logger.error("Error creating table", e);
        }
    }

    public static void insertServerInfo(long guildId, String name, String description) {
        try {
            SQLHelper.executeSQL(guildId,
                    "INSERT INTO serverInfo (name, info) VALUES (" + name + ", " + description + ")");
        } catch (SQLException e) {
            logger.error("Error inserting server info", e);
        }
    }

    public static void set(long guildId, String name, String description) {
        try {
            SQLHelper.executeSQL(guildId,
                    "UPDATE serverInfo SET info = " + description + " WHERE name = " + name);
        } catch (SQLException e) {
            logger.error("Error setting server info", e);
        }
    }

    public static String get(long guildId, String name) {
        try (Connection conn = SQLHelper.getConnection(guildId)) {
            try (PreparedStatement statement = conn
                    .prepareStatement("SELECT info FROM serverInfo WHERE name = " + name)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("info");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting server info", e);
        }
        return null;
    }
}
