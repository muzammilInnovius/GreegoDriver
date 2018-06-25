package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityPayoutDetailBinding;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayoutDetailActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPayoutDetailBinding binding;
    Context context;
    private View snackBarView;
    Double total_deposit_amount;
    Double driveAmount,bonus;
    Double greego_fee;
    String strCashoutdate;
    public  String formatted_date;
    TextView tvClose,tvCashOutTime,tvDrivePaymentAmount,tvBonusFeeAmount,tvGreegoFeeAmount,tvTotalDepositeAmount;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        type
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payout_detail);
        context = PayoutDetailActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
        setValues();
    }

    private void setValues() {
        Intent i = getIntent();
        String drive_amount = i.getStringExtra("drive_amount");
        String strGreegoFee= i.getStringExtra("greego_fee");
        driveAmount = Double.parseDouble(drive_amount);
        bonus = Double.parseDouble(i.getStringExtra("bonus"));
        strCashoutdate = i.getStringExtra("cash_out_time");
        formatted_date = parseDateToddMMyyyy(strCashoutdate);
        greego_fee = Double.parseDouble(strGreegoFee);
        String time = formatted_date.substring(formatted_date.indexOf(",")+1,formatted_date.length());
        String date = formatted_date.substring(0,formatted_date.indexOf(","));
        tvCashOutTime.setText("Cashout at "+time+" on "+date);
        Double total_deposit = driveAmount + bonus - greego_fee;
        /*tvDrivePaymentAmount.setText("$"+new DecimalFormat("##.##").format(driveAmount));
        tvGreegoFeeAmount.setText("-$"+new DecimalFormat("##.##").format(greego_fee));
        tvTotalDepositeAmount.setText("$"+new DecimalFormat("##.##").format(total_deposit));
        tvBonusFeeAmount.setText("+$"+new DecimalFormat("##.##").format(bonus));*/
        tvDrivePaymentAmount.setText("$"+String.format("%.2f",driveAmount));
        tvGreegoFeeAmount.setText("-$"+String.format("%.2f",greego_fee));
        tvTotalDepositeAmount.setText("$"+String.format("%.2f",total_deposit));
        tvBonusFeeAmount.setText("+$"+String.format("%.2f",bonus));
        if (i.getStringExtra("type").equals("1"))
            tvTitle.setText("Express Payout");
        else
            tvTitle.setText("Weekly Payout");
    }
    private String parseDateToddMMyyyy(String payoutDateTime) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(payoutDateTime);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
        return str;
    }
    private void setListners() {
    tvClose.setOnClickListener(this);
    }

    private void bindView() {
        tvClose = binding.tvClose;
        tvCashOutTime = binding.tvCashOutTime;
        tvDrivePaymentAmount = binding.tvDrivePaymentAmount;
        tvBonusFeeAmount = binding.tvBonusFeeAmount;
        tvGreegoFeeAmount = binding.tvGreegoFeeAmount;
        tvTotalDepositeAmount = binding.tvTotalDepositeAmount;
        tvTitle=binding.tvTitlePayout;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvClose:
                finish();
                break;
        }
    }
}
