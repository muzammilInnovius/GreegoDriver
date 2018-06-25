package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityRequestLyftUberBinding;

import java.util.ArrayList;
import java.util.List;

public class RequestLyftUberActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityRequestLyftUberBinding binding;
    Context context;
    private View snackBarView;
    RelativeLayout rlLyft, rlUber;
    ArrayList<String> appName;
    ListView listView;
    TextView tvlyft, tvUber;
    private ImageView imgVwUber;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_lyft_uber);
        context = RequestLyftUberActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
//        setContentView(R.layout.activity_request_lyft_uber);
        bindViews();
        setListner();
        appName = getIntent().getStringArrayListExtra("appList");
        /*for (String appName : appName) {
            if (appName.contains(tvlyft.getText().toString())) {
                *//*button.setVisibility(View.GONE);
                textView.setOnClickListener(this);
*//*
            }
        }*/
    }

    private void setListner() {
        rlLyft.setOnClickListener(this);
        rlUber.setOnClickListener(this);
        tvUber.setOnClickListener(this);
        tvlyft.setOnClickListener(this);
        imgVwUber.setOnClickListener(this);
    }

    private void bindViews() {
        imgVwUber = binding.imgVwUber;
        rlLyft = binding.rlLyft;
        rlUber = binding.rlUber;
        tvlyft = binding.tvRequestLyft;
        tvUber = binding.tvRequestUber;
    }

    PackageManager packageManager;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRequestLyft:
            case R.id.rlLyft:

                boolean lyftFound = isAppInstalled(context, "me.lyft.android");
                if (lyftFound) {
                    String packagename = "me.lyft.android";
                    Intent intent1 = getPackageManager().getLaunchIntentForPackage(packagename);
                    startActivity(intent1);
                }else
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "me.lyft.android")));
                }

                /*if (!checkInList("Lyft"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Lyft")));
                else*/
                //    launchNewActivity(context, "me.lyft.android");
                break;
            case R.id.tvRequestUber:
            case R.id.rlUber:
                boolean uberFound = isAppInstalled(context, "com.ubercab");
                if (uberFound) {
                    String packagename1 = "com.ubercab";
                    Intent intent11 = getPackageManager().getLaunchIntentForPackage(packagename1);
                    startActivity(intent11);
                }else
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.ubercab")));
                }
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Uber")));
                //  launchNewActivity(context, "com.ubercab");
                break;

        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
            return false;
        }
    }

    List<ApplicationInfo> findLyft() {
        Boolean flag = false;
        packageManager = getPackageManager();
        List<ApplicationInfo> appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (ApplicationInfo applist : appList) {
            if (applist.loadLabel(packageManager).toString().contains("Lyft"))
                flag = true;
        }
        return appList;
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {

                if (!info.packageName.equals("com.google.android.googlequicksearchbox")) {
                    if (!info.packageName.equals("com.metrobit.parentalcontrol")) {
                        if (!info.packageName.contains("launcher3")) {
                            if (!info.packageName.contains("launcher")) {//com.google.android.googlequicksearchbox
                                if (!info.packageName.contains("trebuchet")) {
                                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                                        applist.add(info);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
        }

        return applist;
    }

    boolean checkInList(String appName) {
        boolean flag = false;
        for (String appList : this.appName) {
            if (appList.contains(appName))
                flag = true;
        }
        return flag;
    }

    public void launchNewActivity(Context context, String packageName) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        }
        if (intent == null) {
            try {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


}
