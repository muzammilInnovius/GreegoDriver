package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private final int splashTime = 1000;
    public static boolean isRemember = false;
    public static final String PACKAGE = "com.greegoapp";

    //    ProgressBar customProgressBar;
    int progressStatusCounter = 0;
    Handler progressHandler = new Handler();
    SmsVerifyCatcher smsVerifyCatcher;
    public static int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Bugsnag.init(this);
        //  Log.d("MyCustomFCMService",getSharedPreferences("notification",0).getString("flag",""));
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("request_id") != null) {
/*
            for (String key : getIntent().getExtras().keySet()) {
*/
                String value = getIntent().getExtras().getString("request_id");
                Intent intent = new Intent(getApplicationContext(), AcceptUserRequestActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /* String string = json.toString();
                string = string.substring(string.indexOf(":") + 1, string.length() - 1);
                intent.putExtra("message", string);
                */
                //google.sent_time

                /* if(key.equals("request_id")) {
                 */

                intent.putExtra("message", value);
                startActivity(intent);
                /*}*/

                Log.d("MyCustomFCMService", "Key: " + "key" + " Value: " + value + ":" + getIntent().getExtras().getString("request_id"));
                /*            }*/
            } else {
                startTimer();
            }
        } else {
            startTimer();
        }
        getIds();
        //startTimer();
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    private void startTimer() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {

//                    GetKeyHashValue();
//                    doProgress();
                    if (checkAndRequestPermissions()) {


                        setSplaceScreen();
                    }
                }
            }, splashTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkAndRequestPermissions() {
        int hasContactPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS);
        int hasReadSmsPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS);
        int AccessFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int AccessCorasLocation = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int AccessReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int AccessWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int AccessCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (AccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (AccessCorasLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_SMS);
        }
        if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (AccessCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (AccessReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (AccessWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);

       /* switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS: {*/
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            // Permission was granted.
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                startTimer();


            }
        } else {
            // Permission denied
            //   permissionsDenied();
            checkAndRequestPermissions();
        }
               /* break;
            }
        }*/
    }

    private void permissionsDenied() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            checkAndRequestPermissions();
        } else {
            checkAndRequestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
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


