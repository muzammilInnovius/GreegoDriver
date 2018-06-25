package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetTripRate;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.RoundedImageView;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityAcceptUserRequestBinding;
import com.greegoapp.greegodriver.polilineAnimator.MapHttpConnection;
import com.greegoapp.greegodriver.polilineAnimator.PathJSONParser;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AcceptUserRequestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener/*OnMapReadyCallback,*/ {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    public static boolean active = false;
    String arrive_for_user;
    Dialog dialog;
    private static final String TAG = AcceptUserRequestActivity.class.getName();
    JSONObject response_data, response_trip;
    private static final int CALL_PERMIT = 101;
    ActivityAcceptUserRequestBinding binding;
    int trip_status;
    Context context;
    private View snackBarView;
    TextView tvTimer, tvUserAddress, tvRemainTime, tvPrepareUserCar, tvDropInTime, tvUserName, tvVehicleYear, tvVehicleMake, tvVehicleModel, tvVehicleColor, tvRating;
    TextView tvTab;
    CountDownTimer mCountDownTimer;
    RelativeLayout rlUserDetail, rlVehicleDetail;
    ImageView imgVwStart, imgVwStartPin, imgVwCall, imgVwUserDropProfile, imgVwUserProfile;
    Dialog dialogConfirmArrive, dialogCall, dialogConfirmNavi, dialogConfirmDrop;
    String trip_id;
    public static Boolean flag = false;
    //sapan
    //GoogleMap 21/4/2018
    GoogleMap googleMap, customGoogleMap;
    MapFragment mapFragment;
    private boolean mLocationPermissionGranted;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    LatLng latLng;
    LocationRequest locationRequest;
    //sapan
    //LineDraw
    private List<LatLng> routeList;
    private static final long MOVE_ANIMATION_DURATION = 1000;
    private long TURN_ANIMATION_DURATION = 500;
    private Marker marker;
    Bitmap mMarkerIcon;
    String latlong;
    private int mIndexCurrentPoint = 0;
    private LatLng current_location, dropPoint;
    public static final int PICK_CONTACT_REQUEST = 1;  // The request code
    public static final int ADD_EDIT_VEHICAL_REQUEST = 2000;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int REQ_PERMISSION = 999;
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    private String drope_for_user;
    private int id;

    float base_fee;
    float time_fee;
    float mile_fee;
    double trip_price;
    String trip_total_price;
    float over_mile_fee;
    float tripCost;
    float cancel_fee;
    private String username;
    private double est_trip_cost;
    private String actual_trip_amount;
    private boolean flagkm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_accept_user_request);
//broadcast receiver
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.durga.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        //end
        context = AcceptUserRequestActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bindView();
        setListners();
        callTripRateAPINew();
        getUserData("" + getIntent().getStringExtra("message"));
    }

    String url;

    private void getUserData(final String key) {
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);
            jsonObject.put(WebFields.ACCEPT_USER_REQUEST.request_id, key);
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.ACCEPT_USER_REQUEST.MODE, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Applog.E("success request: " + response.toString());
                            response_data = response;
                            timer();
                            JSONObject data = (response.optJSONObject("data")).optJSONObject("body");
                            if (data != null) {

                                trip_id = data.optInt("id") + "";
                                //trip_id = trip_id+data.optString("from_lat")+","+data.optString("from_lng");
                                tvUserAddress.setText(data.optString("from_address"));
                                // Toast.makeText(context, trip_id, Toast.LENGTH_SHORT).show();
                                dropPoint = new LatLng(Double.valueOf(data.optString("from_lat")), Double.valueOf(data.optString("from_lng")));
                                //Toast.makeText(context, ""+dropPoint.latitude+","+dropPoint.longitude, Toast.LENGTH_SHORT).show();
                                JSONObject user = data.optJSONObject("user");
                                username = user.optString("name");
                                tvUserName.setText(user.optString("name"));
                                tvVehicleYear.setText((response.optJSONObject("data")).optString("vehicle_year"));
                                tvVehicleMake.setText((response.optJSONObject("data")).optString("vehicel_name"));
                                tvVehicleModel.setText((response.optJSONObject("data")).optString("vehicel_model"));
                                tvVehicleColor.setText((response.optJSONObject("data")).optString("vehicle_color"));
                                tvRating.setText((response.optJSONObject("data")).optString("average_user_rating"));//average_user_rating
/*
                                url = "http://kroslinkstech.in/greego/storage/app/" + response_data.optJSONObject("data").optJSONObject("body").
                                        optJSONObject("user").optString("profile_pic");*/
                                url = response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("profile_pic");

//                        Glide.with(getApplicationContext()).load(url).into(imgVwUserDropProfile);
                                if (url != null) {
                                    Glide.clear(imgVwUserProfile);
                                    Glide.clear(imgVwUserDropProfile);
                                    Glide.with(context)
                                            .load(url)
                                            .centerCrop()
                                            .signature(new StringSignature(UUID.randomUUID().toString()))
                                            .crossFade().skipMemoryCache(true)
                                            .into(imgVwUserProfile);

                                    Glide.with(context)
                                            .load(url)
                                            .centerCrop()
                                            .signature(new StringSignature(UUID.randomUUID().toString()))
                                            .crossFade().skipMemoryCache(true)
                                            .into(imgVwUserDropProfile);

                                } else {
                                    imgVwUserDropProfile.setImageResource(R.mipmap.ic_place_holder);
                                    imgVwUserProfile.setImageResource(R.mipmap.ic_place_holder);
                                }

                                MyProgressDialog.hideProgressDialog();
                            } else {

                            }
