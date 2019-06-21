package com.alfanthariq.skeleton.features.socketioservice;

import alfanthariq.com.signatureapp.util.PreferencesHelper;
import android.app.*;
import android.content.*;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.alfanthariq.skeleton.R;
import com.alfanthariq.skeleton.features.main.MainActivity;
import com.alfanthariq.skeleton.utils.NetworkUtil;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

import java.util.*;

public class SocketIOService extends Service {
    private  SocketListener socketListener;
    private Boolean appConnectedToService;
    private  Socket mSocket;
    private boolean serviceBinded = false;
    private final LocalBinder mBinder = new LocalBinder();
    private List<String> channel;
    private SharedPreferences pref;

    public void setAppConnectedToService(Boolean appConnectedToService) {
        this.appConnectedToService = appConnectedToService;
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public class LocalBinder extends Binder {
       public SocketIOService getService(){
            return SocketIOService.this;
        }
    }

    public void setServiceBinded(boolean serviceBinded) {
        this.serviceBinded = serviceBinded;
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent toLaunch = new Intent(getApplicationContext(), MainActivity.class);
            toLaunch.setAction(Intent.ACTION_MAIN);
            toLaunch.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification n  = new NotificationCompat.Builder(this, "NEW_MESSAGE_NOTIF")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.notif_new_message))
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .build();
            startForeground(1000, n);
        }
        pref = this.getSharedPreferences("apps_cookies", MODE_MULTI_PROCESS);
        channel = new ArrayList<>();
        initializeSocket();
        addSocketHandlers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocketSession();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return serviceBinded;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),
                this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    private void initializeSocket() {
        try{
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.timeout = 20000;
            options.reconnectionAttempts = 100;
            SharedPreferences pref_profile = PreferencesHelper.INSTANCE.getProfilePref(getApplicationContext());
            final String token = pref_profile.getString("token", "");
            mSocket = IO.socket(NetworkUtil.INSTANCE.getSocket(),options);

            mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport)args[0];

                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            @SuppressWarnings("unchecked")
                            Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                            // modify request headers
                            headers.put("token", Collections.singletonList(token));
                        }
                    });
                }
            });
        }
        catch (Exception e){
            Log.e("Error", "Exception in socket creation");
            throw new RuntimeException(e);
        }
    }

    private void closeSocketSession(){
        mSocket.disconnect();
        mSocket.off();
    }

    private void addSocketHandlers(){
        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(SocketEventConstants.socketConnection);
                intent.putExtra("connectionStatus", true);
                broadcastEvent(intent);
                Log.d("SocketIOService", "Socket connected");
            }
        });

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(SocketEventConstants.socketConnection);
                intent.putExtra("connectionStatus", false);
                broadcastEvent(intent);
                Log.d("SocketIOService", "Socket disconnected");
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(SocketEventConstants.connectionFailure);
                broadcastEvent(intent);
                Log.d("SocketIOService", "Socket error");
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(SocketEventConstants.connectionFailure);
                broadcastEvent(intent);
                Log.d("SocketIOService", "Socket timeout");
            }
        });
        addNewMessageHandler();
    }

    public void addNewMessageHandler(){
        mSocket.off(SocketEventConstants.getMessage);
        mSocket.on(SocketEventConstants.getMessage, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Received message : "+args[0].toString());
                Toast.makeText(SocketIOService.this, args[0].toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeMessageHandler() {
        mSocket.off(SocketEventConstants.getMessage);
    }

    public void emit(String event, Object[] args, Ack ack){
        mSocket.emit(event, args, ack);
    }
    public void emit (String event, Object... args) {
        try {
            mSocket.emit(event, args,null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addOnHandler(String event, Emitter.Listener listener){
            mSocket.on(event, listener);
    }

    public void connect(){
        mSocket.connect();
    }

    public void disconnect(){
        mSocket.disconnect();
    }

    public void restartSocket(){
        mSocket.off();
        mSocket.disconnect();
        addSocketHandlers();
    }

    public void off(String event){
        mSocket.off(event);
    }

    private void broadcastEvent(Intent intent){
        Handler mHandler = new Handler(getMainLooper());
        final Intent myIntent = intent;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(myIntent);
            }
        });
    }

    public boolean isSocketConnected(){
        if (mSocket == null){
            return false;
        }
        return mSocket.connected();
    }

    public void showNotifications(){
        Intent toLaunch = new Intent(getApplicationContext(), MainActivity.class);
        toLaunch.putExtra("new_message", true);
        toLaunch.setAction(Intent.ACTION_MAIN);
        toLaunch.addCategory(Intent.CATEGORY_LAUNCHER);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        //.addParentStack(MainActivity.class)
                        .addNextIntent(toLaunch)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n  = new NotificationCompat.Builder(this, "NEW_MESSAGE_NOTIF")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_new_message))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSound(alarmSound)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(120394, n);
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }
}
