package com.greegoapp.greegodriver.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.Login;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySignUpDigitCodeBinding;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpDigitCodeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySignUpDigitCodeBinding binding;
    ImageButton ibBack;
    TextView tvResend;
    ImageButton ibFinish;
    PinView pinVwOtpCode;
    Context context;
    private View snackBarView;
    String strOtpCode;
    TextView txtTimer, tvCntWthDriver;
    CountDownTimer mCountDownTimer;
    Dialog dialogotp;
    int sendOtp;
    SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_digit_code);
        context = SignUpDigitCodeActivity.this;
        snackBarView = findViewById(android.R.id.content);

        bindView();
        setListners();
        timer();

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                pinVwOtpCode.setText(code);//set code in edit text

                if (ConnectivityDetector
                        .isConnectingToInternet(context)) {

                    callDigitCodeAPI();

                } else {
                    SnackBar.showInternetError(context, snackBarView);
                }
            }
        });

        tvCntWthDriver.setText(SessionManager.getMobileNo(context));
        sendOtp = SessionManager.getOTP(context);
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private void setListners() {
        ibFinish.setOnClickListener(this);
        tvResend.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        pinVwOtpCode.setOnClickListener(this);
    }

    private void bindView() {
        ibBack = binding.ibBack;
        pinVwOtpCode = binding.pVwDigitCode;
        tvResend = binding.tvResend;
        ibFinish = binding.ibFinish;
        tvCntWthDriver = binding.tvCntWthDriver;
        txtTimer = binding.txtTimer;
    }


    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
