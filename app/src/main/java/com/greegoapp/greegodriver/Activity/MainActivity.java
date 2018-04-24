package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    Context context;
    Button btnStart;
    TextView tvUserAppLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = MainActivity.this;
        bindView();
        setListner();
    }

    private void setListner() {
        tvUserAppLink.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    private void bindView() {
        tvUserAppLink=binding.tvUserAppLink;
        btnStart=binding.btnStart;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvUserAppLink:
                Toast.makeText(context,"User App Link",Toast.LENGTH_LONG).show();
                break;
            case R.id.btnStart:
                Intent nextPage=new Intent(this,SignUpMobileNumberActivity.class);
                startActivity(nextPage);
        }
    }
}
