package com.greegoapp.greegodriver.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivitySettingNavigationBinding;

import java.util.ArrayList;
import java.util.List;

public class SettingNavigationActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingNavigationBinding binding;
    List<String> appList;
    ListView lvAppList;
    ArrayList<String> appName;
    ImageButton ivBack;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SettingNavigationActivity.this, R.layout.activity_setting_navigation);
        context = SettingNavigationActivity.this;
        Bugsnag.init(context);
        bind();
        listners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void listners() {
        ivBack.setOnClickListener(this);
    }

    private void bind() {
        appList = new ArrayList<>();
        appList.add("Maps");
        appList.add("Waze");
        appName = getIntent().getStringArrayListExtra("appList");
        Log.d("admin@123", appName.toString());
        ivBack = binding.ibBack;
        lvAppList = binding.lvAppList;
        initControl();
    }

    AppListCustomAdapter applicationCustomAdapter;

    @SuppressLint("NewApi")
    private void initControl() {

        applicationCustomAdapter = new AppListCustomAdapter(appList, appName);
        lvAppList.setAdapter(applicationCustomAdapter);
        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SettingNavigationActivity.this, SettingActivity.class);
                intent.putExtra("index", appList.get(i));
                setResult(1, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                finish();
                break;
        }
    }

    class AppListCustomAdapter extends BaseAdapter implements View.OnClickListener {
        List<String> appList;
        ArrayList<String> appName;

        AppListCustomAdapter(List<String> appList, ArrayList<String> appName) {
            this.appList = appList;
            this.appName = appName;
        }

        @Override
        public int getCount() {
            return appList.size();
        }

        @Override
        public Object getItem(int i) {
            return appList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.row_app_list, null, false);
            //view1.setBackground(getDrawable(R.drawable.app_list_bkg));
            final TextView textView = view1.findViewById(R.id.txtAppName);
            final Button button = view1.findViewById(R.id.btnInstall);
            textView.setText(appList.get(i));
            for (String appName : appName) {
                if (appName.contains(textView.getText().toString())) {
                    button.setVisibility(View.GONE);
                    textView.setOnClickListener(this);
                }
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textView.getText().toString().equals("Waze")) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Waze")));
                    }
                }
            });
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if (textView.getText().toString() == "Maps") {
                        if (button.getVisibility() == View.GONE) {
                           *//* String packagename = "com.google.android.apps.maps";
                            Intent intent1 = getPackageManager().getLaunchIntentForPackage(packagename);
                            startActivity(intent1);*//*
                            Intent intent = new Intent(SettingNavigationActivity.this, SettingActivity.class);
                            intent.putExtra("index", appList.get(i));
                            setResult(1, intent);
                            finish();
                        }
                    } else if (textView.getText().toString() == "Waze") {
                        if (button.getVisibility() == View.GONE) {
                            Intent intent = new Intent(SettingNavigationActivity.this, SettingActivity.class);
                            intent.putExtra("index", appList.get(i));
                            setResult(1, intent);
                            finish();
                           *//* String packagename = "com.waze";
                            Intent intent1 = getPackageManager().getLaunchIntentForPackage(packagename);
                            startActivity(intent1);*//*
                        }
                    }*/
               /*     String packageName = "com.google.android.googlequicksearchbox";
                    Intent intent1 = getPackageManager().getLaunchIntentForPackage(packageName);
                    startActivity(intent1);*/
                    if (button.getVisibility() == View.GONE) {
                        Intent intent = new Intent(SettingNavigationActivity.this, SettingActivity.class);
                        intent.putExtra("index", textView.getText());
                        setResult(1, intent);
                        finish();
                    }
                }
            });
            return view1;
        }

        @Override
        public void onClick(View view) {
            Intent intent = null;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


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

    @Override
    protected void onResume() {
        super.onResume();
        appName.clear();
        for (ApplicationInfo applicationInfo : findWaze()) {
            appName.add(applicationInfo.loadLabel(packageManager).toString());
        }
        applicationCustomAdapter.notifyDataSetChanged();
    }

    PackageManager packageManager;

    List<ApplicationInfo> findWaze() {
        Boolean flag = false;
        packageManager = getPackageManager();
        List<ApplicationInfo> appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (ApplicationInfo applist : appList) {
            if (applist.loadLabel(packageManager).toString().contains("Waze"))
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
}
