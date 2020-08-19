package com.tyctak.canalmap;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static MyApp  instance;
    public MyApp() { instance = this; }
    public static Context getContext() { return instance; }
}