package org.hsrt.config;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SocketConfig {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private static SocketConfig instance;
    private static String host;
    private static int port;

    private SocketConfig(String newHost, int newPort) throws IOException {
        host = newHost;
        port = newPort;
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        instance = this;
    }
    public static synchronized SocketConfig getInstance() throws IOException {
        if (instance == null) {
            instance = new SocketConfig(host, port);
        }
        return instance;
    }

    public static SocketConfig init(String host, int port) {
        try {
            return new SocketConfig(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendObject(Object obj) throws IOException {
        if (!isConnected()) {
            throw new SocketException("Connection lost");
        }
        outputStream.writeObject(obj);
        outputStream.flush();
    }

    public Object receiveObject() throws IOException, ClassNotFoundException{
        if (!isConnected()) {
            throw new SocketException("Connection lost");
        }
        return inputStream.readObject();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    public void sendAuthentication(String username, String password) throws IOException {
        if (!isConnected()) {
            throw new SocketException("Connection lost");
        }
        outputStream.writeObject("AUTH");
        outputStream.writeObject(username);
        outputStream.writeObject(password);
        outputStream.flush();
    }
}