//                            callTripRateAPI(statCode);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage() + ":" + error.getStackTrace());
                    //    Toast.makeText(context, "error" + error.getMessage() + error.getStackTrace(), Toast.LENGTH_SHORT).show();
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

    }

    private void setListners() {
        tvTab.setOnClickListener(this);
        imgVwCall.setOnClickListener(this);
        imgVwStart.setOnClickListener(this);
        if (mapFragment != null)
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(GoogleMap googleMap1) {
                    googleMap = googleMap1;
                    getLocationPermission();
                    if (mLocationPermissionGranted == true) {
                        if (mGoogleApiClient == null) {
                            setUpGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        googleMap.getUiSettings().setCompassEnabled(false);
                        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
                        googleMap.setMapStyle(mapStyle);
                    }

                    LatLng sydney = new LatLng(-33.867, 151.206);

                /*googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                googleMap.addMarker(new MarkerOptions()
                        .title("Sydney")
                        .snippet("The most populous city in Australia.")
                        .position(sydney));*/

                }
            });
    }

    private void bindView() {
        imgVwStartPin = binding.imgVwStartPin;
        tvUserAddress = binding.tvUserAddress;
        tvRemainTime = binding.tvRemainTime;
        tvTimer = binding.tvTimer;
        imgVwStart = binding.imgVwStart;
        imgVwUserProfile = binding.imgVwUserProfile;
        tvDropInTime = binding.tvDropInTime;
        imgVwUserDropProfile = binding.imgVwUserDropProfile;
        imgVwCall = binding.imgVwCall;
        tvTab = binding.tvTabToBeDriver;
        tvPrepareUserCar = binding.tvPrepareUserCar;
        rlUserDetail = binding.rlUserDetail;
        rlVehicleDetail = binding.rlVehicleDetail;
        tvUserName = binding.tvUserName;
        tvVehicleYear = binding.tvVehicleYear;
        tvVehicleMake = binding.tvVehicleMake;
        tvVehicleModel = binding.tvVehicleModel;
        tvRating = binding.tvRating;
        tvVehicleColor = binding.tvVehicleColor;
        statCode = "";
//        mapFragment=binding.mgooleMapAcceptUser;
//        mapFragment=(MapFragment) findViewById(R.id.mgooleMap);
//        mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.mgooleMap_acceptUser);
        initmap();

    }

    private void initmap() {
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mgooleMap_acceptUser, mapFragment);
        fragmentTransaction.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackHome:
                dialog.dismiss();
                finish();
                startActivity(new Intent(AcceptUserRequestActivity.this, HomeActivity.class));
                break;
            case R.id.tvTabToBeDriver:
                String strBtnText = tvTab.getText().toString();
                if (strBtnText == getResources().getString(R.string.tap_to_be_a_driver)) {
                    acceptTrip();

                } else if (strBtnText.equals(arrive_for_user)) {
                    dialogConfirmArrive = new Dialog(this);
                    dialogConfirmArrive.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogConfirmArrive.setContentView(R.layout.dialog_confirm_arrived_user);
                    Button btnConfirm = dialogConfirmArrive.findViewById(R.id.btnConfirm);
                    btnConfirm.setOnClickListener(this);
                    RoundedImageView imgVwUserProfile1 = dialogConfirmArrive.findViewById(R.id.imgVwUserProfile);
                    Button btnCancel = dialogConfirmArrive.findViewById(R.id.btnConfirmCancel);
                    btnCancel.setOnClickListener(this);
                    TextView tvUserAddress = dialogConfirmArrive.findViewById(R.id.tvUserAddress);
                    String address = (response_data.optJSONObject("data")).optJSONObject("body").optString("from_address");
                    address = address + "," + response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("name");
                    address = address + "?";


                    if (url != null) {
                        Glide.clear(imgVwUserProfile1);
                        Glide.with(context)
                                .load(url)
                                .centerCrop()
                                .signature(new StringSignature(UUID.randomUUID().toString()))
                                .crossFade().skipMemoryCache(true)
                                .into(imgVwUserProfile1);

                    } else {
                        imgVwUserProfile1.setImageResource(R.mipmap.ic_place_holder);
                    }


                    tvUserAddress.setText(address);
                    dialogConfirmArrive.show();
                } else if (strBtnText == getResources().getString(R.string.start)) {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss",
                            Locale.getDefault());

                    tripStartTime = format.format(new Date());
                    Applog.E("Time===>" + tripStartTime);

                    driverTripStatus(trip_status + 2);
                } else if (strBtnText.equals(drope_for_user)) {
                    dialogConfirmDrop = new Dialog(this);
                    dialogConfirmDrop.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogConfirmDrop.setContentView(R.layout.dialog_user_dropped);
                    TextView tvUserName = dialogConfirmDrop.findViewById(R.id.tvUserName);
                    RoundedImageView imgVwUserProfile = dialogConfirmDrop.findViewById(R.id.imgVwUserProfile);
                    tvUserName.setText("" + response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("name") + "'s");
                    Button btnConfirmDrop = (Button) dialogConfirmDrop.findViewById(R.id.btnConfirmDrop);
                    btnConfirmDrop.setOnClickListener(this);
                    Button btnCancelDrop = (Button) dialogConfirmDrop.findViewById(R.id.btnDropCancel);
                    btnCancelDrop.setOnClickListener(this);
                    dialogConfirmDrop.show();
                    if (url != null) {
                        Glide.clear(imgVwUserProfile);
                        Glide.with(context)
                                .load(url)
                                .centerCrop()
                                .signature(new StringSignature(UUID.randomUUID().toString()))
                                .crossFade().skipMemoryCache(true)
                                .into(imgVwUserProfile);

                    } else {
                        imgVwUserProfile.setImageResource(R.mipmap.ic_place_holder);
                    }
                }
                break;
            case R.id.imgVwStart:
                switchToNavigation();
                break;
            case R.id.btnConfirm:
                driverTripStatus(trip_status + 1);
                break;
            case R.id.btnConfirmCancel:
                //  tvTab.setText(getResources().getString(R.string.arrive_for_user));
                dialogConfirmArrive.dismiss();
                break;
            case R.id.imgVwCall:
                dialogCall = new Dialog(this);
                dialogCall.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogCall.setContentView(R.layout.dialog_contact);
                Button btnCall = dialogCall.findViewById(R.id.btnCall);
                btnCall.setText("Yes, call " + response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("name"));
                btnCall.setOnClickListener(this);
                Button btnCancelCall = dialogCall.findViewById(R.id.btnCallCancel);
                btnCancelCall.setOnClickListener(this);
                dialogCall.show();
                break;
            case R.id.btnCall:
                boolean check = requestPermission();
                if (check == false) {
                    requestPermission();
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String number = response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("contact_number");
                    //intent.setData(Uri.parse("tel:" + "7874412012"));
                    intent.setData(Uri.parse("tel:" + number));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    context.startActivity(intent);
                    dialogCall.dismiss();
                    //  tvTab.setText(getResources().getString(R.string.start));
                }

                break;
            case R.id.btnCallCancel:
                dialogCall.dismiss();
                // tvTab.setText(getResources().getString(R.string.start));
                break;
            case R.id.txtVwNavigate:
                dialogConfirmNavi.dismiss();
                imgVwStartPin.setVisibility(View.VISIBLE);
                imgVwStartPin.setImageResource(R.mipmap.ic_sport_car_coupe_auto_top_view_512);
                tvUserAddress.setText(response_data.optJSONObject("data").optJSONObject("body").optString("to_address"));
                imgVwStart.setVisibility(View.VISIBLE);
                tvDropInTime.setVisibility(View.VISIBLE);
                imgVwUserDropProfile.setVisibility(View.VISIBLE);
                //     imgVwUserDropProfile.setImageResource(R.mipmap.ic_place_holder);
                tvTimer.setVisibility(View.GONE);
                latlong = "" + response_data.optJSONObject("data").optJSONObject("body").optString("to_lat");
                latlong = latlong + ",";
                latlong = latlong + response_data.optJSONObject("data").optJSONObject("body").optString("to_lng");
                dropPoint = new LatLng(Double.valueOf(response_data.optJSONObject("data").optJSONObject("body").optString("to_lat")), Double.valueOf(response_data.optJSONObject("data").optJSONObject("body").optString("to_lng")));
                //   tvPrepareUserCar.setVisibility(View.GONE);
                imgVwCall.setVisibility(View.GONE);
                rlUserDetail.setVisibility(View.GONE);
                tvTab.setText(drope_for_user);
                break;
            case R.id.btnConfirmDrop:
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss",
                        Locale.getDefault());

                tripEndTime = format.format(new Date());
                Applog.E("Time===>" + tripEndTime);

                aTime = CalculateTime(tripStartTime, tripEndTime);
                callTripRateAPI(statCode, tripTotalDistance, aTime);
