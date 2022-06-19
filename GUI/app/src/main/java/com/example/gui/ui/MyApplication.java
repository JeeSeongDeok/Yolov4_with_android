package com.example.gui.ui;

import android.app.Application;

import com.example.gui.DBManger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initDB();
    }

    private void initDB() {
        DBManger.getInstance(this, "Status", null, 1);
    }

}
