package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.ProfileStatus;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityDriverBankInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_BANK_INFO;

public class DriverBankInfoActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityDriverBankInfoBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibCancel;
    Button btnNext;
    EditText edtTvRoutingNumber, edtTvAccountNumber;
    String strRoutingNumber, strAccountNumber;
    int profileStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_bank_info);
        context = DriverBankInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        edtTvAccountNumber.setOnClickListener(this);
        edtTvRoutingNumber.setOnClickListener(this);
    }

    private void bindView() {
        ibCancel = binding.ibCancel;
        btnNext = binding.btnNext;
        edtTvRoutingNumber = binding.edtTvRoutingNumber;
        edtTvAccountNumber = binding.edtTvAccountNumber;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        callBankInfoAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
            case R.id.ibCancel:
                /*Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_BANK_INFO,i);*/
                Intent in = new Intent(context, HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
//                finish();
                break;
        }
    }


    private boolean isValid() {

        strRoutingNumber = edtTvRoutingNumber.getText().toString();
        strAccountNumber = edtTvAccountNumber.getText().toString();
        if (strRoutingNumber.isEmpty()) {
            edtTvRoutingNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_routing_number));
            return false;
        } else if (strAccountNumber.isEmpty()) {
            edtTvAccountNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_account_number));
            return false;
        } else if (strRoutingNumber.length() < 9) {
            edtTvRoutingNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_routing_number));
            return false;
        } else if (strAccountNumber.length() < 8) {
            edtTvAccountNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_account_number));
            return false;
        }

        return true;
    }

    private void callBankInfoAPI() {
//        Intent in = new Intent(context, DriverTypeInfoActivity.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(in);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_BANK_INFO.PARAM_ROUTING_NO, strRoutingNumber);
            jsonObject.put(WebFields.SIGN_UP_BANK_INFO.PARAM_ACCOUNT_NO, strAccountNumber);

            Applog.E("request driver bank info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_BANK_INFO.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());

                    ProfileStatus userDetails = new Gson().fromJson(String.valueOf(response), ProfileStatus.class);
                    try {
                        MyProgressDialog.hideProgressDialog();
//
                        if (userDetails.getError_code() == 0) {

                            Applog.E("UserDetails" + userDetails);
//                            callUserMeApi();

                            SessionManager.setIsUserLoggedin(context, true);
//                            int profileStatus = userDetails.getData().getProfile_status();
//
                             profileStatus = userDetails.getData().getProfile_status();
//                            //getIs_agreed = 0 new user
                            /*if(REQUEST_ADD_BANK_INFO==1004)
                            {
                                Intent i= new Intent();
                                i.putExtra("profileStatus",profileStatus);
                                setResult(REQUEST_ADD_BANK_INFO,i);
                            }*/
                            setProfileScreen(profileStatus);
////
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

}
