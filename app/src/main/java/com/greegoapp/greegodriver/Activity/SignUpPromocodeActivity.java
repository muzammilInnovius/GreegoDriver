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

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.databinding.ActivitySignUpPromocodeBinding;

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
                strPromocode = edtTvPromoCode.getText().toString();
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
                KeyboardUtility.hideKeyboard(context, view);
                intent_skip = new Intent(context, SignUpAgreementActivity.class);
                intent_skip.putExtra("email", strEmail);
                intent_skip.putExtra("fName", strFname);
                intent_skip.putExtra("lName", strLname);
                intent_skip.putExtra("promoCode", strPromocode);
                intent_skip.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_skip);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
                break;
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
