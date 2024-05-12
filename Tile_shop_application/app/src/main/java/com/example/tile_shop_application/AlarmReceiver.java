package com.example.tile_shop_application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationHandler(context).send("Gyorsan fogynak a csempék, ne maradj le róluk!");
    }
}