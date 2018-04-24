package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.greegoapp.greegodriver.databinding.ActivityDriverTypeInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_DRIVER_INFO;
import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_SHIPPING_INFO;

public class DriverTypeInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    ActivityDriverTypeInfoBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibCancel;
    Button btnNext;
    Spinner spinnerCarSize, spinnerTransmission;

    RelativeLayout rlSedan, rlSuv, rlVan, rlAuto, rlManual;
    TextView tvSedan, tvSuv, tvVan, tvAuto, tvManual;
    ImageView imgVwSedan, imgVwSuv, imgVwVan, imgVwAuto, imgVwManual;
    View viewSedan, viewSuv, viewVan, viewAuto, viewManual;

    boolean isSedan=true, isSuv=true, isVan=true, isAuto=true, isManual=true;
    int sedanType=0, suvType=0, vanType=0, autoType=0, manualType=0;
    int profileStatus;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_type_info);
        context = DriverTypeInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        rlSedan.setOnClickListener(this);
        rlSuv.setOnClickListener(this);
        rlVan.setOnClickListener(this);
        rlAuto.setOnClickListener(this);
        rlManual.setOnClickListener(this);

    }

    private void bindView() {
        ibCancel = binding.ibCancel;
        btnNext = binding.btnNext;

        rlSedan = binding.rlSedan;
        rlSuv = binding.rlSuv;
        rlVan = binding.rlVan;
        rlAuto = binding.rlAuto;
        rlManual = binding.rlManual;

        tvSedan = binding.tvSedan;
        tvSuv = binding.tvSuv;
        tvVan = binding.tvVan;
        tvAuto = binding.tvAuto;
        tvManual = binding.tvManual;

        imgVwSedan = binding.imgVwSedan;
        imgVwSuv = binding.imgVwSuv;
        imgVwVan = binding.imgVwVan;
        imgVwAuto = binding.imgVwAuto;
        imgVwManual = binding.imgVwManual;

        viewSedan = binding.viewSedan;
        viewSuv = binding.viewSuv;
        viewVan = binding.viewVan;
        viewAuto = binding.viewAuto;
        viewManual = binding.viewManual;

//        spinnerCarSize=binding.spinnerCarSize;
//        spinnerTransmission=binding.spinnerTransmission;
//        ArrayAdapter arrayAdapterCarSize = new ArrayAdapter(this,android.R.layout.simple_spinner_item,strCarSize);
//        arrayAdapterCarSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCarSize.setAdapter(arrayAdapterCarSize);
//        ArrayAdapter arrayAdapterTransmission = new ArrayAdapter(this,android.R.layout.simple_spinner_item,strTransmission);
//        arrayAdapterTransmission.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerTransmission.setAdapter(arrayAdapterTransmission);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibCancel:
               /* Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_DRIVER_INFO,i);*/
                Intent back = new Intent(this, HomeActivity.class);
                back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(back);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
//
//                finish();
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        callDriverTypeInfoAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;

            case R.id.rlSedan:
                if (isSedan == true) {
                    sedanType =1;
                    tvSedan.setTextColor(getResources().getColor(R.color.driver_type_bg));
                    imgVwSedan.setImageResource(R.mipmap.ic_check);
                    viewSedan.setBackgroundColor(getResources().getColor(R.color.driver_type_bg));
                    isSedan = false;
                } else {
                    sedanType =0;
                    tvSedan.setTextColor(getResources().getColor(R.color.hint_color));
                    imgVwSedan.setImageResource(0);
                    viewSedan.setBackgroundColor(getResources().getColor(R.color.hint_color));
                    isSedan = true;
                }
                break;
            case R.id.rlSuv:
                if (isSuv == true) {
                    suvType =1;
                    tvSuv.setTextColor(getResources().getColor(R.color.driver_type_bg));
                    imgVwSuv.setImageResource(R.mipmap.ic_check);
                    viewSuv.setBackgroundColor(getResources().getColor(R.color.driver_type_bg));
                    isSuv = false;
                } else {
                    suvType =0;
                    tvSuv.setTextColor(getResources().getColor(R.color.hint_color));
                    imgVwSuv.setImageResource(0);
                    viewSuv.setBackgroundColor(getResources().getColor(R.color.hint_color));
                    isSuv = true;
                }
                break;
            case R.id.rlVan:
                if (isVan == true) {
                    vanType =1;
                    tvVan.setTextColor(getResources().getColor(R.color.driver_type_bg));
                    imgVwVan.setImageResource(R.mipmap.ic_check);
                    viewVan.setBackgroundColor(getResources().getColor(R.color.driver_type_bg));
                    isVan = false;
                } else {
                    vanType =0;
                    tvVan.setTextColor(getResources().getColor(R.color.hint_color));
                    imgVwVan.setImageResource(0);
                    viewVan.setBackgroundColor(getResources().getColor(R.color.hint_color));
                    isVan = true;
                }
                break;
            case R.id.rlAuto:
                if (isAuto == true) {
                    autoType =1;
                    tvAuto.setTextColor(getResources().getColor(R.color.driver_type_bg));
                    imgVwAuto.setImageResource(R.mipmap.ic_check);
                    viewAuto.setBackgroundColor(getResources().getColor(R.color.driver_type_bg));
                    isAuto = false;
                } else {
                    autoType =0;
                    tvAuto.setTextColor(getResources().getColor(R.color.hint_color));
                    imgVwAuto.setImageResource(0);
                    viewAuto.setBackgroundColor(getResources().getColor(R.color.hint_color));
                    isAuto = true;
                }
                break;
            case R.id.rlManual:
                if (isManual == true) {
                    manualType=1;

                    tvManual.setTextColor(getResources().getColor(R.color.driver_type_bg));
                    imgVwManual.setImageResource(R.mipmap.ic_check);
                    viewManual.setBackgroundColor(getResources().getColor(R.color.driver_type_bg));
                    isManual = false;
                } else {
                    manualType=0;
                    tvManual.setTextColor(getResources().getColor(R.color.hint_color));
                    imgVwManual.setImageResource(0);
                    viewManual.setBackgroundColor(getResources().getColor(R.color.hint_color));
                    isManual = true;
                }
                break;
        }
    }

    private void callDriverTypeInfoAPI() {
//        Intent in = new Intent(context, DriverProfileInfoActivity.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(in);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_DRIVER_TYPE.PARAM_IS_SEDAN, sedanType);
            jsonObject.put(WebFields.SIGN_UP_DRIVER_TYPE.PARAM_IS_SUV, suvType);
            jsonObject.put(WebFields.SIGN_UP_DRIVER_TYPE.PARAM_IS_VAN, vanType);
            jsonObject.put(WebFields.SIGN_UP_DRIVER_TYPE.PARAM_IS_AUTO, autoType);
            jsonObject.put(WebFields.SIGN_UP_DRIVER_TYPE.PARAM_IS_MANUAL, manualType);

            Applog.E("request driver TYPE info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_DRIVER_TYPE.MODE, jsonObject, new Response.Listener<JSONObject>() {

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
                         /*   if(REQUEST_ADD_DRIVER_INFO==1005)
                            {
                                Intent i= new Intent();
                                i.putExtra("profileStatus",profileStatus);
                                setResult(REQUEST_ADD_DRIVER_INFO,i);
                            }*/
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

    private boolean isValid() {
        return true;
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
