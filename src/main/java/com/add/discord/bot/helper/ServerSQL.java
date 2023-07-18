package com.add.discord.bot.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.add.discord.bot.addonmanagers.SQLTable;
import com.add.discord.bot.addonmanagers.annotations.sql.CreateTable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSQL {
    private static Logger logger = LoggerFactory.getLogger(ServerSQL.class.getName());
    private static ConcurrentHashMap<String, Class<?>> classTypes = new ConcurrentHashMap<>();
    private static final String TABLE = "serverInfo";

    private ServerSQL() {
    }

    @CreateTable(name = TABLE)
    public static void createTable(long guildId) {
        try {
            SQLHelper.executeSQL(guildId,
                    "CREATE TABLE IF NOT EXISTS serverInfo (name TEXT, info TEXT)");
        } catch (SQLException e) {
            logger.error("Error creating table", e);
        }
    }

    public static void insertServerInfo(long guildId, String name, Object info) {
        try (Connection conn = SQLHelper.getConnection(guildId)) {
            PreparedStatement statement = conn
                    .prepareStatement("INSERT INTO serverInfo (name, info) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setObject(2, info);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting server info", e);
        }
    }

    public static void set(long guildId, String name, Object info) {
        try (Connection conn = SQLHelper.getConnection(guildId)) {
            PreparedStatement statement = conn
                    .prepareStatement("UPDATE serverInfo SET info = ? WHERE name = ?");
            statement.setString(1, "" + info);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error setting server info", e);
        }
    }

    public static String get(long guildId, String name) {
        try (Connection conn = SQLHelper.getConnection(guildId)) {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT info FROM serverInfo WHERE name = ?");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("info");
            }
        } catch (SQLException e) {
            logger.error("Error getting server info", e);
        }
        return null;
    }
}
