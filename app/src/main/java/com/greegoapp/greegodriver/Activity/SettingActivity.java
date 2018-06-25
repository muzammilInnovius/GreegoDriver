package com.greegoapp.greegodriver.Activity;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.FCM.FCMIDService;
import com.greegoapp.greegodriver.Fragment.SettingDetailFragment;
import com.greegoapp.greegodriver.Fragment.UserEmailSettingFragment;
import com.greegoapp.greegodriver.Fragment.UserNameSettingFragment;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.DriverPersnlInfoUpdateData;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySettingBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySettingBinding binding;
    Context context;
    View snackBarView;
    TextView tvTitle, tvEmailVerify;
    ImageButton ibback;

    RelativeLayout rlUserName, rlUserEmail, rlUserPhone, rlPayment, rlNavigate, rlLogout;
    //Priyanka 23-4
    GetDriverData.DataBean driverDetails;
    ArrayList<GetDriverData> alDriverList;
    ImageView imgVwProPic;
    TextView tvUserName, tvUserEmail, tvUserPhone, tvGoogleMap, tvJoinDate;
    public static int REQUEST_USER_NAME = 1001;
    int REQUEST_USER_EMAIL = 002;
    int REQUEST_USER_BANK = 003;
    int REQUEST_NAVIGATION = 004;
    private JSONObject response_data;
    private int email_verified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        context = SettingActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        bindViews();
        setListner();
        //priyanka 23-4
        if (ConnectivityDetector.isConnectingToInternet(context)) {
            callUserMeApi();
            //  callEmailVerifyApi();
//            CheckGpsStatus();
        } else {
            SnackBar.showInternetError(context, snackBarView);
            //     Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
        }

