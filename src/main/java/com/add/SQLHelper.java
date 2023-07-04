package com.add;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLHelper {
    private static Connection conn = null;

    private static HikariDataSource ds;

    private SQLHelper() {
    }

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Config.get("SQLLINK"));
        config.setUsername(Config.get("SQLUSER"));
        config.setPassword(Config.get("SQLPASS"));
        config.setMaximumPoolSize(1000); // adjust this value according to your needs
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection(long guildId) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setCatalog("" + guildId);
        } catch (SQLException e) {
            throw e;
        }
        return conn;
    }

    private static int getMaxConnections() throws SQLException {
        try (Connection connection = DriverManager.getConnection(Config.get("SQLLINK"), Config.get("SQLUSER"),
                Config.get("SQLPASS"))) {
            try (PreparedStatement statement = connection.prepareStatement("SHOW VARIABLES LIKE 'max_connections'")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("Value");
                    }
                }
            }
        }
        throw new SQLException("Failed to retrieve max_connections");
    }

    private static int getCurrentConnections() throws SQLException {
        try (Connection connection = DriverManager.getConnection(Config.get("SQLLINK"), Config.get("SQLUSER"),
                Config.get("SQLPASS"))) {
            try (PreparedStatement statement = connection.prepareStatement("SHOW PROCESSLIST")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    int count = 0;
                    while (resultSet.next()) {
                        count++;
                    }
                    return count;
                }
            }
        }
    }

    public static void executeSQL(long guildId, String sql) {
        try {
            Connection conn = getConnection(guildId);
            conn.setCatalog("" + guildId);
            conn.createStatement().execute(sql);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T get(long guildId, SQLTable enumGet, SQLTable enumFrom, Object where) {
        try {
            String tableName = enumGet.getTableName();
            String columnName = enumGet.getName();
            Class<T> classType = (Class<T>) enumGet.getType();
            String whereColumnName = enumFrom.getName();
            Connection conn = SQLHelper.getConnection(guildId);
            PreparedStatement stmt = conn
                    .prepareStatement(
                            "SELECT " + columnName + " FROM " + tableName + " WHERE " + whereColumnName + " = ?");
            stmt.setObject(1, where);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getObject(columnName, classType);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid enum");
    }

    public static void update(long guildId, SQLTable enumSet, SQLTable enumWhere,
            Object where, Object newValue) {
        try {
            String tableName = enumSet.getTableName();
            String columnName = enumSet.getName();
            String whereColumnName = enumWhere.getName();
            Connection conn = SQLHelper.getConnection(guildId);
            PreparedStatement stmt = conn
                    .prepareStatement(
                            "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " +
                                    whereColumnName + " = ?");
            stmt.setObject(1, newValue);
            stmt.setObject(2, where);
            stmt.executeUpdate();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid enum");
    }
}

class SQLConnectionFailed extends RuntimeException {
    public SQLConnectionFailed(String message) {
        super(message);
    }
}