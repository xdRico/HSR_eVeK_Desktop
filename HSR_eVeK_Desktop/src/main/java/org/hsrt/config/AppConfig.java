package org.hsrt.config;

import org.hsrt.config.SocketConfig;
import java.io.IOException;

public class AppConfig {
    private static SocketConfig socketConfig;

    public static void init(String host, int port) throws IOException {
        socketConfig = SocketConfig.getInstance(host, port);
    }

    public static SocketConfig getSocketConfig(String host, int port) throws IOException {
        if (socketConfig == null || !socketConfig.isConnected()) {
            socketConfig = SocketConfig.getInstance(host, port);
        }
        return socketConfig;
    }
}
