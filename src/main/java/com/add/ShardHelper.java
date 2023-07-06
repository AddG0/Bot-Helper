package com.add;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ShardHelper {
    private static ShardManager shardManager;

    private ShardHelper() {
    }

    public static void setShardManager(ShardManager shardManager) {
        ShardHelper.shardManager = shardManager;
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static JDA getJDAForGuild(long guildId) {
        return shardManager.getShardById((int) (guildId >> 22));
    }

    public static boolean allShardsReady() {
        return shardManager.getStatuses().values().stream()
                .allMatch(status -> status == JDA.Status.CONNECTED);
    }
}
