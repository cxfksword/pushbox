package com.xenori.pushbox.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.xenori.pushbox.GlobalApplication;

public class StartReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Logger.i(">>>  Receive msg:" + intent.getAction());
            GlobalApplication.startBackgroundService(context);
        }
    }

}