//                callTripRateAPINew();
//                        trip_total_price =  CalculateMile(tripTotalDistance, a);
                Applog.E("tripTotalTime===>" + aTime + " tripTotalDistance==>" + tripTotalDistance);

                break;
            case R.id.btnDropCancel:
                dialogConfirmDrop.dismiss();
                //   tvTab.setText(getResources().getString(R.string.drop_user));
                break;
        }
    }

    private void callTripRateAPINew() {
        GetTripRate tripRateData = new Gson().fromJson(getSharedPreferences("driverData", 0).getString("fee", ""), GetTripRate.class);
        base_fee = Float.parseFloat("" + tripRateData.getData().getBase_fee());
        time_fee = Float.parseFloat("" + tripRateData.getData().getTime_fee());
        mile_fee = Float.parseFloat("" + tripRateData.getData().getMile_fee());
        over_mile_fee = Float.parseFloat("" + tripRateData.getData().getOvermile_fee());
        cancel_fee = Float.parseFloat("" + tripRateData.getData().getCancel_fee());
        //   CalculateMile(str,elapsedTime);

       /* double time, dis;
        if (tripTotalDistance != null && aTime != null) {
            dis = tripTotalDistance;
            time = aTime;

            if (dis <= 10) {
                trip_price = base_fee + (time * time_fee) + (dis * mile_fee);
            } else {
                trip_price = base_fee + (time * time_fee) + (10 * mile_fee) + ((dis - 10) * over_mile_fee);
            }
        }


        Applog.E("==>" + trip_price);
        driverTripStatus(trip_status + 3);*/
    }

    Double aTime;

    private void switchToNavigation() {
        String mode = getSharedPreferences("navigation", 0).getString("mode", "");
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latlong);
        String pkg = "com.google.android.apps.maps";
        Intent mapIntent = null;
        if (mode.equals("Waze")) {

            gmmIntentUri = Uri.parse("https://waze.com/ul?ll=" + latlong + "&navigate=yes");
            //  mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            pkg = "com.waze";
        } else if (mode.equals("Maps")) {
            //mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            gmmIntentUri = Uri.parse("google.navigation:q=" + latlong);
            pkg = "com.google.android.apps.maps";
        }

        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(pkg);
        // Create a Uri from an intent string. Use the result to create an Intent.
        /*Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");*/
        //create a Uri from an intent string. Use the result to create an geo Intent

//        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latlong);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW

// Make the Intent explicit by setting the Google Maps package


// Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }

    private void linedraw() {
        current_location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        //   dropPoint = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
//                current_location = new LatLng(23.009748,72.56257);
        if (dropPoint == null)
            dropPoint = new LatLng(23.01349, 72.56221);
        createRoute(current_location, dropPoint);
    }

    private void acceptTrip() {
        //   Toast.makeText(context, "" + trip_id, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);
            jsonObject.put(WebFields.ACCPET_TRIP.request_id, trip_id);
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.ACCPET_TRIP.MODE, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            response_trip = response;
                            Applog.E("success: " + response.toString());
                            //priyanka (5-6)
                            SharedPreferences sp = getSharedPreferences("trip", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("response", response.toString());
                            ed.commit();


                            est_trip_cost = response.optJSONObject("data").optDouble("total_estimated_trip_cost");
                            String code = response.optString("error_code");
                            trip_status = response.optJSONObject("data").optInt("status");
                            id = response.optJSONObject("data").optInt("id");
                            Applog.E("" + code);
                            if (code.equals("0")) {
                                driveraccpettrip();
                            } else {
                                mCountDownTimer.cancel();
                                showCustomAlert("" + getString(R.string.user_already_taken));
/*
                        Toast.makeText(context, "trip accept by other driver", Toast.LENGTH_SHORT).show();
*/
                                //finish();
                            }
                            MyProgressDialog.hideProgressDialog();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    private void showCustomAlert(String s) {
        try {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setView(R.layout.dialog_unable_request);


          //  builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        dialog.dismiss();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
            });*//*

//            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.dismiss();
//                }
//            });
            AlertDialog dialog = builder.create();
            dialog.show();*/
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_unable_request);
            TextView tvTitle = dialog.findViewById(R.id.tvTitle1);
            Button btnHome = (Button) dialog.findViewById(R.id.btnBackHome);
            tvTitle.setText("" + s);
            btnHome.setOnClickListener(this);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleMap != null)
            googleMap.clear();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
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
    //sapan load map 21/4/2018
    /*@SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.867, 151.206);
        this.googleMap=googleMap;
        getLocationPermission();
        if (mLocationPermissionGranted == true) {
            if (mGoogleApiClient == null) {
                setUpGoogleApiClient();
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
        }


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        googleMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));

    }*/

    private synchronized void setUpGoogleApiClient() {
        Log.d("mapFragment", "createGoogleApi()");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void getLocationPermission() {

        /*     * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.*/

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    if (mLocationPermissionGranted == true) {
                        if (mGoogleApiClient == null) {
                            setUpGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setCompassEnabled(false);
                    }
                }
            }
        }
