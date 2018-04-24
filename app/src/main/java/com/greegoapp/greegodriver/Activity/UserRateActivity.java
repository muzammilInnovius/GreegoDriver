package com.greegoapp.greegodriver.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityUserRateBinding;

public class UserRateActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityUserRateBinding binding;
    Context context;
    private View snackBarView;
    Button btnDone;
    Dialog dialogRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_user_rate);
        context= UserRateActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        btnDone.setOnClickListener(this);
    }

    private void bindView() {
        btnDone=binding.btnDone;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnDone:
                dialogRequest = new Dialog(this);
                dialogRequest.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogRequest.setContentView(R.layout.dialog_request_lyft_uber);
                Button btnBack=(Button)dialogRequest.findViewById(R.id.btnBack);
                btnBack.setOnClickListener(this);
                RelativeLayout rlRequest=(RelativeLayout)dialogRequest.findViewById(R.id.rlRequest);
                rlRequest.setOnClickListener(this);
                ImageView imgRequest=(ImageView)dialogRequest.findViewById(R.id.imgVwLyftUber);
                imgRequest.setOnClickListener(this);
                dialogRequest.show();
                break;
            case R.id.btnBack:
                Intent intentBack=new Intent(this,HomeActivity.class);
                startActivity(intentBack);
                break;
            case R.id.rlRequest:
                Intent intentRlRequest=new Intent(this,RequestLyftUberActivity.class);
                startActivity(intentRlRequest);
                break;
            case R.id.imgVwLyftUber:
                Intent intentImgRequest=new Intent(this,RequestLyftUberActivity.class);
                startActivity(intentImgRequest);

                break;
        }
    }
}
