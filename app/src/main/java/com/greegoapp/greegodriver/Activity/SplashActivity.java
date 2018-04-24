package com.greegoapp.greegodriver.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private final int splashTime = 1000;
    public static boolean isRemember = false;
    public static final String PACKAGE = "com.greegoapp";

    //    ProgressBar customProgressBar;
    int progressStatusCounter = 0;
    Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getIds();
        startTimer();
    }

    private void startTimer() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {

//                    GetKeyHashValue();
//                    doProgress();

                    setSplaceScreen();

                }
            }, splashTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSplaceScreen() {
        if (SessionManager.getIsUserLoggedin(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {

            Intent in = new Intent(SplashActivity.this, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
            finish();

        }
    }

    private void getIds() {

    }

    private void doProgress() {
        try {
            while (progressStatusCounter < 100) {
                progressStatusCounter += 2;
                progressHandler.post(new Runnable() {
                    public void run() {
//                        customProgressBar.setProgress(progressStatusCounter);
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void GetKeyHashValue() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

//                ((TextView) findViewById(R.id.hashKey)).setText(Base64.encodeToString(md.digest(),Base64.NO_WRAP));

                Applog.e("HAsh", "Key:::" + Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));

            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.e("Error", e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