//        updateLocationUI();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("mapHomeFragment", "onConnected()");
        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w("mapHomeFragment", "onConnectionSuspended()");
        mGoogleApiClient.connect();
    }

    // Get last known location
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        {

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();


                double lat = lastLocation.getLatitude();
                double lng = lastLocation.getLongitude();

                if (ConnectivityDetector.isConnectingToInternet(context)) {
                    String currentLat = String.valueOf(lat);
                    String currentLong = String.valueOf(lng);
                } else {
                    SnackBar.showInternetError(context, snackBarView);
                    //   Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }

    }

    // Start location Updates
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }

    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private void writeActualLocation(Location location) {
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(location.getLatitude(), location.getLongitude())).bearing(360).
                zoom(15f).tilt(40).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onLocationChanged(Location location) {
        /*Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;*/
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        statCode = getstatCode(Double.parseDouble(lastLocation.getLatitude() + ""), Double.parseDouble(lastLocation.getLongitude() + ""));
        current_location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        //   dropPoint = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
//                pickupPoint = new LatLng(23.009748,72.56257);
       /* if (dropPoint == null)
            dropPoint = new LatLng(23.01349, 72.56221);
*/
        Log.d("aj", "onLocationChanged [" + current_location + "]");

        try {
            createRoute(current_location, dropPoint);
        } catch (Exception e) {

        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.durga.action.close")) {
                showCustomAlert("Sorry Trip Canceled by User.");
                stripePay();
            }
        }
    };

    void stripePay() {

      /*  Float num1 = Float.parseFloat(tvPrice.getText().toString());
        Float num2 = Float.parseFloat(tvTipPrice.getText().toString());
        Float sum = num1 + num2;
*/
        Applog.E("Total sum = > " + cancel_fee);

        MyProgressDialog.showProgressDialog(context);
        final Map<String, Object> parames = new HashMap<>();
//        double price=0;
        String price1 = cancel_fee + "";
        String trip_disc = "Cancel Charge of " + (response_data.optJSONObject("data")).optJSONObject("body").optString("from_address")
                + " to " + (response_data.optJSONObject("data")).optJSONObject("body").optString("to_address");
        if (price1.length() < 4)
            price1 = price1 + "0";
        int price = Integer.parseInt(price1.replace(".", ""));
        parames.put("amount", price);
        parames.put("currency", "usd");
        parames.put("description", trip_disc);
        parames.put("customer", response_trip.optJSONObject("data").optString("card_token"));


        try {
            new AsyncTask<Void, Void, Void>() {

                Charge charge;

                @Override
                protected Void doInBackground(
                        Void... params) {
                    try {

                        com.stripe.Stripe.apiKey = "sk_test_BXg8CAB9wtNTm1lNBe3HMK2X";
                        charge = Charge
                                .create(parames);


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        Bugsnag.notify(throwable);
                    }
                    return null;
                }

                protected void onPostExecute(Void result) {
                    MyProgressDialog.hideProgressDialog();
                    if (charge != null && charge.getPaid()) {
                        //Toast.makeText(getApplicationContext(), "thanks for transaction" + charge.getId(), Toast.LENGTH_SHORT).show();
//                        transactionApicall(charge.getId());
                    } else {
                        //Toast.makeText(context, "payment fail", Toast.LENGTH_SHORT).show();
                    }
                }

            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //sapan-->location over
    //sapan->line drawing
    private void createRoute(LatLng pickupPoint, LatLng dropPoint) {
        String url = getMapsApiDirectionsUrl(pickupPoint, dropPoint);
        ReadTask downloadTask = new ReadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    //create a url to find a route point
    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String filter = "units=imperial";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor+"&"+filter;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    //async to get points
    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            } catch (Throwable throwable) {
                Bugsnag.notify(throwable);
            }
            return data;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                new ParserTask().execute(result);
            } else {
                Log.e("TAG", "Didn't get response");
            }
        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String distance;

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
                /*duration = parser.getDuration(jObject);
                String[] separated = duration.split(" ");
                duration1 = separated[0]; // this will contain "Fruit"*/

                distance = parser.getDistance();
                parser.getDistance();
                parser.getDuratuion();
//                Toast.makeText(context, (Double.parseDouble( parser.getDistance())*0.621971)+":"+parser.getDuratuion(), Toast.LENGTH_LONG).show();
                Log.d("sapan", "distance and time fourmula :" + Double.parseDouble(parser.getDistance()) + ":" + parser.getDuratuion());
            } catch (Exception e) {
                e.printStackTrace();
            } /*catch (Throwable throwable) {
                Bugsnag.notify(throwable);
            }*/
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
//            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            Double dist = 0.0;
            int mapZoomLevel;
            try {
                if (routes.size() > 0) {
                    for (int i = 0; i < routes.size(); i++) {
                        routeList = new ArrayList<LatLng>();
                        polyLineOptions = new PolylineOptions();
                        List<HashMap<String, String>> path = routes.get(i);

                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            routeList.add(position);
                        }


                        polyLineOptions.addAll(routeList);
                        polyLineOptions.width(6);
                        polyLineOptions.color(Color.BLACK);
                    }

                    googleMap.clear();
                    googleMap.addPolyline(polyLineOptions);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(current_location);
                    builder.include(dropPoint);

                    LatLngBounds bounds = builder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int padding = (int) (width * 0.2);

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cameraUpdate);

                    //     dist = getDistance(current_location.latitude, current_location.longitude, dropPoint.latitude, dropPoint.longitude);
//                myCalc(current_location.latitude, current_location.longitude, dropPoint.latitude, dropPoint.longitude);
//                price = CalculateMile(String.valueOf(dist), duration1);
                    /*distance = distance.trim();
                    if (distance.contains("km")) {
                        flagkm =true;
                        distance = distance.replace("km", "");
                        dist = Double.parseDouble(distance);
                    }else if(distance.contains("mile")){
                        distance =distance.replace("mile","");
                        dist = Double.parseDouble(distance);
                    }else if (distance.contains("miles")){
                        distance = distance.replace("miles","");
                        dist = Double.parseDouble(distance);
                    }*/
//                    distance = distance.replace(, "");
                    //18/6/2018 sapan
                    /*distance = distance.substring(0, distance.indexOf(" "));
                    dist = Double.parseDouble(distance);
                    tripTotalDistance = dist;*/
                    if (distance.contains("ft"))
                    {

                        distance = distance.replace(",","");
                        distance = distance.substring(0, distance.indexOf(" "));
                        dist = Double.parseDouble(distance);
                        dist = dist *0.000189394;
                        tripTotalDistance = dist;
                    }
                    else {
                        distance = distance.replace(",","");
                        distance = distance.substring(0, distance.indexOf(" "));
                        dist = Double.parseDouble(distance);
                        tripTotalDistance = dist;
                    }
                   /* if (dist > 2 && dist <= 5) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                        mapZoomLevel = 12;
                    } else if (dist > 5 && dist <= 10) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
                        mapZoomLevel = 11;
                    } else if (dist > 10 && dist <= 20) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f));
                        mapZoomLevel = 11;
                    } else if (dist > 20 && dist <= 40) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                        mapZoomLevel = 10;
                    } else if (dist > 40 && dist < 100) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f));
                        mapZoomLevel = 9;
                    } else if (dist > 100 && dist < 200) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
                        mapZoomLevel = 8;
                    } else if (dist > 200 && dist < 400) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(7.0f));
                        mapZoomLevel = 7;
                    } else if (dist > 400 && dist < 700) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));
                        mapZoomLevel = 7;
                    } else if (dist > 700 && dist < 1000) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
                        mapZoomLevel = 6;
                    } else if (dist > 1000) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                        mapZoomLevel = 5;
                    } else {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
                        mapZoomLevel = 14;
                    }
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);*/
                /*txtEstMentCost.setText("US$" + df.format(price));

                if (userDetails != null) {
                    for (GetUserData userData : userDetails) {
                        if (userData.getData().getCards().get(0).getCard_number() != null) {
                            byte[] data = Base64.decode(userData.getData().getCards().get(0).getCard_number(), Base64.DEFAULT);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                cardNo = new String(data, StandardCharsets.UTF_8);

//                                edtCardNo.setSelected(false);
//                                edtCardNo.setFocusable(false);
//                                edtCardNo.setFocusableInTouchMode(false);
//                                String cn=cardNo.substring(11,15);
                                String s = cardNo;

                                String s4 = s.substring(12, 16);
                                String strCardNumber = "**** **** **** " + s4;
                                edtCardNo.setText(strCardNumber);

                            }
                        }
                    }
                }*/


                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(current_location)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ellipse2))
                            .title("Current Location")
                            .snippet("Home"));

                    googleMap.addMarker(new MarkerOptions()
                            .position(dropPoint)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ellipse2))
                            .title("Drop Location")
                            .snippet("Driver"));
                } else {
                    Log.e("TAG", "No route found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                Bugsnag.notify(throwable);
            }
        }
    }


    public String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();

        System.out.println("get address");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

        try {
            if (addresses.size() > 0) {

                HashMap itemAddress;
                ArrayList itemList = new ArrayList<HashMap<String, String>>();
                Log.d("Addresses", "" + "Start to print the ArrayList");
                for (int i = 0; i < addresses.size(); i++) {
                    itemAddress = new HashMap<String, String>();
                    Address address = addresses.get(i);
                    String addressline = "Addresses from getAddressLine(): ";
                    for (int n = 0; n <= address.getMaxAddressLineIndex(); n++) {
                        addressline += " index n: " + n + ": " + address.getAddressLine(n) + ", ";
                    }
                    Log.d(TAG, "Addresses: " + addressline);
                    Log.d(TAG, "Addresses getAdminArea()" + address.getAdminArea());
                    Log.d(TAG, "Addresses getCountryCode()" + address.getCountryCode());
                    Log.d(TAG, "Addresses getCountryName()" + address.getCountryName());
                    Log.d(TAG, "Addresses getFeatureName()" + address.getFeatureName());
                    Log.d(TAG, "Addresses getLocality()" + address.getLocality());
                    Log.d(TAG, "Addresses getPostalCode()" + address.getPostalCode());
                    Log.d(TAG, "" + address.getPremises());
                    Log.d(TAG, "Addresses getSubAdminArea()" + address.getSubAdminArea());
                    Log.d(TAG, "" + address.getSubLocality());
                    Log.d(TAG, "" + address.getSubThoroughfare());
                    Log.d(TAG, "Addresses getThoroughfare()" + address.getThoroughfare());
                }


                System.out.println("size====" + addresses.size());
                Address address = addresses.get(0);


                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getAddressLine(i));
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");
                    }
                }


                System.out.println("ad==" + address);
                System.out.println("result---" + result.toString());

