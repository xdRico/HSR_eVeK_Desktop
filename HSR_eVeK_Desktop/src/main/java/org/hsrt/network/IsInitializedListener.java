package org.hsrt.network;

/**
 * Class used for Listeners listening for changes at Initialization <br>
 * Must be added to DataHandler as Listener!
 */
public interface IsInitializedListener{

    /**
     * Called when Initialization state changes
     *
     * @param isInitialized - boolean representing if Initialization has been successful
     */
    void onInitializedStateChanged(boolean isInitialized);
}