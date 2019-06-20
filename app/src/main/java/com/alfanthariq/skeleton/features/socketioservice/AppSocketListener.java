package com.alfanthariq.skeleton.features.socketioservice;

import android.content.*;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.socket.client.Ack;
import io.socket.emitter.Emitter;

/**
 * Created by alfanthariq on 28/05/2018.
 */

public class AppSocketListener implements SocketListener {
    private static AppSocketListener sharedInstance;
    private SocketIOService socketServiceInterface;
    public SocketListener activeSocketListener;
    public SetChannelListener channelListener;
    private Context context;

    public interface SetChannelListener {
        void onSetChannel();
    }

    public void setSetChannelListener(SetChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    public void setActiveSocketListener(SocketListener activeSocketListener) {
        this.activeSocketListener = activeSocketListener;
        if (socketServiceInterface != null && socketServiceInterface.isSocketConnected()){
            onSocketConnected();
        }
    }

    public static AppSocketListener getInstance(){
        if (sharedInstance==null){
            sharedInstance = new AppSocketListener();
        }
        return sharedInstance;
    }

    public void setContext(Context c) {
        context = c;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketServiceInterface = ((SocketIOService.LocalBinder)service).getService();
            //channelListener.onSetChannel();
            socketServiceInterface.setServiceBinded(true);
            socketServiceInterface.setSocketListener(sharedInstance);
            if (socketServiceInterface.isSocketConnected()){
                onSocketConnected();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketServiceInterface.setServiceBinded(false);
            socketServiceInterface=null;
            onSocketDisconnected();
        }
    };

    public void initialize(){
        Intent intent = new Intent(context, SocketIOService.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(context).
                registerReceiver(socketConnectionReceiver, new IntentFilter(SocketEventConstants.
                        socketConnection));
        LocalBroadcastManager.getInstance(context).
                registerReceiver(connectionFailureReceiver, new IntentFilter(SocketEventConstants.
                        connectionFailure));
        LocalBroadcastManager.getInstance(context).
                registerReceiver(newMessageReceiver, new IntentFilter(SocketEventConstants.
                        getMessage));
    }

    private BroadcastReceiver socketConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = intent.getBooleanExtra("connectionStatus",false);
            if (connected){
                onSocketConnected();
            }
            else{
                onSocketDisconnected();
            }
        }
    };

    private BroadcastReceiver connectionFailureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AppSocketListener", "connection failure");
        }
    };

    private BroadcastReceiver newMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonData = intent.getStringExtra("jsonData");
            onNewMessageReceived(jsonData);
            Log.d("AppSocketListener", "Broadcast New Message");
        }
    };

    public void destroy(){
        socketServiceInterface.setServiceBinded(false);
        context.unbindService(serviceConnection);
        LocalBroadcastManager.getInstance(context).
                unregisterReceiver(socketConnectionReceiver);
        LocalBroadcastManager.getInstance(context).
                unregisterReceiver(newMessageReceiver);
    }

    @Override
    public void onSocketConnected() {
        if (activeSocketListener != null) {
            activeSocketListener.onSocketConnected();
        }
    }

    @Override
    public void onSocketDisconnected() {
        if (activeSocketListener != null) {
            activeSocketListener.onSocketDisconnected();
        }
    }

    @Override
    public void onNewMessageReceived(String jsonData) {
        if (activeSocketListener != null) {
            activeSocketListener.onNewMessageReceived(jsonData);
        }
    }

    public void addOnHandler(String event, Emitter.Listener listener){
        socketServiceInterface.addOnHandler(event, listener);
    }

    public void emit(String event, Object[] args, Ack ack){
        socketServiceInterface.emit(event, args, ack);
    }

    public void emit (String event, Object... args){
        socketServiceInterface.emit(event, args);
    }

//    public void setChannel(List<String> channel){
//        socketServiceInterface.setChannel(channel);
//    }

//    public void joinRoom(){
//        socketServiceInterface.joinRoom();
//    }
//
//    public void leaveRoom(){
//        socketServiceInterface.leaveRoom();
//    }

    void connect(){
        socketServiceInterface.connect();
    }

    public void disconnect(){
        socketServiceInterface.disconnect();
    }

    public void off(String event) {
        if (socketServiceInterface != null) {
            socketServiceInterface.off(event);
        }
    }

    public boolean isSocketConnected(){
        if (socketServiceInterface == null){
            return false;
        }
        return socketServiceInterface.isSocketConnected();
    }

    public void setAppConnectedToService(Boolean status){
        if ( socketServiceInterface != null){
            socketServiceInterface.setAppConnectedToService(status);
        }
    }

    public void restartSocket(){
        if (socketServiceInterface != null){
            socketServiceInterface.restartSocket();
        }
    }
    public void addNewMessageHandler(){
        if (socketServiceInterface != null){
            socketServiceInterface.addNewMessageHandler();
        }
    }

    public void removeNewMessageHandler(){
        if (socketServiceInterface != null){
            socketServiceInterface.removeMessageHandler();
        }
    }
}
