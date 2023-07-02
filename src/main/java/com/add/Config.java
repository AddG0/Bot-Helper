package com.add;


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.StandardOpenOption;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;

public class Config {
    private Config() {
    }

    private static List<String> configItems = new ArrayList<>();
    private static Dotenv dotenv;

    public static void check() {
        if (dotenv == null) {
            dotenv = new DotenvBuilder().ignoreIfMissing().load();
        }
    }

    public static String get(String key) {
        check();
        return dotenv.get(key);
    }

    public static void add(String key) {
        check();
        configItems.add(key + "=");
    }

    public static void add(String key, String defaultValue) {
        check();
        configItems.add(key + "=" + defaultValue);
    }

    public static boolean isEmpty(String key) {
        check();
        String value = dotenv.get(key);
        if (value == null) {
            return true;
        }
        return value.isEmpty();
    }

    public static void updateConfig() {
        check();
        try {
            File file = new File(".env");
            if (!file.exists() && !file.createNewFile()) {
                System.err.println("Failed to create .env file");
                return;
            }
            List<String> lines = Files.readAllLines(file.toPath());
            for (String configItem : configItems) {
                String[] split = configItem.split("=");
                String key = split[0];
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).startsWith(key)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    lines.add(configItem);
                }
            }
            Files.write(file.toPath(), lines, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}