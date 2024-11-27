package org.hsrt.config;

import org.hsrt.config.SocketConfig;
import java.io.IOException;

public class AppConfig {
    private static SocketConfig socketConfig;

    public static void init(String host, int port) throws IOException {
        socketConfig = SocketConfig.init(host, port);
    }

    public static SocketConfig getSocketConfig() throws IOException {
        if (socketConfig == null || !socketConfig.isConnected()) {
            socketConfig = SocketConfig.getInstance();
        }
        return socketConfig;
    }
}
