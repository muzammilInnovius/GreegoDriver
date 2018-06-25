package com.greegoapp.greegodriver.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityHelpCenterDetailBinding;

public class HelpCenterDetailActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHelpCenterDetailBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibBack;
    TextView tvTitle/*, tvDetail*/;
    WebView tvDetail;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help_center_detail);

        snackBarView = findViewById(android.R.id.content);
        context = HelpCenterDetailActivity.this;
        Bugsnag.init(context);
        bindViews();
        setListner();
        Intent i = getIntent();
        if (i.getExtras() != null) {
            tvTitle.setText(i.getStringExtra("title"));//@font-face {font-family: 'arial';src: url('file:///android_asset/fonts/bichkam.ttf');}body {font-family: 'verdana';}
//            @SuppressLint("WrongConstant") String head1 = "<head><style>"+ Typeface.defaultFromStyle(R.style.AppTheme)+"</style></head>";
//            String text="<html>"+head1
//                    + "<body style=\"font-family: arial\">" + i.getStringExtra("detail")
//                    + "</body></html>";
//            tvDetail.loadDataWithBaseURL("",text,"text/html","utf-8","");
            tvDetail.loadData(i.getStringExtra("detail"),"text/html","utf-8");
            //tvDetail.setText(Html.fromHtml(i.getStringExtra("detail")));
        }
    }

    private void setListner() {
        ibBack.setOnClickListener(this);
    }

    private void bindViews() {
        tvTitle = binding.tvTitle;
//        tvDetail = binding.tv_help_center_detail;
        tvDetail = binding.tvHelpCenterDetail;
        ibBack = binding.ibBack;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                finish();
                break;
        }
    }
}
