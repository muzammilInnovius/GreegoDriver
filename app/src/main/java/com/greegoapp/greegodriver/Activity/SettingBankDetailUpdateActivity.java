package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.ProfileStatus;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySettingBankDetailUpdateBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SettingBankDetailUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingBankDetailUpdateBinding binding;
    Context context;
    TextInputEditText etRoutingNumber,etAccountNumber;
    Button btnSubmit;
    ImageButton ivBack;
    private String strRoutingNumber;
    private String strAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_setting_bank_detail_update);
        context = SettingBankDetailUpdateActivity.this;
        Bugsnag.init(context);
        bind();
        listners();
    }

    private void bind() {
        etRoutingNumber=binding.edtTvRoutingNumber;
        etAccountNumber=binding.edtTvAccountNumber;
        btnSubmit=binding.btnUpdate;
        ivBack=binding.ibBack;
        strAccountNumber=getIntent().getStringExtra("acc_no");
        strRoutingNumber=getIntent().getStringExtra("rout_no");
        try {
            strAccountNumber= new String (Base64.decode(strAccountNumber,Base64.DEFAULT),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
        etAccountNumber.setText("********"+strAccountNumber.substring(strAccountNumber.length()-4,strAccountNumber.length()));
        etRoutingNumber.setText("*****"+strRoutingNumber.substring(strRoutingNumber.length()-4,strRoutingNumber.length()));
        context=SettingBankDetailUpdateActivity.this;
        snackBarView = findViewById(android.R.id.content);
    }
    private void listners() {
        etAccountNumber.setOnClickListener(this);
        etRoutingNumber.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        KeyboardUtility.hideKeyboard(context, view);
        switch (view.getId()){
            case R.id.ibBack:
                finish();
                break;
            case R.id.btnUpdate:
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        callBankInfoAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
        }
    }
    private View snackBarView;
    private boolean isValid() {

        strRoutingNumber = etRoutingNumber.getText().toString();
        strAccountNumber = etAccountNumber.getText().toString();
        if (strRoutingNumber.isEmpty()) {
            etRoutingNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_routing_number));
            return false;
        } else if (strAccountNumber.isEmpty()) {
            etAccountNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_account_number));
            return false;
        } else if (strRoutingNumber.length()!= 9) {
            etRoutingNumber.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_routing_number));
            return false;
        } else if (strAccountNumber.length()!= 8) {
            etAccountNumber.requestFocus();
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
        strRoutingNumber=etRoutingNumber.getText().toString();
        strAccountNumber=etAccountNumber.getText().toString();
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
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("success: " + response.toString());
                    finish();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());
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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }

    }

}
