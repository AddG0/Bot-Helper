package com.add;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

public class SQLHelper {
    private static Logger logger = LoggerFactory.getLogger(SQLHelper.class.getName());
    private static Dotenv env = Dotenv.load();
    private static Connection conn = null;

    private static HikariDataSource ds;

    private SQLHelper() {
    }

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.get("SQLLINK"));
        config.setUsername(env.get("SQLUSER"));
        config.setPassword(env.get("SQLPASS"));
        try {
            config.setMaximumPoolSize(getMaxConnections());
        } catch (SQLException e) {
            logger.error("Error getting max connections", e);
        }
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection(long guildId) throws SQLException {
        Connection conn = ds.getConnection();
        conn.setCatalog("" + guildId);
        return conn;
    }

    private static int getMaxConnections() throws SQLException {
        try (Connection connection = DriverManager.getConnection(env.get("SQLLINK"), env.get("SQLUSER"),
                env.get("SQLPASS"))) {
            PreparedStatement statement = connection.prepareStatement("SHOW VARIABLES LIKE 'max_connections'");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Value");
                }
            }
        }
        throw new SQLException("Failed to retrieve max_connections");
    }

    private static int getCurrentConnections() throws SQLException {
        try (Connection connection = DriverManager.getConnection(env.get("SQLLINK"), env.get("SQLUSER"),
                env.get("SQLPASS"))) {
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

    public static void executeSQL(long guildId, String sql) throws SQLException {
        Connection conn = getConnection(guildId);
        conn.createStatement().execute(sql);
        conn.close();
    }

    public static <T> T get(long guildId, SQLTable enumGet, SQLTable enumFrom, Object where) {
        String tableName = enumGet.getTableName();
        String columnName = enumGet.getName();
        Class<T> classType = (Class<T>) enumGet.getType();
        String whereColumnName = enumFrom.getName();
        try (Connection conn = SQLHelper.getConnection(guildId)) {
            PreparedStatement stmt = conn
                    .prepareStatement(
                            "SELECT " + columnName + " FROM " + tableName + " WHERE " + whereColumnName + " = ?");
            stmt.setObject(1, where);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getObject(columnName, classType);
            }
        } catch (SQLException e) {
            logger.error("Error getting from SQL", e);
        }
        throw new IllegalArgumentException("Invalid enum");
    }

    public static void update(long guildId, SQLTable enumSet, SQLTable enumWhere,
            Object where, Object newValue) {
        String tableName = enumSet.getTableName();
        String columnName = enumSet.getName();
        String whereColumnName = enumWhere.getName();
        try {
            executeSQL(guildId, "UPDATE " + tableName + " SET " + columnName + " = " + newValue + " WHERE " +
                    whereColumnName + " = " + where);
        } catch (SQLException e) {
            logger.error("Error updating SQL", e);
        }
        throw new IllegalArgumentException("Invalid enum");
    }
}

class SQLConnectionFailed extends RuntimeException {
    public SQLConnectionFailed(String message) {
        super(message);
    }
}