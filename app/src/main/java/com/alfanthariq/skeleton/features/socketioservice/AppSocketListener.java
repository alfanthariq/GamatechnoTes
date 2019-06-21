package com.alfanthariq.skeleton.features.socketioservice;

import alfanthariq.com.signatureapp.util.PreferencesHelper;
import android.app.PendingIntent;
import android.content.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.alfanthariq.skeleton.data.local.AppDatabase;
import com.alfanthariq.skeleton.data.model.Conversation;
import com.alfanthariq.skeleton.data.model.Messages;
import com.alfanthariq.skeleton.features.main.MainActivity;
import com.alfanthariq.skeleton.utils.NotificationUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.socket.client.Ack;
import io.socket.emitter.Emitter;

import java.util.List;

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
            JsonObject json = new JsonParser().parse(jsonData).getAsJsonObject();
            JsonObject obj = json.get("gtfwResult").getAsJsonObject();
            JsonObject data = obj.get("data").getAsJsonObject();
            int from = data.get("from_user_id").getAsInt();
            SharedPreferences pref = PreferencesHelper.INSTANCE.getProfilePref(context);
            int to = pref.getInt("user_id", -1);
            String message = data.get("from_message").getAsString();
            String time = data.get("timestamp").getAsString();

            Intent pInt = new Intent(context, MainActivity.class);
            NotificationUtils.INSTANCE.showNotificationMessage(context, "New Message", message, "", pInt, true);

            final AppDatabase db = AppDatabase.Companion.getInstance(context);
            Messages msg = new Messages();
            msg.setMessage(message);
            msg.setSender_id(from);
            msg.setUser_id(to);
            msg.setMessage_time(time);

            new AsyncTask<Messages, Integer, Integer>() {
                @Override
                protected Integer doInBackground(Messages... message) {
                    List<Conversation> conv = db.conversationDAO().bySender(message[0].getSender_id());
                    if (conv.size() > 0) {
                        int convId = conv.get(0).getId();
                        message[0].setConversation_id(convId);
                        db.MessageDAO().insert(message[0]);
                    } else {
                        Conversation c = new Conversation();
                        c.setSender_id(message[0].getSender_id());
                        c.setUser_id(message[0].getUser_id());

                        Long co = db.conversationDAO().insert(c);
                        int convId = co.intValue();
                        message[0].setConversation_id(convId);
                        db.MessageDAO().insert(message[0]);
                    }
                    return 0;
                }
            }.execute(msg);
            Log.d("AppSocketListener", "Broadcast New Message "+jsonData);
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

    public void connect(){
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
