package com.alfanthariq.skeleton.features.socketioservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by alfanthariq on 28/05/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BootReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        Intent serviceIntent = new Intent(context, SocketIOService.class);
        context.startService(serviceIntent);
    }
}