//                autoComplete_location.setText(result.toString()); // Here is you AutoCompleteTextView where you want to set your string address (You can remove it if you not need it)
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

        return result.toString();
    }

    //    https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
//String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key="+getString(R.string.url);
    private void myCalc(double lat1, double lng1, double lat2, double lng2) {
        /*Toast.makeText(context, "" + trip_id, Toast.LENGTH_SHORT).show();*/
        String source = getAddress(lat1, lng1);
        String des = getAddress(lat2, lng2);
        String url = "" + WebFields.DIST_URL.MODE + WebFields.DIST_URL.ORIGINS + source + WebFields.DIST_URL.DESTINATION + des + "&key=AIzaSyDleLMo3h7J3f6FdG8ELCuArBajMLVFxKM";
        try {
            JSONObject jsonObject = new JSONObject();
            Applog.E("request: " + jsonObject.toString());
//            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
//                    MyProgressDialog.hideProgressDialog();
                    GoogleResponsePojo googleResponsePojo = new Gson().fromJson(response.toString(), GoogleResponsePojo.class);
                    // tvRemainTime.setText("" + googleResponsePojo.getRows().get(0).getElements().get(0).getDuration().getText() + " Away");
                    tvDropInTime.setText("" + googleResponsePojo.getRows().get(0).getElements().get(0).getDuration().getText());
                    //Applog.d("123456",response.optJSONObject("rows").optJSONObject("elements").optJSONObject("distance").optString("text")+":"+response.optJSONObject("rows").optJSONObject("elements").optJSONObject("duration").optString("text"));
                    Applog.E("success: " + response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    MyProgressDialog.hideProgressDialog();
                    error.getStackTrace();
                    Applog.E("Error: " + error.getMessage());
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
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
            finish();
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    /**
     * calculates the distance between two locations in MILES
     */
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {

//        double earthRadius = 3958.75; // in miles,
        double earthRadius = 6371; //for kilometer output
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;
        double totaldistance = dist / 1.6;
        return totaldistance; // output distance, in MILES

//        https://maps.googleapis.com/maps/api/directions/json?origin=\(origin)&destination=\(destination)&mode=driving&key=AIzaSyDuLTaJL-tMzdBoTZtCQfCz4m66iEZ1eQc


    }


    private void driverTripStatus(final int status) {
        /*Toast.makeText(context, "" + trip_id, Toast.LENGTH_SHORT).show();*/
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);

            if (status == 4) {
                jsonObject.put(WebFields.DRIVER_TRIP_STATUS.trip_id, id);
                jsonObject.put(WebFields.DRIVER_TRIP_STATUS.status, status);
                //    jsonObject.put(WebFields.DRIVER_TRIP_STATUS.actual_trip_travel_time, aTime);
                if (trip_price <= base_fee) {
                    trip_price = est_trip_cost;
                }
                /*else
                {
                    if(trip_price < est_trip_cost)
                    {
                        trip_price = est_trip_cost;
                    }
                }*/
                jsonObject.put(WebFields.DRIVER_TRIP_STATUS.actual_trip_amount, trip_price);
                if (dis != 0)
                    jsonObject.put(WebFields.DRIVER_TRIP_STATUS.actual_trip_miles, dis);
                else
                    jsonObject.put(WebFields.DRIVER_TRIP_STATUS.actual_trip_miles, "0");
            } else {
                jsonObject.put(WebFields.DRIVER_TRIP_STATUS.trip_id, id);
                jsonObject.put(WebFields.DRIVER_TRIP_STATUS.status, status);
            }
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.DRIVER_TRIP_STATUS.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());

                    String code = response.optString("error_code");
                    Applog.E("" + code);
                    flag = true;
                    if (status == 2) {
                        driverarrived();
                    } else if (status == 3) {
                        driverstarttrip();
                    } else if (status == 4) {
                        actual_trip_amount = response.optJSONObject("data").optString("actual_trip_amount");
                        driverendtrip();
//                        driverendtrip();
                    }
                    MyProgressDialog.hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    error.getStackTrace();
                    Applog.E("Error: " + error.getMessage());
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
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


    void driveraccpettrip() {
        if (mapFragment != null)
            linedraw();
        latlong = "" + response_data.optJSONObject("data").optJSONObject("body").optString("from_lat");
        latlong = latlong + ",";
        latlong = latlong + response_data.optJSONObject("data").optJSONObject("body").optString("from_lng");
        mCountDownTimer.cancel();
        tvTimer.setVisibility(View.GONE);
        imgVwStart.setVisibility(View.VISIBLE);
        arrive_for_user = response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("name");
        arrive_for_user = "Arrive for " + arrive_for_user;
        drope_for_user = "Drop off " + response_data.optJSONObject("data").optJSONObject("body").optJSONObject("user").optString("name");

        tvTab.setText(arrive_for_user);
    }

    void driverarrived() {
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
    }

    void driverstarttrip() {
        dialogConfirmNavi = new Dialog(this);
        dialogConfirmNavi.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConfirmNavi.setContentView(R.layout.dialog_confirm_navigate);
        TextView tvSure = dialogConfirmNavi.findViewById(R.id.tvSure);
        tvSure.setText(drope_for_user.replace("Drop off ", ""));
        TextView tvUserAddress = dialogConfirmNavi.findViewById(R.id.tvUserAddress);
        RoundedImageView imgVwUserProfile = dialogConfirmNavi.findViewById(R.id.imgVwUserProfile);
        tvUserAddress.setText(response_data.optJSONObject("data").optJSONObject("body").optString("to_address"));
        TextView tvNavigate = (TextView) dialogConfirmNavi.findViewById(R.id.txtVwNavigate);
        tvNavigate.setOnClickListener(this);
        dialogConfirmNavi.show();
        GoogleMap googleMap = null;
        MapView mMapView = (MapView) dialogConfirmNavi.findViewById(R.id.map);
        MapsInitializer.initialize(this);
        mMapView = (MapView) dialogConfirmNavi.findViewById(R.id.map);
//        customGoogleMap=this.googleMap;
        mMapView.onCreate(dialogConfirmNavi.onSaveInstanceState());
        if (url != null) {
            Glide.clear(imgVwUserProfile);
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .crossFade().skipMemoryCache(true)
                    .into(imgVwUserProfile);

        } else {
            imgVwUserProfile.setImageResource(R.mipmap.ic_place_holder);
        }
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng sydney = new LatLng(-33.867, 151.206);


                customGoogleMap = googleMap;
                getLocationPermission();
                if (mLocationPermissionGranted == true) {
                    if (mGoogleApiClient == null) {
                        setUpGoogleApiClient();
                    }
                    customGoogleMap.setMyLocationEnabled(true);
                    customGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    customGoogleMap.getUiSettings().setCompassEnabled(false);
                    MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
                    customGoogleMap.setMapStyle(mapStyle);
                }
                dropPoint = new LatLng(Double.valueOf(response_data.optJSONObject("data").optJSONObject("body").optString("to_lat")), Double.valueOf(response_data.optJSONObject("data").optJSONObject("body").optString("to_lng")));
                customGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dropPoint, 13));
                customGoogleMap.addMarker(new MarkerOptions()
                        .title("Sydney")
                        .snippet("The most populous city in Australia.")
                        .position(dropPoint));
            }
        });
    }

    String tripStartTime, tripEndTime, tripToatalTime;
    double diffTotelMin;
    int tripTotalTime;
    Double tripTotalDistance;

    void driverendtrip() {

//        MyProgressDialog.hideProgressDialog();
        Intent i = new Intent(this, UserRateActivity.class);
//        i.putExtra("rate", response_trip.optJSONObject("data").optString("total_estimated_trip_cost"));


        i.putExtra("rate", String.valueOf(trip_price));
        i.putExtra("actual_trip_amount", actual_trip_amount);
        i.putExtra("est_trip_cost", String.valueOf(est_trip_cost));
        i.putExtra("base_fee", String.valueOf(base_fee));
        i.putExtra("trip_id", "" + id);
        i.putExtra("profilePic", url);
        i.putExtra("uname", username);
        i.putExtra("trip_disc", (response_data.optJSONObject("data")).optJSONObject("body").optString("from_address")
                + " to " + (response_data.optJSONObject("data")).optJSONObject("body").optString("to_address"));
//        i.putExtra("card_token", "tok_1CNLuSKIlUXS2P2yfBTWvGQy");
        i.putExtra("card_token", response_trip.optJSONObject("data").optString("card_token"));
        startActivity(i);
        finish();
    }


    private double CalculateTime(String tripStartTime, String tripEndTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date startDate = simpleDateFormat.parse(tripStartTime);
            Date endDate = simpleDateFormat.parse(tripEndTime);

            long difference = endDate.getTime() - startDate.getTime();

            int timeInSeconds = (int) (difference / 1000);
            tripToatalTime = (timeInSeconds) + "";
            int hours, minutes, seconds;
            hours = timeInSeconds / 3600;
            timeInSeconds = timeInSeconds - (hours * 3600);
            minutes = timeInSeconds / 60;
            timeInSeconds = timeInSeconds - (minutes * 60);
            seconds = timeInSeconds;

            String diffTime = (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + " h";
            Applog.E("Total Time==>" + diffTime);
            //tripToatalTime = (timeInSeconds) + "";
            double a = timeInSeconds * 0.0166667;
            Applog.E("Total Time===>" + a);
            int strTotalMinute = (hours * 60) + minutes;
            Log.d("total minute", String.valueOf(strTotalMinute));
      /*      if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
            }
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            Log.i("log_tag", "Hours: " + hours + ", Mins: " + min);
*/
            diffTotelMin = strTotalMinute;

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

//        return diffTotelMin;
        return Double.parseDouble(tripToatalTime);
    }

    private String CalculateMile(double strDistance, double strTime) {
        double dis = strDistance;
        double time = strTime;
        if (dis <= 10) {
            trip_price = base_fee + (time * time_fee) + (dis * mile_fee);
        } else {
            trip_price = base_fee + (time * time_fee) + (10 * mile_fee) + ((dis - 10) * over_mile_fee);
        }
        Applog.E("==>" + trip_price);
        return trip_price + "";
    }

    public class GoogleResponsePojo {

        private List<String> destination_addresses;
        private List<String> origin_addresses;
        private List<Rows> rows;
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setDestination_addresses(List<String> destination_addresses) {
            this.destination_addresses = destination_addresses;
        }

        public void setOrigin_addresses(List<String> origin_addresses) {
            this.origin_addresses = origin_addresses;
        }

        public void setRows(List<Rows> rows) {
            this.rows = rows;
        }

        public List<String> getDestination_addresses() {
            return destination_addresses;
        }

        public List<String> getOrigin_addresses() {
            return origin_addresses;
        }

        public List<Rows> getRows() {
            return rows;
        }
//getters
    }


    private static class Rows {
        private List<Element> elements;

        public void setElements(List<Element> elements) {
            this.elements = elements;
        }

        public List<Element> getElements() {
            return elements;
        }
//getters
    }

    private static class Element {
        private TextValue distance;
        private TextValue duration;
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setDistance(TextValue distance) {
            this.distance = distance;
        }

        public void setDuration(TextValue duration) {
            this.duration = duration;
        }

        //getters

        public TextValue getDistance() {
            return distance;
        }

        public TextValue getDuration() {
            return duration;
        }
    }

    private static class TextValue {
        private String text;
        private String value;

        public void setText(String text) {
            this.text = text;
        }

        public void setValue(String value) {
            this.value = value;
        }

        //getters

        public String getText() {
            return text;
        }

        public String getValue() {
            return value;
        }
    }

    String stripPay(final String card_token) {
        Stripe stripe;
        Card card = new Card(card_token, 05, 2018, "123");
// Remember to validate the card object before you use it to save time.
        if (!card.validateCard()) {
            // Do not continue token creation.
            SnackBar.showError(context, snackBarView, "card not valid.");
//            Toast.makeText(this, "card not valid", Toast.LENGTH_SHORT).show();
        }


        try {
            MyProgressDialog.showProgressDialog(context);
            stripe = new Stripe("pk_test_GF0y48SCqViKdCSA8LwOPFVj");
            googleMap.clear();
            customGoogleMap.clear();
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        @SuppressLint("StaticFieldLeak")
                        public void onSuccess(Token token) {
                            // Send token to your server
                            Log.e("check token", ":::::" + token.getId());
                            Log.e("check token all data", ":::::" + token);
                            card1 = token.getId();
                            driverendtrip();
                        }

                        public void onError(Exception error) {
                            // Show localized error message
                            Log.e("check", "erorr" + error.getMessage());

                        }
                    }
            );


        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
        return card1;
    }

    String card1;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }


    String statCode;

    public String getstatCode(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();

        System.out.println("get address");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
        try {
            if (addresses.size() > 0) {
                System.out.println("size====" + addresses.size());
                Address address = addresses.get(0);

                statCode = address.getAdminArea();

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getAddressLine(i));
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");
                    }
                }

                System.out.println("ad==" + address);
                System.out.println("result---" + result.toString());

