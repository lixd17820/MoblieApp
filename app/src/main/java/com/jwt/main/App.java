package com.jwt.main;

import android.app.Application;
import android.os.Environment;
import android.util.Log;


import com.jwt.pojo.MyObjectBox;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;

import java.io.File;

import io.objectbox.BoxStore;

public class App extends Application {

    public static final String TAG = "ObjectBoxExample";
    public static final boolean EXTERNAL_DIR = false;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        if (EXTERNAL_DIR) {
            // Example how you could use a custom dir in "external storage"
            // (Android 6+ note: give the app storage permission in app info settings)
            File directory = new File(Environment.getExternalStorageDirectory(), "objectbox-notes");
            boxStore = MyObjectBox.builder().androidContext(App.this).directory(directory).build();
        } else {
            // This is the minimal setup required on Android
            boxStore = MyObjectBox.builder().androidContext(App.this).build();
        }
        GlobalMethod.parseZdgzVehCbz(this);
        Log.e("Application", "重点关注车辆：" + GlobalSystemParam.bjVehSet.size() + "，重点关注地点：" + GlobalSystemParam.bjCbzSet.size());
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
