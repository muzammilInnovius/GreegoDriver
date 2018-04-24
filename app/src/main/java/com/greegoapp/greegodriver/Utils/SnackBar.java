package com.greegoapp.greegodriver.Utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.greegoapp.greegodriver.R;


/**
 * Created by Android on 29-Jan-18.
 */

public class SnackBar {


    public static void showInternetError(Context context, View view) {
        Snackbar snackbar = Snackbar
                .make(view, "Please check your internet connection.", Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.warning_bg));
        snackbar.show();
    }

    public static void showValidationError(Context context, View view, String msg) {
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.warning_bg));
//        snackbarView.setBackgroundColor(Color.RED);
        snackbar.show();
    }


    public static void showError(Context context, View view, String msg) {
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.error_bg));
        snackbar.show();
    }

    public static void showSuccess(Context context, View view, String msg) {
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.success_bg));
        snackbar.show();
    }


}
