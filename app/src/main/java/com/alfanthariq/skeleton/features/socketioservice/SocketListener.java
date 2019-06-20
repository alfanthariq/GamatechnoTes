package com.alfanthariq.skeleton.features.socketioservice;

public interface SocketListener {
    void onSocketConnected();
    void onSocketDisconnected();
    void onNewMessageReceived(String jsonData);
}
