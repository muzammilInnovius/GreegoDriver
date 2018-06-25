package com.greegoapp.greegodriver.Activity;

import android.app.Activity;
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
import com.bugsnag.android.Bugsnag;
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
import com.greegoapp.greegodriver.databinding.ActivitySignUpPromocodeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpPromocodeActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpPromocodeBinding binding;
    private View snackBarView;
    Context context;
    EditText edtTvPromoCode;
    Button btnSkip, btnNext;
    ImageButton ibcancel;

    String strPromocode;
    String strEmail, strFname, strLname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_promocode);
        strEmail = getIntent().getStringExtra("email");
        strFname = getIntent().getStringExtra("fName");
        strLname = getIntent().getStringExtra("lName");
        context = SignUpPromocodeActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListner();

    }

    private void setListner() {
        edtTvPromoCode.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ibcancel.setOnClickListener(this);
    }

    private void bindView() {
        edtTvPromoCode = binding.edtTvPromoCode;
        btnSkip = binding.btnSkip;
        btnNext = binding.btnNext;
        ibcancel = binding.ibCancel;
    }

    @Override
    public void onClick(View view) {
        Intent intent_skip;
        switch (view.getId()) {
            case R.id.ibCancel:
//                finish();
                KeyboardUtility.hideKeyboard(context, view);
                Intent in = new Intent(context, SignUpUserNameActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
                break;
            case R.id.btnSkip:
                strPromocode = " ";
                intent_skip = new Intent(context, SignUpAgreementActivity.class);
                intent_skip.putExtra("email", strEmail);
                intent_skip.putExtra("fName", strFname);
                intent_skip.putExtra("lName", strLname);
                intent_skip.putExtra("promoCode", strPromocode);
                intent_skip.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_skip);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
                break;
            case R.id.btnNext:
                if(isvalid()) {
                    validatePromoCode(view);
                }

                break;
        }
    }

    private boolean isvalid() {
        String promocode = edtTvPromoCode.getText().toString();
        if(promocode.equals(""))
        {
            edtTvPromoCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_promo));
            return false;
        }
        return true;
    }

    private void validatePromoCode(final View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.VERIFY_PROMO.promo_code, edtTvPromoCode.getText().toString().trim());
            Applog.E("request DigitCode=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.VERIFY_PROMO.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("success: " + response.toString());
                    if (response.optInt("error_code") == 0) {

                        Intent intent_skip;
                        KeyboardUtility.hideKeyboard(context, view);
                        intent_skip = new Intent(context, SignUpAgreementActivity.class);
                        intent_skip.putExtra("email", strEmail);
                        intent_skip.putExtra("fName", strFname);
                        intent_skip.putExtra("lName", strLname);
                        intent_skip.putExtra("promoCode", strPromocode);
                        intent_skip.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_skip);
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
                    } else {
                        SnackBar.showError(context, snackBarView, response.optString("message"));
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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    private boolean isValid() {
        String strCode = edtTvPromoCode.getText().toString();
        if (strCode.isEmpty()) {
            edtTvPromoCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.entered_promocode_no_empty));
            return false;
        }
        return true;
    }


}
