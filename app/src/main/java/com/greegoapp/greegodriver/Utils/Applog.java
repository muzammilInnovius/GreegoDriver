package com.greegoapp.greegodriver.Utils;

import android.util.Log;

public class Applog {

    public static void E(String msg) {

        Log.e("GreegoApp", msg);

    }

    public static void e(String tag, String msg) {

        Log.e(tag, msg);

    }

    public static void d(String tag, String msg) {

        Log.d(tag, msg);

    }

    public static void v(String tag, String msg) {

        Log.v(tag, msg);

    }

    public static void i(String tag, String msg) {

        Log.i(tag, msg);

    }
}
