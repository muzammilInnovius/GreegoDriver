package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
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
import com.greegoapp.greegodriver.databinding.ActivityDriverShippingInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_SHIPPING_INFO;

public class DriverShippingInfoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivityDriverShippingInfoBinding binding;
    Context context;
    private View snackBarView;
    Spinner spinnerCity, spinnerState;
    TextInputEditText edtTvStreetAddress, edtTvApt, edtTvZipCode;
    ImageButton ibCancel;
    Button btnNext;
    String[] citys = {"Washington", "Chicago", "New York", "Oxford"};
    String[] states = {"District of Columbia (DC)", "New York (NY)", "Illinois (IL)", "North Carolina (NC)"};
    int profileStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_shipping_info);
        context = DriverShippingInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        edtTvStreetAddress.setOnClickListener(this);
        edtTvApt.setOnClickListener(this);
        spinnerState.setOnItemSelectedListener(this);
        spinnerCity.setOnItemSelectedListener(this);
        edtTvZipCode.setOnClickListener(this);
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void bindView() {
        edtTvStreetAddress = binding.edtTvStreetAddress;
        edtTvApt = binding.edtTvAptName;
        spinnerState = binding.spinnerState;
        spinnerCity = binding.spinnerCity;
        edtTvZipCode = binding.edtTvZipCode;
        ibCancel = binding.ibCancel;
        btnNext = binding.btnNext;

        ArrayAdapter arrayAdapterCity = new ArrayAdapter(this, android.R.layout.simple_spinner_item, citys);
        arrayAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(arrayAdapterCity);

        ArrayAdapter arrayAdapterState = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
        arrayAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(arrayAdapterState);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibCancel:
                /*Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_SHIPPING_INFO,i);*/
                Intent in = new Intent(context, HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);


//                finish();
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        callShippingInfoAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
        }

    }

    String strStreet, strApt, strZipCode, strCity, strState;

    private boolean isValid() {

        strStreet = edtTvStreetAddress.getText().toString();
        strApt = edtTvApt.getText().toString();
        strZipCode = edtTvZipCode.getText().toString();

        if (strStreet.isEmpty()) {
            edtTvStreetAddress.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_street_address));
            return false;
        }/* else if (strCity.isEmpty()) {
            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_zip_code));
            return false;
        } else if (strCity.isEmpty()) {
            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_zip_code));
            return false;
        }*/ else if (strZipCode.isEmpty()) {
            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_zip_code));
            return false;
        } else if (strZipCode.length() < 5) {
            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_zip_code));
            return false;
        }
        return true;
    }

    private void callShippingInfoAPI() {
        strCity=spinnerCity.getSelectedItem().toString();
        strState=spinnerState.getSelectedItem().toString();
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_STREET, strStreet);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_APT, strApt);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_CITY, strCity);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_STATE, strState);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_ZIPCODE, strZipCode);

            Applog.E("request driver shipping info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_SHIPPING_ADDRESS.MODE, jsonObject, new Response.Listener<JSONObject>() {

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
                            setProfileScreen(profileStatus);
                           /* if(REQUEST_ADD_SHIPPING_INFO==1002)
                            {
                                Intent i= new Intent();
                                i.putExtra("profileStatus",profileStatus);
                                setResult(REQUEST_ADD_SHIPPING_INFO,i);
                            }*/
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
