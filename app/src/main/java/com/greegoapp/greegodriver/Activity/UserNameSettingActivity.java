package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.databinding.ActivityUserNameSettingBinding;

public class UserNameSettingActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityUserNameSettingBinding binding;
    Context context;
    View snackBarView;
    ImageButton ibBack;
    ImageView imgVwProPic;
    TextInputEditText edtTvFirstName,edtTvLastName;
    Button btnUpdate;
    String strFname,strLname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_name_setting);
        context = UserNameSettingActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindViews();
        setListner();
    }

    private void setListner() {
        ibBack.setOnClickListener(this);
        imgVwProPic.setOnClickListener(this);
        edtTvFirstName.setOnClickListener(this);
        edtTvLastName.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    private void bindViews() {
        ibBack = binding.ibBack;
        imgVwProPic = binding.imgVwProPic;
        edtTvFirstName = binding.edtTvProfileFname;
        edtTvLastName = binding.edtTvProfileLname;
        btnUpdate = binding.btnUpdate;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnUpdate:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    Intent intent = new Intent(this,SettingActivity.class);
                    intent.putExtra("name",edtTvFirstName.getText()+" "+edtTvLastName.getText());
                    setResult(100,intent);
                    finish();
                }
                break;
            case R.id.ibBack:
                Intent back=new Intent(context,SettingActivity.class);
                startActivity(back);
                break;
        }
    }

    private boolean isValid() {
        strFname = edtTvFirstName.getText().toString();
        strLname = edtTvLastName.getText().toString();
        if(strFname.isEmpty())
        {
            edtTvFirstName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_first_name));
            return false;
        }
        else if(strLname.isEmpty())
        {
            edtTvLastName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_last_name));
            return false;
        }
        return true;
    }
}
