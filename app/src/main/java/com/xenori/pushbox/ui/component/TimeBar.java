package com.xenori.pushbox.ui.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeBar extends androidx.appcompat.widget.AppCompatTextView {

    private Timer timer;

    public TimeBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        start();
    }

    public void start() {
        if (this.timer == null) {
            this.timer = new Timer();
            this.timer.schedule(new RefreshTimeTask(this), 0, 30000);
        }
    }

    /* access modifiers changed from: private */
    public void refresh() {
        ((Activity) getContext()).runOnUiThread(new RefreshRunner(this));
    }

    public void stop() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    class RefreshTimeTask extends TimerTask {
        final TimeBar timeBar;

        public RefreshTimeTask(TimeBar timeBar) {
            this.timeBar = timeBar;
        }

        @Override
        public void run() {
            this.timeBar.refresh();
        }
    }

    class RefreshRunner implements Runnable {

        final  TimeBar timeBar;

        public RefreshRunner(TimeBar timeBar) {
            this.timeBar = timeBar;
        }

        public void run() {
            this.timeBar.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        }
    }
}