//                finish();
                KeyboardUtility.hideKeyboard(context, view);
                Intent iback = new Intent(context, SignUpMobileNumberActivity.class);
                iback.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iback);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
                break;
            case R.id.tvResend:
                cancelTimer();
                timer();
                callMobileNumberAPI();
                pinVwOtpCode.setText("");
                break;


            case R.id.ibFinish:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        callDigitCodeAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }

                break;
        }
    }


    private boolean isValid() {
        String code = pinVwOtpCode.getText().toString();
        if (code.length() == 0) {
            pinVwOtpCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_digit_code));
            return false;
        } else if (code.length() < 6) {
            pinVwOtpCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_valid_digit_code));
            return false;
        }
        return true;
    }

    private void callDigitCodeAPI() {
       /* Intent in = new Intent(context, SignUpEmailActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);*/
       // callUserMeApi();

        String mobileNo = SessionManager.getMobileNo(context);
        strOtpCode = pinVwOtpCode.getText().toString();
        callUserMeApi();
        if (sendOtp != 0) {
            sendOtp = SessionManager.getOTP(context);
            String newOtp = "" + sendOtp;
            if (strOtpCode.matches(newOtp)) {
                callUserMeApi();


            } else {
                SnackBar.showValidationError(context, snackBarView, getString(R.string.otp_resend_wng));
//            sendOtp = resendOtp;
            }} else {
            SnackBar.showValidationError(context, snackBarView, getString(R.string.otp_resend_wng));
//            sendOtp = resendOtp;
            }
//..............................................................................
//        if (sendOtp != null) {
//        Applog.E("sendOtp==>" +sendOtp);
//            if (strOtpCode.matches(sendOtp)) {
//                Intent in = new Intent(context, SignUpEmailActivity.class);
//                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(in);
//                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
////            }
//        } else {
//            SnackBar.showValidationError(context, snackBarView, getString(R.string.otp_resend_empty));
//        }

//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(WebFields.VERIFY_OTP.PARAM_CONTACT_NO, SessionManager.getMobileNo(context));
//            jsonObject.put(WebFields.VERIFY_OTP.PARAM_OTP, strOtpCode);
//
//            Applog.E("request DigitCode=> " + jsonObject.toString());
//            MyProgressDialog.showProgressDialog(context);
//
//            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                    WebFields.BASE_URL + WebFields.VERIFY_OTP.MODE, jsonObject, new Response.Listener<JSONObject>() {
//
//                @Override
//                public void onResponse(JSONObject response) {
//                    Applog.E("success: " + response.toString());
//
//                    VerifyOtp otpData = new Gson().fromJson(String.valueOf(response), VerifyOtp.class);
//                    try {
//                        MyProgressDialog.hideProgressDialog();
////
//                        if (otpData.getError_code() == 0) {
//
//                            Applog.E("Contact No" + otpData);
////                            SessionManager.saveUserData(context, otpData);
////                            SnackBar.showSuccess(context, snackBarView, response.getString("message"));
////
//                            if (otpData.getData().getNewX() == 1) {
//                                Intent in = new Intent(context, HomeActivity.class);
//                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(in);
//                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
//                            } else {
//                                Intent in = new Intent(context, SignUpEmailActivity.class);
//                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(in);
//                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
//                            }
//
//
////
//                        } else {
//                            MyProgressDialog.hideProgressDialog();
//                            SnackBar.showError(context, snackBarView, response.getString("message"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    MyProgressDialog.hideProgressDialog();
//                    Applog.E("Error: " + error.getMessage());
//
//                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
//                }
//            });
//            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                    GlobalValues.TIME_OUT,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//            AppController.getInstance().addToRequestQueue(jsonObjReq);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public void timer() {
        txtTimer.setText("");

        mCountDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                txtTimer.setText(String.format("%02d", minutes) + " : " + String.format("%02d", seconds));

            }

            @Override
            public void onFinish() {
                SnackBar.showValidationError(context, snackBarView, getString(R.string.otp_resend_empty));
                String mobileNo = SessionManager.getMobileNo(context);

                sendOtp = 0;

                txtTimer.setText("00" + " : " + "00");
                tvResend.setEnabled(true);
                tvResend.setTextColor(Color.BLUE);
                pinVwOtpCode.setText("");
//                isCounterRunning = false;
//                dialogotp.setCanceledOnTouchOutside(true);
//                dialogotp.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        return false;
//                    }
//                });
//                dialogotp.dismiss();
                pinVwOtpCode.setText("");
            }
        }.start();

        //cancel timer

    }

    //cancel timer
    void cancelTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    private void callMobileNumberAPI() {
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);

            jsonObject.put(WebFields.SIGN_IN.PARAM_CONTACT_NO, SessionManager.getMobileNo(context));
            jsonObject.put(WebFields.SIGN_IN.PARAM_IS_PHONE_NO, 0);
            jsonObject.put(WebFields.SIGN_IN.PARAM_USER_TYPE, GlobalValues.USER_TYPE);

            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_IN.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());

                    Login loginData = new Gson().fromJson(String.valueOf(response), Login.class);
                    try {
                        MyProgressDialog.hideProgressDialog();
                        if (loginData.getError_code() == 0) {

                            Applog.E("Contact No" + loginData.getData().getContact_number());
                            SessionManager.saveUserData(context, loginData);

                            sendOtp = SessionManager.getOTP(context);
                            Applog.E("Resend Otp ==> " + sendOtp);
//                            SnackBar.showSuccess(context, snackBarView, response.getString("message"));
//                            backPressed.onBackPressed(getContext());


//                            Intent in = new Intent(context, DigitCodeActivity.class);
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
            });
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    GetDriverData userDetails = new Gson().fromJson(String.valueOf(response), GetDriverData.class);

                    try {
                        MyProgressDialog.hideProgressDialog();
//
                        if (userDetails.getError_code() == 0) {

                            Applog.E("UserDetails==>Dg==>" + userDetails);
//                            SessionManager.saveUserData(context, userDetails);
//                            SnackBar.showSuccess(context, snackBarView, response.getString("message"));
//
                            int profileStatus = userDetails.getData().getProfile_status();
                            String isAggred = userDetails.getData().getIs_agreed();
                            //getIs_agreed = 0 new user
                            setProfileScreen(isAggred, profileStatus);

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

    private void setProfileScreen(String isAggreds, int profileStatus) {

        if (isAggreds.matches("0")) {
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
        } else if (isAggreds.matches("1")) {
//            if (profileStatus == 0) {
//                in = new Intent(context, SignUpEmailActivity.class);
//            } else if (profileStatus == 1) {
//                in = new Intent(context, DriverPersonalInfoActivity.class);
//            } else if (profileStatus == 2) {
//                in = new Intent(context, DriverShippingInfoActivity.class);
//            } else if (profileStatus == 3) {
//                in = new Intent(context, DriverAttachFileInfoActivity.class);
//            } else if (profileStatus == 4) {
//                in = new Intent(context, DriverBankInfoActivity.class);
//            } else if (profileStatus == 5) {
//                in = new Intent(context, DriverTypeInfoActivity.class);
//            } else if (profileStatus == 6) {
//                in = new Intent(context, DriverProfileInfoActivity.class);
//            } else if (profileStatus == 7) {
//                in = new Intent(context, HomeActivity.class);
//            }
//        } else {
            SessionManager.setIsUserLoggedin(context, true);
            in = new Intent(context, HomeActivity.class);
        }

        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
    }

}

