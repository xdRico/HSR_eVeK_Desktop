package org.hsrt.network;

import de.ehealth.evek.api.exception.EncryptionException;
import de.ehealth.evek.api.exception.IllegalProcessException;
import de.ehealth.evek.api.network.ComClientReceiver;
import de.ehealth.evek.api.network.ComClientSender;
import de.ehealth.evek.api.network.IComClientReceiver;
import de.ehealth.evek.api.network.IComClientSender;
import de.ehealth.evek.api.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection implements IsInitializedListener {
    private int serverPort = 12013;
    private int timesToTryConnect = 15;
    private int timesTriedToConnect = 0;
    private int msToWaitWhileConnecting = 9500;
    private int msToWaitForReconnect = 500;
    private boolean isInitialized;
    private String serverAddress = "localhost";

    private IComClientReceiver receiver;
    private IComClientSender sender;
    private Socket server;
    private final List<IsInitializedListener> isInitializedListeners = new ArrayList<>();

    /**
     * Method to get the current {@link IComClientSender}
     *
     * @return {@link IComClientSender} - the current {@link IComClientSender}
     */
    IComClientSender getComClientSender(){ return sender; }

    /**
     * Method to get the current {@link IComClientReceiver}
     *
     * @return {@link IComClientReceiver} - the current {@link IComClientReceiver}
     */
    IComClientReceiver getComClientReceiver() { return receiver; }

    /**
     * Method to set the Server Address to be connected with
     *
     * @param serverAddress - String representing the Servers Address
     */
    void setServerAddress (String serverAddress){
        this.serverAddress = serverAddress;
    }

    /**
     * Method to set the Server Port to be connected with
     *
     * @param serverPort - int representing the Servers Port
     */
    void setServerPort (int serverPort){
        this.serverPort = serverPort;
    }

    /**
     * Method to get the current count of connection tries
     *
     * @return int - the count of tries to connect to the server
     */
    public int getTimesTriedToConnect(){ return timesTriedToConnect +1; }

    /**
     * Method to get the maximum amount of connection tries
     *
     * @return int - the amount of maximum tries to connect to the server
     */
    public int getTimesToTryConnect(){ return timesToTryConnect; }

    /**
     * Method to get if the Server Connection has been initialized
     *
     * @return boolean - if the Server Connection has been initialized
     */
    boolean isInitialized(){
        return isInitialized;
    }

    /**
     * Method to add {@link IsInitializedListener}'s to be called on Initialization state changes
     *
     * @param listener - the {@link IsInitializedListener} to be added
     */
    public void addIsInitializedListener(IsInitializedListener listener){
        if(!isInitializedListeners.contains(listener))
            isInitializedListeners.add(listener);
    }

    /**
     * Method to remove a {@link IsInitializedListener} from being called on Initialization state changes
     *
     * @param listener - the {@link IsInitializedListener} to be removed
     */
    public void removeIsInitializedListener(IsInitializedListener listener){
        isInitializedListeners.remove(listener);
    }

    /**
     * Method to set the Initialization state and notify the {@link IsInitializedListener}'s
     *
     * @param isInitialized - if the Connection has been successfully initialized
     */
    private void setInitialized(boolean isInitialized){
        this.isInitialized = isInitialized;
        for(IsInitializedListener listener : isInitializedListeners){
            try {
                listener.onInitializedStateChanged(isInitialized);
            }catch(NullPointerException e){
                Log.sendException(e);
            }
        }
    }

    @Override
    public void onInitializedStateChanged(boolean isInitialized) {
        Log.sendMessage(isInitialized
                ? "Network Thread has been successfully started up!"
                : "Network Thread could not be started up!");
    }

    /**
     * Method to initialize the Server connection with the currently set Network properties
     *
     * @throws IllegalStateException - thrown, when the initialization could not be finished
     */
    void initConnection() throws IllegalStateException {
        if (isInitialized)
            throw new IllegalStateException("Connection already initialized!");
        Log.sendMessage("Starting up Network Thread...");
        new Thread(() -> {

            Log.sendMessage("Trying to initialize server connection...");
            addIsInitializedListener(this);
            while (timesTriedToConnect < timesToTryConnect) {

                try {
                    server = new Socket();
                    SocketAddress endpoint = new InetSocketAddress(serverAddress, serverPort);
                    server.connect(endpoint, msToWaitWhileConnecting);
                    sender = new ComClientSender(server);
                    receiver = new ComClientReceiver(server);
                    sender.useEncryption(receiver);
                    setInitialized(true);
                    Log.sendMessage("Server connection has been successfully initialized!");
                    break;
                } catch (EncryptionException e) {
                    Log.sendException(e);
                    Log.sendMessage("Connection not encrypted!");
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    try {
                        server.close();
                    } catch (IOException ex) {
                        Log.sendMessage("Socket could not be closed!");
                    }
                    if (timesTriedToConnect >= timesToTryConnect - 1) {
                        setInitialized(false);
                        Log.sendMessage(String.format( "Server connection failed to initialize %d times!", timesToTryConnect));
                        Log.sendException(e);
                        return;
                    }
                    try {
                        timesTriedToConnect++;
                        setInitialized(false);
                        //noinspection BusyWait
                        Thread.sleep(msToWaitForReconnect);
                    } catch (InterruptedException ex) {
                        Log.sendException(ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Method to be called to close and reset the current server connection. <br>
     * The connection isn't restarted!
     *
     * @throws IllegalProcessException - thrown, when the connection could not be reset
     */
    void resetConnection() throws IllegalProcessException {
        if(!isInitialized)
            throw new IllegalProcessException("Connection not initialized!");
        try {
            server.close();
            setInitialized(false);
            isInitializedListeners.clear();
        }catch(IOException e){
            Log.sendException(e);
            throw new IllegalProcessException(e);
        }
    }
}
