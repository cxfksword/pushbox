package com.xenori.pushbox;

import android.content.Intent;
import android.os.Bundle;


public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_welcome);

        GlobalApplication.startBackgroundService(this);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
