package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivitySignUpUserNameBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpUserNameActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpUserNameBinding binding;
    EditText edtTvFname,edtTvLname;
    ImageButton ibBack,ibFinish;
    Context context;
    private View snackBarView;
    String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user_name);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_user_name);
        strEmail = getIntent().getStringExtra("email");
        context= SignUpUserNameActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        edtTvFname.setOnClickListener(this);
        edtTvLname.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        ibFinish.setOnClickListener(this);
        edtTvFname.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
        edtTvLname.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
    }

    private void bindView() {
        edtTvFname=binding.edtTvFirstName;
        edtTvLname=binding.edtTvLastName;
        ibBack=binding.ibBack;
        ibFinish=binding.ibFinish;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibBack:
                KeyboardUtility.hideKeyboard(context, view);
                Intent in = new Intent(context, SignUpEmailActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
//                finish();
                break;
            case R.id.ibFinish:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        callUnameApi();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
        }
    }

    private void callUnameApi() {
        String strFName = edtTvFname.getText().toString();
        String strLName = edtTvLname.getText().toString();

        Intent in = new Intent(context, SignUpPromocodeActivity.class);
       // Intent in = new Intent(context,SignUpAgreementActivity.class);
        in.putExtra("promoCode","");
        //
        in.putExtra("email", strEmail);
        in.putExtra("fName", strFName);
        in.putExtra("lName", strLName);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);

    }

    private boolean isValid() {
        if (edtTvFname.getText().toString().isEmpty()) {
            edtTvFname.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_first_name));
            return false;
        }else  if (edtTvLname.getText().toString().isEmpty()) {
            edtTvLname.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_last_name));
            return false;
        }
        return true;
    }
}
