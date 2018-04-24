package com.greegoapp.greegodriver.Utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Android on 28-Feb-18.
 */

public class MyProgressDialog {
    private static ProgressDialog progressBar;

    public static void showProgressDialog(Context context) {
        progressBar = new ProgressDialog(context);
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.show();
    }

    public static void hideProgressDialog() {
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }

    }
}