//                autoComplete_location.setText(result.toString());
// Here is you AutoCompleteTextView where you want to set your string address (You can remove it if you not need it)
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

        return result.toString();
    }

    double time, dis;

    private void callTripRateAPI(String statCode, final Double tripTotalDistance, final Double aTime) {
        try {

            JSONObject jsonObject = new JSONObject();
            if (statCode.isEmpty()) {
                jsonObject.put(WebFields.TRIP.PARAM_STATE, "NY");
            } else {
                jsonObject.put(WebFields.TRIP.PARAM_STATE, statCode);
            }


            Applog.E("request Trip Price => " + jsonObject.toString());
//            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.TRIP.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    GetTripRate tripRateData = new Gson().fromJson(String.valueOf(response), GetTripRate.class);
                    try {
//                        MyProgressDialog.hideProgressDialog();
                        if (tripRateData.getError_code() == 0) {
                            base_fee = Float.parseFloat("" + tripRateData.getData().getBase_fee());
                            time_fee = Float.parseFloat("" + tripRateData.getData().getTime_fee());
                            mile_fee = Float.parseFloat("" + tripRateData.getData().getMile_fee());
                            over_mile_fee = Float.parseFloat("" + tripRateData.getData().getOvermile_fee());
                            cancel_fee = Float.parseFloat("" + tripRateData.getData().getCancel_fee());
                            //   CalculateMile(str,elapsedTime);


                            if (tripTotalDistance != null && aTime != null) {
                                dis = tripTotalDistance;
                                time = aTime;
//                                if (flagkm==true)
                                //    dis = (float) (dis * 0.621371);
                                time_fee = time_fee / 60;
                                if (dis <= 10) {
                                    trip_price = base_fee + (time * time_fee) + (dis * mile_fee);
                                } else {
                                    trip_price = base_fee + (time * time_fee) + (10 * mile_fee) + ((dis - 10) * over_mile_fee);
                                }
                            }
                            /*if(trip_price == base_fee)
                            {
                                trip_price = est_trip_cost;
                            }else
                            {
                                if(trip_price < est_trip_cost)
                                {
                                    trip_price = est_trip_cost;
                                }
                            }*/

                            Applog.E("==>" + trip_price);
                            driverTripStatus(trip_status + 3);

                        } else {
//                            MyProgressDialog.hideProgressDialog();
                            SnackBar.showError(context, snackBarView, response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        Bugsnag.notify(throwable);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());

                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
//                    String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImE4MTc0OTQxMjA5YWQxNGRiYzhkMzBjNTI1ZWY3ZWQ4ZWI0YzhiYjEzNzY4YTYyNDU5YTM0NDg5NTFhYjNmYTE3MjUyOTI4NzJkNGY1NjRkIn0.eyJhdWQiOiIxIiwianRpIjoiYTgxNzQ5NDEyMDlhZDE0ZGJjOGQzMGM1MjVlZjdlZDhlYjRjOGJiMTM3NjhhNjI0NTlhMzQ0ODk1MWFiM2ZhMTcyNTI5Mjg3MmQ0ZjU2NGQiLCJpYXQiOjE1MjM3MDM5MzgsIm5iZiI6MTUyMzcwMzkzOCwiZXhwIjoxNTU1MjM5OTM4LCJzdWIiOiI0Iiwic2NvcGVzIjpbXX0.CZNL841VWZnk_Pd7CMCtcfqP0NtefI8Y6-M4qVCkmr5ryYmC9EwR7R5446zpwr7f6h-XrII92F_6Q3frd_86RG_9j0Ye_oTOuk2QscPULU2CACsyChC6quzZWiEQDj7MKwsVogNeTqhNFgo_-TtpTxMBRlaIjqSjrydiWczrc9hCh4iD5kZEPBU_5GxcFlBI4SgVHiJTZcc1CNyb_iLc4zq0OHQHWlczJELGH9V8wVIEm8qrc1wQYqRsHo3Sb1uSlbyoqEPKLsPspqHAG-xwTKS5b0__6-KfteeDnfapCjKl6Ll-_U-jpdzhrsvPv67nMcYMW0arQUVl1AWuhO5B6tUEvQI12mBB8Pyfd9ayHJsmc27oBaB_cEQlAR31Jz2nrfgIisST8mZ8ylNtJnX1Xk0f5LBDst8E5UFEX5OtWdzOeQHIt78WEzVSyV6pQHhESK_XiizoBaHndOZ3Yac6W8EzB3SWWTJ10bQiyBt88UOpgHkc_dNrc0GCtOkN1n6SUGd24RsS2zPXk3vSEL2rncT5JXzo82RZVCtdGqFdl8mXuta6hAtyeknRo94xPjAkwI2qbWiztw28eN_bQIPoSCBbNkoyVbojDIAI-0Ww0mLhh5_zVQCYMjXtJIrYPXDDSBL7crxgxUahoblGCsLmumybBizPgZeuA9E6tHiYnHg";
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(WebFields.PARAM_ACCEPT, "application/json");
//                    params.put(WebFields.PARAM_AUTHOTIZATION,GlobalValues.BEARER_TOKEN+token);
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }
}
