package org.hsrt.config;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SocketConfig {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private static SocketConfig instance;

    private SocketConfig(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }
    public static synchronized SocketConfig getInstance(String host, int port) throws IOException {
        if (instance == null) {
            instance = new SocketConfig(host, port);
        }
        return instance;
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
}