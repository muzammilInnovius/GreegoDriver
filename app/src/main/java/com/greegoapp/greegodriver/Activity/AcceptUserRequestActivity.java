package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.ActivityAcceptUserRequestBinding;

public class AcceptUserRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CALL_PERMIT = 101;
    ActivityAcceptUserRequestBinding binding;
    Context context;
    private View snackBarView;
    TextView tvTimer, tvUserAddress, tvRemainTime, tvPrepareUserCar, tvDropInTime;
    TextView tvTab;
    CountDownTimer mCountDownTimer;
    RelativeLayout rlUserDetail, rlVehicleDetail;
    ImageView imgVwStart, imgVwStartPin, imgVwCall, imgVwUserDropProfile;
    Dialog dialogConfirmArrive, dialogCall, dialogConfirmNavi, dialogConfirmDrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_accept_user_request);
        context = AcceptUserRequestActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
        timer();

    }

    private void setListners() {
        tvTab.setOnClickListener(this);
        imgVwCall.setOnClickListener(this);
    }

    private void bindView() {
        imgVwStartPin = binding.imgVwStartPin;
        tvUserAddress = binding.tvUserAddress;
        tvRemainTime = binding.tvRemainTime;
        tvTimer = binding.tvTimer;
        imgVwStart = binding.imgVwStart;
        tvDropInTime = binding.tvDropInTime;
        imgVwUserDropProfile = binding.imgVwUserDropProfile;
        imgVwCall = binding.imgVwCall;
        tvTab = binding.tvTabToBeDriver;
        tvPrepareUserCar = binding.tvPrepareUserCar;
        rlUserDetail = binding.rlUserDetail;
        rlVehicleDetail = binding.rlVehicleDetail;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTabToBeDriver:
                String strBtnText = tvTab.getText().toString();
                if (strBtnText == getResources().getString(R.string.tap_to_be_a_driver)) {
                    mCountDownTimer.cancel();
                    tvTimer.setVisibility(View.GONE);
                    imgVwStart.setVisibility(View.VISIBLE);
                    tvTab.setText(getResources().getString(R.string.arrive_for_user));
                } else if (strBtnText == getResources().getString(R.string.arrive_for_user)) {
                    dialogConfirmArrive = new Dialog(this);
                    dialogConfirmArrive.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogConfirmArrive.setContentView(R.layout.dialog_confirm_arrived_user);
                    Button btnConfirm = (Button) dialogConfirmArrive.findViewById(R.id.btnConfirm);
                    btnConfirm.setOnClickListener(this);
                    Button btnCancel = (Button) dialogConfirmArrive.findViewById(R.id.btnConfirmCancel);
                    btnCancel.setOnClickListener(this);
                    dialogConfirmArrive.show();
                } else if (strBtnText == getResources().getString(R.string.start)) {
                    /* timerPrepare();*/
                    dialogConfirmNavi = new Dialog(this);
                    dialogConfirmNavi.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogConfirmNavi.setContentView(R.layout.dialog_confirm_navigate);
                    TextView tvNavigate = (TextView) dialogConfirmNavi.findViewById(R.id.txtVwNavigate);
                    tvNavigate.setOnClickListener(this);
                    dialogConfirmNavi.show();
                    GoogleMap googleMap;
                    MapView mMapView = (MapView) dialogConfirmNavi.findViewById(R.id.map);
                    MapsInitializer.initialize(this);
                    mMapView = (MapView) dialogConfirmNavi.findViewById(R.id.map);
                    mMapView.onCreate(dialogConfirmNavi.onSaveInstanceState());
                    mMapView.onResume();// needed to get the map to display immediately
                } else if (strBtnText == getResources().getString(R.string.drop_user)) {
                    dialogConfirmDrop = new Dialog(this);
                    dialogConfirmDrop.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogConfirmDrop.setContentView(R.layout.dialog_user_dropped);
                    Button btnConfirmDrop = (Button) dialogConfirmDrop.findViewById(R.id.btnConfirmDrop);
                    btnConfirmDrop.setOnClickListener(this);
                    Button btnCancelDrop = (Button) dialogConfirmDrop.findViewById(R.id.btnDropCancel);
                    btnCancelDrop.setOnClickListener(this);
                    dialogConfirmDrop.show();
                }
                break;
            case R.id.btnConfirm:
                dialogConfirmArrive.dismiss();
                imgVwStartPin.setVisibility(View.GONE);
                //tvUserAddress.setVisibility(View.GONE);
                tvUserAddress.setText(getResources().getString(R.string.prepare_user_car));
                tvRemainTime.setVisibility(View.GONE);
                //    tvPrepareUserCar.setVisibility(View.VISIBLE);
                imgVwStart.setVisibility(View.GONE);
                tvTimer.setVisibility(View.VISIBLE);
                imgVwCall.setVisibility(View.VISIBLE);
                imgVwCall.setOnClickListener(this);
                tvTab.setText(getResources().getString(R.string.start));
                timerPrepare();
                break;
            case R.id.btnConfirmCancel:
                tvTab.setText(getResources().getString(R.string.arrive_for_user));
                dialogConfirmArrive.dismiss();
                break;
            case R.id.imgVwCall:
                dialogCall = new Dialog(this);
                dialogCall.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogCall.setContentView(R.layout.dialog_contact);
                Button btnCall = (Button) dialogCall.findViewById(R.id.btnCall);
                btnCall.setOnClickListener(this);
                Button btnCancelCall = (Button) dialogCall.findViewById(R.id.btnCallCancel);
                btnCancelCall.setOnClickListener(this);
                dialogCall.show();
                break;
            case R.id.btnCall:
                boolean check = requestPermission();
                if (check == false) {
                    requestPermission();
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + "7874412012"));
                    context.startActivity(intent);
                }

                break;
            case R.id.btnCallCancel:
                dialogCall.dismiss();
                tvTab.setText(getResources().getString(R.string.start));
                break;
            case R.id.txtVwNavigate:
                dialogConfirmNavi.dismiss();
                imgVwStartPin.setVisibility(View.VISIBLE);
                imgVwStartPin.setImageResource(R.mipmap.ic_sport_car_coupe_auto_top_view_512);
                tvUserAddress.setText(getResources().getString(R.string.user_address));
                imgVwStart.setVisibility(View.VISIBLE);
                tvDropInTime.setVisibility(View.VISIBLE);
                imgVwUserDropProfile.setVisibility(View.VISIBLE);
                tvTimer.setVisibility(View.GONE);
                //   tvPrepareUserCar.setVisibility(View.GONE);
                imgVwCall.setVisibility(View.GONE);
                rlUserDetail.setVisibility(View.GONE);
                tvTab.setText(getResources().getString(R.string.drop_user));
                break;
            case R.id.btnConfirmDrop:
                Intent i = new Intent(this, UserRateActivity.class);
                startActivity(i);
                break;
            case R.id.btnDropCancel:
                dialogConfirmDrop.dismiss();
                tvTab.setText(getResources().getString(R.string.drop_user));
                break;
        }
    }


    private boolean requestPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMIT);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMIT);
            }
            return false;
        } else {
            // Toast.makeText(getApplicationContext(), "Permission  Granted", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void timerPrepare() {
        final int[] seconds = new int[1];
        tvTimer.setText("");
        mCountDownTimer = new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                seconds[0] = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%2d", minutes) + ":" + String.format("%02d", seconds[0]));
            }

            @Override
            public void onFinish() {
                //   OpenDialog();
            }
        }.start();
    }

    public void timer() {
        final int[] seconds = new int[1];
        tvTimer.setText("");
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                seconds[0] = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%02d", seconds[0]));
            }

            @Override
            public void onFinish() {
                //   OpenDialog();
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void OpenDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
        alert.setView(R.layout.dialog_user_booked_by_other);
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
