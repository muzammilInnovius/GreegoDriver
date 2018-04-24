package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.EmailValidation;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.databinding.ActivitySignUpEmailBinding;

public class SignUpEmailActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpEmailBinding binding;
    EditText edtTvEmail;
    ImageButton ibBack;
    ImageButton ibFinish;
    Context context;
    private View snackBarView;
    String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_sign_up_email);
        context= SignUpEmailActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }
    private void setListners() {
        ibFinish.setOnClickListener(this);
        edtTvEmail.setOnClickListener(this);
        ibBack.setOnClickListener(this);
    }

    private void bindView() {
        edtTvEmail=binding.edtTvEmail;
        ibBack=binding.ibBack;
        ibFinish=binding.ibFinish;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibBack:
                KeyboardUtility.hideKeyboard(context, view);
//                Intent in = new Intent(context, SignUpDigitCodeActivity.class);
//                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(in);
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
                finish();

                break;
            case R.id.ibFinish:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        callEmailAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
        }
    }

    private boolean isValid() {
         strEmail = edtTvEmail.getText().toString();
        if (strEmail.isEmpty()) {
            edtTvEmail.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.entered_email_no_empty));
            return false;
        } else if(!EmailValidation.checkEmailIsCorrect(strEmail))
        {
            edtTvEmail.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_valid_email));
            return false;
        }

        return true;
    }

    private void callEmailAPI() {
        Intent in = new Intent(context, SignUpUserNameActivity.class);
        in.putExtra("email", strEmail);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
    }
}

