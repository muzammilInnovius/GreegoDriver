package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Location;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.Fragment.MapHomeFragment;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityAcceptUserRequestBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String trip_id;
    public static Boolean flag=false;
    //sapan
    //LineDraw
    private List<LatLng> routeList;
    private static final long MOVE_ANIMATION_DURATION = 1000;
    private long TURN_ANIMATION_DURATION = 500;
    private Marker marker;
    Bitmap mMarkerIcon;
    private int mIndexCurrentPoint = 0;
    private LatLng pickupPoint, dropPoint;
    public static final int PICK_CONTACT_REQUEST = 1;  // The request code
    public static final int ADD_EDIT_VEHICAL_REQUEST = 2000;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int REQ_PERMISSION = 999;
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_accept_user_request);
        context = AcceptUserRequestActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
        timer();
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        getUserData(""+getIntent().getStringExtra("message"));
    }

    private void getUserData(final String key) {
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);
            jsonObject.put(WebFields.ACCEPT_USER_REQUEST.request_id,key);
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.ACCEPT_USER_REQUEST.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    trip_id=key;
                    //
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    MyProgressDialog.hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));

                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    if (acceptTrip())
                    {
                        mCountDownTimer.cancel();
                        tvTimer.setVisibility(View.GONE);
                        imgVwStart.setVisibility(View.VISIBLE);
                        tvTab.setText(getResources().getString(R.string.arrive_for_user));
                    }

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

    private boolean acceptTrip() {
        Toast.makeText(context, ""+trip_id, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);
            jsonObject.put(WebFields.ACCEPT_USER_REQUEST.request_id,trip_id);
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.ACCPET_TRIP.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    flag=true;
                    MyProgressDialog.hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
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
//                   OpenDialog();
                finish();
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

    /*private class TripDetailResponse {
        String message;
        String error_code;
        TripDetail tripDetail;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError_code() {
            return error_code;
        }

        public void setError_code(String error_code) {
            this.error_code = error_code;
        }

        public TripDetail getTripDetail() {
            return tripDetail;
        }

        public void setTripDetail(TripDetail tripDetail) {
            this.tripDetail = tripDetail;
        }

    }*/
}