//        setFragmentValues();
    }


    private void callUserMeApi() {
        try {
            JSONObject jsonObject = new JSONObject();

            Applog.E("request: " + jsonObject.toString());
             MyProgressDialog.showProgressDialog(context);
            try {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        WebFields.BASE_URL + WebFields.USER_ME.MODE, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        MyProgressDialog.hideProgressDialog();
                        Applog.E("success: " + response.toString());
                        response_data = response;
                        driverDetails = new Gson().fromJson(String.valueOf(response), GetDriverData.DataBean.class);
                        GetDriverData driverDetail = new Gson().fromJson(String.valueOf(response), GetDriverData.class);
                        try {
                            MyProgressDialog.hideProgressDialog();
                            alDriverList = new ArrayList<>();

                            if (driverDetail.getError_code() == 0) {


                                alDriverList.add(driverDetail);
                                Applog.E("UserUpdate==>Dg==>" + driverDetail);
                                String strName = driverDetail.getData().getName();
                                String strEmail = driverDetail.getData().getEmail();
                                String strPhone = driverDetail.getData().getContact_number();
                                String strProfilePic = driverDetail.getData().getProfile_pic();
                                String strJoinDate = "";
                                tvGoogleMap.setText(getApplicationContext().getSharedPreferences("navigation", 0).getString("mode", "Map"));
                                try {
                                    strJoinDate = driverDetail.getData().getBank_information().getCreated_at();
                                    strJoinDate = new SimpleDateFormat("MMM  yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(strJoinDate.toString()));//2018-05-24
                                } catch (ParseException e) {
                                }catch (Throwable throwable)
                                {
                                    Bugsnag.notify(throwable);
                                }
                                email_verified = driverDetail.getData().getEmail_verifed();
                                if (email_verified != 0) {
                                    tvEmailVerify.setText(getResources().getString(R.string.verify));
                                } else {
                                    tvEmailVerify.setText(getResources().getString(R.string.unverify));
                                }
                                tvUserName.setText(strName);
                                tvUserEmail.setText(strEmail);
                                tvUserPhone.setText(strPhone);
                                tvJoinDate.setText("" + strJoinDate);
                                Glide.clear(imgVwProPic);
                                Glide.with(context).load(strProfilePic)
                                        .into(imgVwProPic);

                            } else {
                                MyProgressDialog.hideProgressDialog();
                                SnackBar.showError(context, snackBarView, response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Throwable throwable)
                        {
                            Bugsnag.notify(throwable);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyProgressDialog.hideProgressDialog();
                        Applog.E("Error: " + error.getMessage());

                    //    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put(WebFields.PARAM_ACCEPT, "application/json");
                        Applog.E("Token==>" + SessionManager.getToken(context));
                        params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));

                        return params;
                    }
                };
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                        GlobalValues.TIME_OUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance().addToRequestQueue(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

//    private void setFragmentValues() {
//        try {
//            Fragment fragmentPro = null;
//            fragmentPro = new SettingDetailFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            fragmentTransaction.replace(R.id.containerSetting, fragmentPro);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
//            mContentFragment = fragmentPro;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void setListner() {
        ibback.setOnClickListener(this);
        rlUserName.setOnClickListener(this);
        rlUserEmail.setOnClickListener(this);
        rlUserPhone.setOnClickListener(this);
        rlPayment.setOnClickListener(this);
        rlNavigate.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
    }

    private void bindViews() {
        ibback = binding.ibBack;
        rlUserName = binding.rlName;
        rlUserEmail = binding.rlEmail;
        rlUserPhone = binding.rlPhone;
        rlPayment = binding.rlPayment;
        rlNavigate = binding.rlNavigate;
        rlLogout = binding.rlLogout;
        //priyanka 23/4
        imgVwProPic = binding.ivProPic;
        tvUserName = binding.tvUserName;
        tvGoogleMap = binding.tvGoogleMap;
        tvUserPhone = binding.tvUserPhone;
        tvUserEmail = binding.tvUserEmail;
        tvEmailVerify = binding.tvEmailVerify;
        tvJoinDate = binding.tvJoinDate;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USER_NAME) {
            if (resultCode == REQUEST_USER_NAME) {
                //    Toast.makeText(context, ""+data.getStringExtra("name"), Toast.LENGTH_SHORT).show();
                tvUserName.setText(data.getStringExtra("name"));
                String profilePic = data.getStringExtra("profile");

                Glide.clear(imgVwProPic);
                Glide.with(context).load(profilePic)
                        .into(imgVwProPic);
//                callUserMeApi();
                //callAcceptAgreementAPI();
            }
        } else if (requestCode == REQUEST_USER_EMAIL) {
            if (resultCode == 1) {
                tvUserEmail.setText(data.getStringExtra("email"));
            }
        } else if (requestCode == REQUEST_USER_BANK) {
            callUserMeApi();
        } else if (requestCode == REQUEST_NAVIGATION) {
            if (resultCode == 1) {
                String string = data.getStringExtra("index");
                tvGoogleMap.setText(data.getStringExtra("index"));
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("navigation", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mode", string);
                editor.commit();
            }
        }
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

    /*private void callAcceptAgreementAPI() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_FIRST_NAME, tvUserName.getText().toString().trim());
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_LAST_NAME, tvUserName.getText().toString().substring(tvUserName.getText().toString().indexOf(" "),tvUserName.getText().toString().length()));
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_EMAIL, tvUserEmail);
         *//*   jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_CITY, "");
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PRO_PIC, "");*//*

            Applog.E("request DigitCode=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_UPDATE.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    MyProgressDialog.hideProgressDialog();
                *//*    DriverPersnlInfoUpdateData userDetails = new Gson().fromJson(String.valueOf(response), DriverPersnlInfoUpdateData.class);
                    try {
                        MyProgressDialog.hideProgressDialog();
//
                        if (userDetails.getError_code() == 0) {

                            Applog.E("UserDetails" + userDetails);
//                            callUserMeApi();

                            SessionManager.setIsUserLoggedin(context, true);
                            int profileStatus = userDetails.getData().getProfile_status();

//                            String isAggred = userDetails.getData().getIs_agreed();
                            //getIs_agreed = 0 new user

//
//                            Intent in = new Intent(context, DriverPersonalInfoActivity.class);
//                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(in);
//                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
                        } else {
                            MyProgressDialog.hideProgressDialog();
                            SnackBar.showError(context, snackBarView, response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*//*
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());

                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));

                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlName:
                Intent intentName = new Intent(context, UserNameSettingActivity.class);
                intentName.putExtra("fname", response_data.optJSONObject("data").optString("name"));
                intentName.putExtra("lname", response_data.optJSONObject("data").optString("lastname"));
                intentName.putExtra("promo", response_data.optJSONObject("data").optString("promocode"));
                intentName.putExtra("email", response_data.optJSONObject("data").optString("email"));
                intentName.putExtra("is_agreed", response_data.optJSONObject("data").optString("is_agreed"));
                intentName.putExtra("profile", response_data.optJSONObject("data").optString("profile_pic"));
                startActivityForResult(intentName, REQUEST_USER_NAME);
            /*    Fragment fragment = new UserNameSettingFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerSetting, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                break;
            case R.id.rlEmail:
                if (email_verified == 0) {
                    Intent emailIntent = new Intent(context, SettingEmailActivity.class);
                    emailIntent.putExtra("fname", response_data.optJSONObject("data").optString("name"));
                    emailIntent.putExtra("lname", response_data.optJSONObject("data").optString("lastname"));
                    emailIntent.putExtra("promo", response_data.optJSONObject("data").optString("promocode"));
                    emailIntent.putExtra("email", response_data.optJSONObject("data").optString("email"));
                    emailIntent.putExtra("is_agreed", response_data.optJSONObject("data").optString("is_agreed"));
                    emailIntent.putExtra("profile", response_data.optJSONObject("data").optString("profile_pic"));
                    startActivityForResult(emailIntent, REQUEST_USER_EMAIL);
                }
//                Fragment fragmentUserEmail = new UserEmailSettingFragment();
//                FragmentManager FMUserEmail = getSupportFragmentManager();
//                FragmentTransaction FTUserEmail = FMUserEmail.beginTransaction();
//                FTUserEmail.replace(R.id.containerSetting, fragmentUserEmail);
//                FTUserEmail.addToBackStack(null);
//                FTUserEmail.commit();
                break;
            case R.id.rlPhone:
                break;
            case R.id.rlPayment:
                Intent bankIntent = new Intent(context, SettingBankDetailUpdateActivity.class);
                bankIntent.putExtra("acc_no", response_data.optJSONObject("data").optJSONObject("bank_information").optString("account_number"));
                bankIntent.putExtra("rout_no", response_data.optJSONObject("data").optJSONObject("bank_information").optString("routing_number"));
                startActivityForResult(bankIntent, REQUEST_USER_BANK);
                break;
            case R.id.rlNavigate:
                Intent navigationIntent = new Intent(context, SettingNavigationActivity.class);
                ArrayList<String> appName = new ArrayList<>();
                for (ApplicationInfo applicationInfo : findWaze()) {
                    appName.add(applicationInfo.loadLabel(packageManager).toString());
                }
                navigationIntent.putStringArrayListExtra("appList", appName);
                startActivityForResult(navigationIntent, REQUEST_NAVIGATION);
                break;

            case R.id.ibBack:
                finish();
                break;
            case R.id.rlLogout:
                showCallbacksLogout("Are you sure you want to Logout?");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void showCallbacksLogout(String strLogOut) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Greego").setMessage(strLogOut);


            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {


                        //priyanka(17-5)
                        callChangeDeviceIdAPI(0);
                        stopService(new Intent(SettingActivity.this, FCMIDService.class));
                        SessionManager.clearAppSession(context);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();


                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
            });

            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    private void callChangeDeviceIdAPI(int driver_on) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.CHANGE_DEVICE_ID.driver_on, driver_on);
            Applog.E("request: " + jsonObject.toString());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.CHANGE_DEVICE_ID.MODE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Applog.E("success: " + response.toString());
                            //  Toast.makeText(context, "device changed"+response, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Applog.E("Error: " + error.getMessage());
                    //SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    Applog.E("Token==>" + SessionManager.getToken(context));
                    params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

}

