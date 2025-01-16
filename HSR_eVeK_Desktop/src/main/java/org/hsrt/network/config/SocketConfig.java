package org.hsrt.network.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SocketConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = SocketConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
