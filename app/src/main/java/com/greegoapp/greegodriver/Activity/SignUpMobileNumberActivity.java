package com.greegoapp.greegodriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Adapter.CustomAdapter;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.FCM.Config;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.Login;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySignUpMobileNumberBinding;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SignUpMobileNumberActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivitySignUpMobileNumberBinding binding;
    Context context;
    EditText edtTvMobileNo;
    ImageButton ibBack;
    ImageButton ibFinish;
    private View snackBarView;
    String strMobileNo;
    Spinner spinnerCountry;
    TextView tvCountryCode;
    ImageView imgVwCntyLogo;
    String[] countryName = {"India", "USA", "North Korea", "South Korea"};
    String[] countryCode = {"+91", "+1", "+850", "+82"};
    int[] countryFlag = {R.mipmap.ic_indian_flag, R.mipmap.american_flag_large, R.mipmap.ic_nc_flag, R.mipmap.ic_sc_flag};
    String registerFCMKey;
    SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_mobile_number);
        context = SignUpMobileNumberActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        registerFCMKey = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("regId", "def");
        bindView();
        setListner();
        setSpinnerData();

    }

    private void setSpinnerData() {
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), countryFlag, countryCode, countryName);
        spinnerCountry.setAdapter(customAdapter);
    }

    private void setListner() {
        ibBack.setOnClickListener(this);
        ibFinish.setOnClickListener(this);
        edtTvMobileNo.setOnClickListener(this);
        imgVwCntyLogo.setOnClickListener(this);
        spinnerCountry.setOnItemSelectedListener(this);

        //   edtTvMobileNo.addTextChangedListener(mDateEntryWatcher);
        //     edtTvMobileNo.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());
    }

    private TextWatcher mDateEntryWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            if (working.length() == 2 && before == 0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                    isValid = false;
                } else {
                    working += "/";
                    edtTvMobileNo.setText(working);
                    edtTvMobileNo.setSelection(working.length());
                }
            } else if (working.length() == 5 && before == 0) {
                String enteredYear = working.substring(3);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt(enteredYear) < currentYear) {
                    isValid = false;
                }
            } else if (working.length() != 7) {
                isValid = false;
            }

            if (!isValid) {
                edtTvMobileNo.setError("Enter a valid date: MM/YY");
            } else {
                edtTvMobileNo.setError(null);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

    };

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        imgVwCntyLogo.setImageResource(countryFlag[position]);
        tvCountryCode.setText(countryCode[position]);
        spinnerCountry.setVisibility(View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class CreditCardNumberFormattingTextWatcher implements TextWatcher {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 16) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;
        }
    }


    private void bindView() {
        edtTvMobileNo = binding.edtTvMobileNo;
        ibBack = binding.ibBack;
        imgVwCntyLogo = binding.imgVwCntyLogo;
        ibFinish = binding.ibFinish;
        spinnerCountry = binding.spinnerCountry;
        tvCountryCode = binding.tvCountryCode;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                KeyboardUtility.hideKeyboard(context, view);
                Intent in = new Intent(context, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);

                break;
            case R.id.ibFinish:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        if (registerFCMKey != null) {
                            Log.e("FCM Key: ",registerFCMKey);
                            if (registerFCMKey == "def") {
                                registerFCMKey = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("regId", "def");
                                Log.e("FCM new Key: ",registerFCMKey);
                                callMobileNumberAPI();
                            } else {
                                callMobileNumberAPI();
                            }
                        } else {
                            SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                        }

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
            case R.id.imgVwCntyLogo:
                spinnerCountry.setVisibility(View.VISIBLE);
                if (spinnerCountry.getVisibility() == View.VISIBLE) {
                    spinnerCountry.performClick();
                }
                /*   spinnerCountry.performClick();*/
                break;
        }

    }

    private void callMobileNumberAPI() {
       /* Intent in = new Intent(context, SignUpDigitCodeActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);*/
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);

            jsonObject.put(WebFields.SIGN_IN.PARAM_CONTACT_NO, strMobileNo);
            jsonObject.put(WebFields.SIGN_IN.PARAM_IS_PHONE_NO, 0);
            jsonObject.put(WebFields.SIGN_IN.PARAM_USER_TYPE, GlobalValues.USER_TYPE);
            jsonObject.put(WebFields.SIGN_IN.PARAM_DEVICE_ID, registerFCMKey);
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_IN.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    try {
                        Login loginData = new Gson().fromJson(String.valueOf(response), Login.class);
                        try {
                            MyProgressDialog.hideProgressDialog();
                            if (loginData.getError_code() == 0) {

                                ///    Applog.E("Contact No" + loginData.getData().getContact_number());
                                SessionManager.saveUserData(context, loginData);
//                            SnackBar.showSuccess(context, snackBarView, response.getString("message"));
//                            backPressed.onBackPressed(getContext());

                                Intent in = new Intent(context, SignUpDigitCodeActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(in);
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);

                            } else {
                                MyProgressDialog.hideProgressDialog();
                                SnackBar.showError(context, snackBarView, response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            Bugsnag.notify(throwable);
                        }
                    } catch (Exception e) {

                    } catch (Throwable throwable) {
                        Bugsnag.notify(throwable);
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    private boolean isValid() {
        strMobileNo = tvCountryCode.getText().toString() + edtTvMobileNo.getText().toString();
        //strMobileNo = "+1" + edtTvMobileNo.getText().toString();
        if (strMobileNo.isEmpty()) {
            edtTvMobileNo.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_mobile_no_empty));
            return false;
        } else if (strMobileNo.length() < 10 || strMobileNo.length() > 15) {
            edtTvMobileNo.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.mobile_no_length));
            return false;
        }
        return true;
    }
}
