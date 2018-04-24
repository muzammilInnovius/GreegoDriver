package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.DriverPersnlInfoUpdateData;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySignUpAgreementBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpAgreementActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySignUpAgreementBinding binding;
    Context context;
    NestedScrollView scrollView;
    CheckBox cbChecked;
    ImageButton ibBack;
    Button btnFinish;
    private View snackBarView;
    RelativeLayout relativeLayout;
    String strEmail, strFName, strLName, strPromoCode;
    int promoCode;
    boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_agreement);

        strEmail = getIntent().getStringExtra("email");
        strFName = getIntent().getStringExtra("fName");
        strLName = getIntent().getStringExtra("lName");
        strPromoCode = getIntent().getStringExtra("promoCode");

        context = SignUpAgreementActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    //.hide();
                } else {
                    relativeLayout.setVisibility(View.INVISIBLE
                    );
                    // fab.show();
                }
            }
        });
        btnFinish.setOnClickListener(this);
        ibBack.setOnClickListener(this);
    }

    private void bindView() {
        scrollView = binding.svAgreement;
        cbChecked = binding.cbAgreement;
        ibBack = binding.ibBack;
        relativeLayout = binding.rlAgreement;
        btnFinish = binding.btnFinish;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                KeyboardUtility.hideKeyboard(context, view);
                Intent in = new Intent(context, SignUpPromocodeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
//                finish();

                break;
            case R.id.btnFinish:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        isChecked = true;
                        callAcceptAgreementAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
        }
    }

    private void callAcceptAgreementAPI() {

        try {
            JSONObject jsonObject = new JSONObject();

            if (!strPromoCode.matches("")) {
                promoCode = Integer.parseInt(strPromoCode);
            }

            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_FIRST_NAME, strFName);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_LAST_NAME, strLName);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_EMAIL, strEmail);
         /*   jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_CITY, "");
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PRO_PIC, "");*/
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PROMO_CODE, promoCode);

            if (isChecked) {
                jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_IS_AGGREED, 1);
            } else {
                jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_IS_AGGREED, 0);
            }

            Applog.E("request DigitCode=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_UPDATE.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());

                    DriverPersnlInfoUpdateData userDetails = new Gson().fromJson(String.valueOf(response), DriverPersnlInfoUpdateData.class);
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
                            setProfileScreen(profileStatus);
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

    Intent in;

    private void setProfileScreen(int profileStatus) {

        if (profileStatus == 0) {
            in = new Intent(context, SignUpEmailActivity.class);
        } else if (profileStatus == 1) {
            in = new Intent(context, DriverPersonalInfoActivity.class);
        } else if (profileStatus == 2) {
            in = new Intent(context, DriverShippingInfoActivity.class);
        } else if (profileStatus == 3) {
            in = new Intent(context, DriverAttachFileInfoActivity.class);
        } else if (profileStatus == 4) {
            in = new Intent(context, DriverBankInfoActivity.class);
        } else if (profileStatus == 5) {
            in = new Intent(context, DriverTypeInfoActivity.class);
        } else if (profileStatus == 6) {
            in = new Intent(context, DriverProfileInfoActivity.class);
        } else if (profileStatus == 7) {
            in = new Intent(context, HomeActivity.class);
        }

        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
    }

    public boolean isValid() {
        //   boolean cbchecked=false;
        if (!cbChecked.isChecked()) {
            isChecked = false;
            cbChecked.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.no_checked_agreement));
            return false;
        }
        return true;
    }
}
