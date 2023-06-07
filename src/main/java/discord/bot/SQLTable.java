package discord.bot;


public interface SQLTable {
    String getName();

    Class<?> getType();

    String getTableName();
}
