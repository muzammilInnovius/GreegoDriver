package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    ActivitySettingBinding binding;
    Context context;
    View snackBarView;
    TextView tvTitle;
    ImageButton ibback;

    RelativeLayout rlUserName,rlUserEmail,rlUserPhone,rlPayment,rlNavigate,rlLogout;
    //Priyanka 23-4
    GetDriverData.DataBean driverDetails;
    ArrayList<GetDriverData> alDriverList;
    ImageView imgVwProPic;
    TextView tvUserName,tvUserEmail,tvUserPhone;
    int REQUEST_USER_NAME=001;
    int REQUEST_USER_EMAIL=002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        context = SettingActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindViews();
        setListner();
        //priyanka 23-4
        if (ConnectivityDetector.isConnectingToInternet(context)) {
            callUserMeApi();
//            CheckGpsStatus();
        } else {
            Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
        }

//        setFragmentValues();
    }

    private void callUserMeApi() {
        try {
            JSONObject jsonObject = new JSONObject();

            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.USER_ME.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());


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
                            tvUserName.setText(strName);
                            tvUserEmail.setText(strEmail);
                            tvUserPhone.setText(strPhone);

                            Glide.with(context).load(strProfilePic)
                                    .into(imgVwProPic);

                        } else {
                            MyProgressDialog.hideProgressDialog();
                            SnackBar.showError(context, snackBarView, response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        ibback=binding.ibBack;
        rlUserName=binding.rlName;
        rlUserEmail=binding.rlEmail;
        rlUserPhone=binding.rlPhone;
        rlPayment=binding.rlPayment;
        rlNavigate=binding.rlNavigate;
        rlLogout = binding.rlLogout;
        //priyanka 23/4
        imgVwProPic = binding.ivProPic;
        tvUserName = binding.tvUserName;
        tvUserEmail = binding.tvUserEmail;
        tvUserPhone = binding.tvUserPhone;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_USER_NAME)
        {
            if (resultCode==100)
            {
                Toast.makeText(context, ""+data.getStringExtra("name"), Toast.LENGTH_SHORT).show();
                tvUserName.setText(data.getStringExtra("name"));
                callAcceptAgreementAPI();
            }
        }
    }

    private void callAcceptAgreementAPI() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_FIRST_NAME, tvUserName.getText().toString().trim());
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_LAST_NAME, tvUserName.getText().toString().substring(tvUserName.getText().toString().indexOf(" "),tvUserName.getText().toString().length()));
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_EMAIL, tvUserEmail);
         /*   jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_CITY, "");
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PRO_PIC, "");*/

            Applog.E("request DigitCode=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_UPDATE.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    MyProgressDialog.hideProgressDialog();
                /*    DriverPersnlInfoUpdateData userDetails = new Gson().fromJson(String.valueOf(response), DriverPersnlInfoUpdateData.class);
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
                    }*/
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
                public Map<String, String> getHeaders() throws AuthFailureError {
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

    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rlName:
                Intent intentName=new Intent(context,UserNameSettingActivity.class);
                startActivityForResult(intentName,REQUEST_USER_NAME);
            /*    Fragment fragment = new UserNameSettingFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerSetting, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                break;
            case R.id.rlEmail:
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
                break;
            case R.id.rlNavigate:
                break;
            case R.id.ibBack:
                Intent i=new Intent(context, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.rlLogout:
                showCallbacksLogout("Are you sure you want to Logout?");
                break;
        }
    }

    private void showCallbacksLogout(String strLogOut) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Greego").setMessage(strLogOut);


            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

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
        }
    }

}
