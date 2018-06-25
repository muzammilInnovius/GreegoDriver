package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySettingEmailBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingEmailActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingEmailBinding binding;
    TextInputEditText etEmail;
    ImageButton imgBtnBack;
    Context context;
    private View snackBarView;
    Button btnUpdate;
    TextView txtConfirmMail;
    String strFname, strLname, strEmail, promoCode, is_agreed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this, R.layout.activity_setting_email);
        context = SettingEmailActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        bindView();
        listners();
        loaddata();
    }

    private void loaddata() {
        etEmail.setText(getIntent().getStringExtra("email"));
    }

    private void bindView() {
        etEmail=binding.edtTvEmail;
        imgBtnBack=binding.ibBack;
        btnUpdate=binding.btnUpdate;
        txtConfirmMail = binding.txtConfirmEmail;
        strFname = getIntent().getStringExtra("fname");
        strLname = getIntent().getStringExtra("lname");
        strEmail = getIntent().getStringExtra("email");
        promoCode = getIntent().getStringExtra("promo");
        is_agreed = getIntent().getStringExtra("is_agreed");

    }

    private void listners() {
        etEmail.setOnClickListener(this);
        imgBtnBack.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        txtConfirmMail.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }
    void closeActivity()
    {
        Intent intent = new Intent(SettingEmailActivity.this,SettingActivity.class);
        intent.putExtra("email",etEmail.getText().toString());
        setResult(1,intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.edtTvEmail:
                break;
            case R.id.ibBack:
                closeActivity();
                break;
            case R.id.btnUpdate:
                strEmail=etEmail.getText().toString();
                callAcceptAgreementAPI();
                break;
            case R.id.txtConfirmEmail:
               callEmailVerifyApi();
                break;
        }
    }
    private void callEmailVerifyApi() {
        try {
            JSONObject jsonObject = new JSONObject();

            Applog.E("request: " + jsonObject.toString());
//            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.VERIFY_EMAIL.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //                MyProgressDialog.hideProgressDialog();
                    Applog.E("success: " + response.toString());
                    String error_code = response.optInt("error_code") + "";
                    if(error_code.equals("0"))
                    {
                        SnackBar.showSuccess(context, snackBarView, response.optString("message").toString());
                    }
                  // SnackBar.showError(context, snackBarView, response.optString("message").toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //        MyProgressDialog.hideProgressDialog();
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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    private void callAcceptAgreementAPI() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_FIRST_NAME, strFname);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_LAST_NAME, strLname);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_EMAIL, strEmail);
         /*   jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_CITY, "");
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PRO_PIC, "");*/
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PROMO_CODE, promoCode);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_IS_AGGREED, is_agreed);


            Applog.E("request DigitCode=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(this);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_UPDATE.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("success: " + response.toString());
                    closeActivity();
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
                    params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(SettingEmailActivity.this));

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
