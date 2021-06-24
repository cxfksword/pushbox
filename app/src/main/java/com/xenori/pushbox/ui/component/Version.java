package com.xenori.pushbox.ui.component;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Version extends androidx.appcompat.widget.AppCompatTextView {


    public Version(@NonNull Context context) {
        super(context);

        setVersion( context);
    }

    public Version(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setVersion( context);
    }

    public Version(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVersion( context);
    }

    public void setVersion(Context context) {
        this.setText("v" + getAppVersionName(context));
    }

    String getAppVersionName(@NonNull Context context){
        try{
            PackageManager packageManager=context.getPackageManager();
            return String.valueOf(packageManager.getPackageInfo(context.getPackageName(),0).versionName);
        }catch (Exception e){e.printStackTrace();}
        return "<UNKOWN>";
    }
}