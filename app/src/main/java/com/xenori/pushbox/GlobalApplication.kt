package com.xenori.pushbox

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.xenori.pushbox.dlna.OboxRendererService
import com.xenori.pushbox.misc.SharedPref
import com.xenori.pushbox.system.StartReceiver
import java.util.logging.Level


class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        java.util.logging.Logger.getLogger("org.fourthline.cling").level = Level.FINEST;
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .tag(applicationContext.packageName) // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        SharedPref.init(applicationContext)
        registerReceiver()
    }

    fun registerReceiver() {
        var mIntentFilter = IntentFilter()
        mIntentFilter.addAction("android.intent.action.SCREEN_ON")
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        var starReceiver = StartReceiver()
        registerReceiver(starReceiver, mIntentFilter)
    }

    companion object {
        @JvmStatic
        fun startBackgroundService(context: Context) {
            OboxRendererService.startService(context)
            //DLNARendererService.startService(context);
        }
    }

}