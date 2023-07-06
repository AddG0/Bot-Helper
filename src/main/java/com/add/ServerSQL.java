package com.add;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.annotation.Nonnull;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerSQL extends ListenerAdapter {
    private static Dotenv env = Dotenv.load();
    private ServerSQL() {
    }

    public static boolean createGuildDb(long guildId) {
        try {
            Connection conn = DriverManager.getConnection(env.get("SQLLINK"), env.get("SQLUSER"),
                    env.get("SQLPASS"));
            PreparedStatement createDbStatement = conn
                    .prepareStatement("CREATE DATABASE IF NOT EXISTS `" + guildId + "`");
            createDbStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to create database " + guildId + "!");
            e.printStackTrace();
            return false;
        }
        try {
            Connection conn = SQLHelper.getConnection(guildId);
            conn.setCatalog("" + guildId);
            System.out.println("Connected to database " + guildId + " successfully!");
        } catch (SQLException e) {
            System.err.println("Could not connect to database " + guildId + "!");
            e.printStackTrace();
            return false;
        }
        createTable(guildId);
        return true;
    }

    public static void deleteGuildDb(long guildId) {
        try {
            Connection conn = SQLHelper.getConnection(guildId);
            PreparedStatement createDbStatement = conn
                    .prepareStatement("DELETE FROM category WHERE category_name = '" + guildId + "';");
            createDbStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(long guildId) {
        SQLHelper.executeSQL(guildId,
                "CREATE TABLE IF NOT EXISTS serverInfo (name TEXT, info TEXT)");
    }

    public static void insertServerInfo(long guildId, String name, String description) {
        try {
            Connection conn = SQLHelper.getConnection(guildId);
            PreparedStatement insertData = conn.prepareStatement(
                    "INSERT INTO serverInfo (name, info) VALUES (?, ?)");
            insertData.setString(1, name);
            insertData.setString(2, description);
            insertData.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    public static <T> T get(long guildId, ServerOptions get) {
        try {
            String columnName = get.getName();
            Class<T> classType = (Class<T>) get.getType();
            Connection conn = SQLHelper.getConnection(guildId);
            PreparedStatement stmt = conn
                    .prepareStatement("SELECT info FROM serverInfo WHERE name = ?");
            stmt.setString(1, columnName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                T value = resultSet.getObject("info", classType);
                Objects.requireNonNull(value);
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid enum");
    }

    public static void set(long guildId, SQLTable set, Object value) {
        try {
            String columnName = set.getName();
            Connection conn = SQLHelper.getConnection(guildId);
            PreparedStatement stmt = conn
                    .prepareStatement("UPDATE serverInfo SET info = ? WHERE name = ?");
            stmt.setString(1, "" + value);
            stmt.setString(2, columnName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}