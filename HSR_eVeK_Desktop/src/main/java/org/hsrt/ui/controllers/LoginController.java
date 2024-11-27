package org.hsrt.ui.controllers;

import org.hsrt.config.AppConfig;
import org.hsrt.config.SocketConfig;
import org.hsrt.database.models.User;
import org.hsrt.services.SocketListener;

import java.io.IOException;
import java.net.Socket;

public class LoginController {
    public static User login(String username, String password) {
        try {
            SocketConfig socket = AppConfig.getSocketConfig();
            socket.sendAuthentication(username, password);
            SocketListener socketListener = new SocketListener(socket);
            new Thread(socketListener).start();
            socketListener.run();
            return socketListener.getAuthenticatedUser();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
