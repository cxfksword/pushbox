package com.xenori.pushbox

import android.app.Activity
import android.os.Bundle

open class BaseActivity : Activity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.requestFeature(Window.FEATURE_SWIPE_TO_DISMISS)
    }

}