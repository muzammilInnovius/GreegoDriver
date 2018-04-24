package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityRequestLyftUberBinding;

public class RequestLyftUberActivity extends AppCompatActivity {

    ActivityRequestLyftUberBinding binding;
    Context context;
    private View snackBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_lyft_uber);

        context= RequestLyftUberActivity.this;
        snackBarView = findViewById(android.R.id.content);
        setContentView(R.layout.activity_request_lyft_uber);
    }
}
