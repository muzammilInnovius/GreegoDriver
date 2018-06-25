package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Adapter.DrawerLayoutAdapter;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.FCM.Config;
import com.greegoapp.greegodriver.FCM.FCMIDService;
import com.greegoapp.greegodriver.Fragment.DriverEarningFragment;
import com.greegoapp.greegodriver.Fragment.MapHomeFragment;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityHomeBinding;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private static final String TAG = HomeActivity.class.getName();
    ActivityHomeBinding binding;
    Context context;
    private View snackBarView;


    private RelativeLayout navHeader;
    private ImageView ivPro, ivProPicHome;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    private DrawerLayout drawer_layout;
    private ListView menuList;
    private static RelativeLayout drawerlist;
    android.app.FragmentManager manager;
    private FrameLayout container_body;
    TextView tvDrawUsername, tvDriveGreego;
    private Fragment mContentFragment = null;
    private String[] drawerTitle = {"Home", "Dashboard", "Help", "Settings"};
    private int[] drawerImage = {R.mipmap.ic_map, R.mipmap.dashboard, R.mipmap.ic_help, R.mipmap.ic_settings};
    public static int index = 0;
    //sapan
    //GoogleMap 24/4/2018
    public GoogleMap googleMap;
    MapFragment mapFragment;
    private boolean mLocationPermissionGranted;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    LatLng latLng;
    LocationRequest locationRequest;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int REQ_PERMISSION = 999;
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    //Priyanka 21-4
    GetDriverData.DataBean driverDetails;
    ArrayList<GetDriverData> alDriverList;
    String driverName;
    private Stack<Fragment> fragmentStack;
    FragmentManager fragmentManager;
    public static final int MY_PERMISSION_REQUEST = 1;
    //ajit
    ImageView iv_clickOnOff;
    int x = 1;
    private String strProfilePic;
    public String device_id;
    private boolean flag;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private String approved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, FCMIDService.class));
        drawerLayoutAdapter = new DrawerLayoutAdapter(HomeActivity.this, drawerTitle, drawerImage);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        context = HomeActivity.this;
        Bugsnag.init(context);
        device_id = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("regId", "");
        fragmentStack = new Stack<Fragment>();
        fragmentManager = this.getSupportFragmentManager();
        snackBarView = findViewById(android.R.id.content);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manager = getFragmentManager();
        bindView();
        setListners();

        //priyanka 20-4
        if (ConnectivityDetector.isConnectingToInternet(context)) {
            callUserMeApi();
            Glide.with(getApplicationContext()).load(strProfilePic).into(ivPro);
//            CheckGpsStatus();?
        } else {
            SnackBar.showInternetError(context, snackBarView);
            //   Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
        }
     /*  String mile = String.format("%.1f",Double.valueOf(1.68));
        Double dis = Double.valueOf(mile);
        Log.e("str", String.valueOf(dis));*/
        setHomeValues();
        slideMenu();

        String language = Locale.getDefault().getLanguage().toString();
        Log.i("language", language);
        drawerlist.requestDisallowInterceptTouchEvent(true);
        navHeader.requestDisallowInterceptTouchEvent(true);


        iv_clickOnOff = findViewById(R.id.iv_clickOnOff);
       /* iv_clickOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (x % 2 == 0) {
                    //pass actual device id
               //     iv_clickOnOff.setImageResource(R.mipmap.ic_on);
                    *//*device_id = "null";
                    Toast.makeText(context,device_id,Toast.LENGTH_LONG).show();*//*
                    showAlertDialog("off");
                  *//*  callChangeDeviceIdAPI(0);
                    stopService(new Intent(HomeActivity.this,FCMIDService.class));*//*
                    //x++;
                } else {
                    //pass null device id
                  //  iv_clickOnOff.setImageResource(R.mipmap.off);
                    *//*device_id =  SessionManager.getToken(context);
                    Toast.makeText(context,device_id,Toast.LENGTH_LONG).show();*//*
                    showAlertDialog("on");
                   *//* callChangeDeviceIdAPI(1);
                    startService(new Intent(HomeActivity.this,FCMIDService.class));*//*
                   // x++;
                }
            }
        });*/
        iv_clickOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ///   callUserMeApi();
                    if (alDriverList.size() != 0) {

                        for (GetDriverData userData : alDriverList) {
                            //if (userData.getData().getProfile_pic() != null && userData.getData().getProfile_status() == 7) {
                            if (userData.getData().getIs_approve().equals("0")) {
                                // showCheckUserUpdateData("Please complete your updates before proceeding.");
                            } else if (userData.getData().getIs_approve().equals("1")) {
                                if (x % 2 == 0) {
                                    //pass actual device id
                                    //     iv_clickOnOff.setImageResource(R.mipmap.ic_on);
                    /*device_id = "null";
                    Toast.makeText(context,device_id,Toast.LENGTH_LONG).show();*/
                                    showAlertDialog("OFF");
                  /*  callChangeDeviceIdAPI(0);
                    stopService(new Intent(HomeActivity.this,FCMIDService.class));*/
                                    //x++;
                                } else {
                                    //pass null device id
                                    //  iv_clickOnOff.setImageResource(R.mipmap.off);
                    /*device_id =  SessionManager.getToken(context);
                    Toast.makeText(context,device_id,Toast.LENGTH_LONG).show();*/
                                    showAlertDialog("ON");
                   /* callChangeDeviceIdAPI(1);
                    startService(new Intent(HomeActivity.this,FCMIDService.class));*/
                                    // x++;
                                }
                            }
                            //  openDrawer();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    Bugsnag.notify(throwable);
                }
//------------------------------------end-----------------------------------------------

            }
        });

        driverOn();
    }

    private void driverOn() {
        iv_clickOnOff.setImageResource(R.mipmap.off);
        callChangeDeviceIdAPI(1);
        startService(new Intent(HomeActivity.this, FCMIDService.class));
        x++;
    }

    String changeStatus;

    private void showAlertDialog(final String btn_status) {

        if (btn_status.equals("ON")) {
            changeStatus = "OFF";
        } else if (btn_status.equals("OFF")) {
            changeStatus = "ON";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Greego").setMessage("Are you sure you want to change from " + changeStatus + " to " + btn_status + "?");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (ConnectivityDetector
                        .isConnectingToInternet(context)) {

                    dialog.dismiss();

                } else {
                    //SnackBar.showInternetError(context, snackBarView);
                }
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (btn_status.equals("OFF")) {
                    iv_clickOnOff.setImageResource(R.mipmap.ic_on);
                    callChangeDeviceIdAPI(0);
                    stopService(new Intent(HomeActivity.this, FCMIDService.class));
                    x++;
                } else {
                    iv_clickOnOff.setImageResource(R.mipmap.off);
                    callChangeDeviceIdAPI(1);
                    startService(new Intent(HomeActivity.this, FCMIDService.class));
                    x++;
                }

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callChangeDeviceIdAPI(int driver_on) {
        // Toast.makeText(context, ""+driver_on, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.CHANGE_DEVICE_ID.driver_on, driver_on);
            Applog.E("request: " + jsonObject.toString());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.CHANGE_DEVICE_ID.MODE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Applog.E("success: " + response.toString());
                            //  Toast.makeText(context, "device changed"+response, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Applog.E("Error: " + error.getMessage());
                    //SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    Applog.E("Token==>" + SessionManager.getToken(context));
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

    private void sendLatlong(String lat, String lng) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.SEND_LATLNG.lat, lat);
            jsonObject.put(WebFields.SEND_LATLNG.lng, lng);
            Applog.E("request: " + jsonObject.toString());


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SEND_LATLNG.MODE, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Applog.E("success: " + response.toString() + "location updated");
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Applog.E("location fail Error: " + error.getMessage());
                    //SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    Applog.E("Token==>" + SessionManager.getToken(context));
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


    private void callUserMeApi() {
        try {
            JSONObject jsonObject = new JSONObject();

            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.USER_ME.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    SharedPreferences.Editor editor = getSharedPreferences("driverData", 0).edit();
                    editor.putString("driver", response.toString());
                    editor.commit();

                    driverDetails = new Gson().fromJson(String.valueOf(response), GetDriverData.DataBean.class);
                    GetDriverData driverDetail = new Gson().fromJson(String.valueOf(response), GetDriverData.class);
                    if (driverDetail.getData().getIs_approve().equals("0")) {
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    } else {
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                    try {
                        MyProgressDialog.hideProgressDialog();
                        alDriverList = new ArrayList<>();

                        if (driverDetail.getError_code() == 0) {
                            callApiForFee();

                            alDriverList.add(driverDetail);

                            Applog.E("UserUpdate==>Dg==>" + driverDetail);

                            strProfilePic = driverDetail.getData().getProfile_pic();
                            String userName = driverDetail.getData().getName();
                            tvDrawUsername.setText(userName);

                            approved = driverDetail.getData().getIs_approve();

                            Glide.clear(ivProPicHome);
                            if (strProfilePic != null) {
                                Glide.with(context)
                                        .load(strProfilePic)
                                        .centerCrop()
                                        .signature(new StringSignature(UUID.randomUUID().toString()))
                                        .crossFade().skipMemoryCache(true)
                                        .into(ivProPicHome);
                            }

                            Glide.clear(ivPro);
                            if (strProfilePic != null) {
                                Glide.with(context)
                                        .load(strProfilePic)
                                        .centerCrop()
                                        .signature(new StringSignature(UUID.randomUUID().toString()))
                                        .crossFade().skipMemoryCache(true)
                                        .into(ivPro);
                            }
//                            Glide.with(context).load(strProfilePic).into(ivProPicHome);
//                            Glide.with(context).load(strProfilePic).into(ivPro);

                            if (Build.VERSION.SDK_INT < 23) {
                                setHomeValues();
                            } else {
                                if (checkSelfPermission()) {
                                    setHomeValues();
                                }
                            }

//                            SessionManager.saveUserData(context, userDetails);
//                            SnackBar.showSuccess(context, snackBarView, response.getString("message"));
//
                            //getIs_agreed = 0 new user

//
                        } else if (driverDetail.getError_code() == 1) {
                            logout();
                        } else {
                            MyProgressDialog.hideProgressDialog();
                            //   SnackBar.showError(context, snackBarView, response.getString("message"));
                        }
                    } catch (/*JSON*/Exception e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        Bugsnag.notify(throwable);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());

                    //SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    Applog.E("Token==>" + SessionManager.getToken(context));
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

    private void logout() {
        callChangeDeviceIdAPI(0);
        stopService(new Intent(HomeActivity.this, FCMIDService.class));
        SessionManager.clearAppSession(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean checkSelfPermission() {
        int AccessCorasLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int AccessFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        final List<String> permissionNeeded = new ArrayList<>();

        if (AccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (AccessCorasLocation != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }


        flag = false;
        if (!permissionNeeded.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("For Greego service, we may collect location data while using an app. This improves pickups, support, and more.");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            permissionNeeded.toArray(new String[permissionNeeded.size()]), MY_PERMISSION_REQUEST);
                    flag = false;
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkSelfPermission();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


        } else {
            flag = true;
        }
        return flag;
    }

    private void slideMenu() {
        try {
            DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) drawerlist.getLayoutParams();
            drawerlist.setLayoutParams(layoutParams);

            menuList.setAdapter(drawerLayoutAdapter);
            if (drawer_layout.isDrawerOpen(drawerlist)) {
                drawer_layout.closeDrawer(drawerlist);
            }

            menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                    drawer_layout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                        }
                    });

                    drawer_layout.closeDrawer(drawerlist);
                    index = pos;

                    displayView(pos);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    private void setHomeValues() {
        try {

            Fragment fragmentPro = null;
            fragmentPro = new MapHomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.replace(R.id.containerBody, fragmentPro);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            mContentFragment = fragmentPro;
            drawer_layout.closeDrawer(drawerlist);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Greego").setMessage("Are you sure to exit Greego App ?");


            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        dialog.dismiss();

                    } else {
                        //SnackBar.showInternetError(context, snackBarView);
                    }
                }
            });

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
        //  finish();
     /*   Fragment fragmentPro = null;
        fragmentPro = new MapHomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.containerBody, fragmentPro);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
    }

    private void setListners() {
        ivProPicHome.setOnClickListener(this);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap1) {
                LatLng sydney = new LatLng(-33.867, 151.206);
                googleMap = googleMap1;
                getLocationPermission();
                if (mLocationPermissionGranted == true) {
                    if (mGoogleApiClient == null) {
                        setUpGoogleApiClient();
                        CheckGpsStatus();
                    }
                    googleMap.setMyLocationEnabled(true);
//                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(false);
                    MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
                    googleMap.setMapStyle(mapStyle);
                }


                /*googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                googleMap.addMarker(new MarkerOptions()
                        .title("Sydney")
                        .snippet("The most populous city in Australia.")
                        .position(sydney));*/

            }
        });
    }

    private void bindView() {
        navHeader = binding.navHeader;
        drawer_layout = binding.drawerLayout;
        ivProPicHome = findViewById(R.id.ivProPicHome);
        container_body = findViewById(R.id.containerBody);

        drawerlist = binding.drawerlist;
        ivPro = binding.ivPro;
        tvDrawUsername = binding.tvDrawUsername;
        menuList = binding.menuList;
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mgooleMap);
        /*     tvDriveGreego = binding.tvDriveGreego;*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivProPicHome:
                try {
                    ///   callUserMeApi();
                    if (alDriverList.size() != 0) {

                        for (GetDriverData userData : alDriverList) {
                            //if (userData.getData().getProfile_pic() != null && userData.getData().getProfile_status() == 7) {
                            if (userData.getData().getIs_approve().equals("0")) {
                                //showCheckUserUpdateData("Please complete your updates before proceeding.");
                            } else if (userData.getData().getIs_approve().equals("1")) {
                                openDrawer();
                            }
                            //  openDrawer();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    Bugsnag.notify(throwable);
                }
                break;
        }
    }

    private void showCheckUserUpdateData(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Greego").setMessage(msg);


            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {

                        dialog.dismiss();

                    } else {
                        //SnackBar.showInternetError(context, snackBarView);
                    }
                }
            });

//            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.dismiss();
//                }
//            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    public void openDrawer() {
        try {
//            setvalues();

            container_body.setClickable(false);
            drawerlist.setClickable(true);
            navHeader.setClickable(true);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            menuList.invalidateViews();
            drawer_layout.openDrawer(drawerlist);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    private void closeDrawer() {
        try {
            if (drawer_layout.isDrawerOpen(drawerlist))
                drawer_layout.closeDrawer(drawerlist);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mapFragment.onResume();
            if (googleMap != null) {
                googleMap.clear();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (lastLocation != null) {
                    writeLastLocation();
                    startLocationUpdates();


                    double lat = lastLocation.getLatitude();
                    double lng = lastLocation.getLongitude();

                } else {
                    Log.w(TAG, "No location retrieved yet");
                    startLocationUpdates();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayView(final int pos) {
        try {
            closeDrawer();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = null;
                    switch (pos) {
                        case 0:
                            Fragment fragmentHome = null;
                            fragmentHome = new MapHomeFragment();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            fragmentTransaction.replace(R.id.containerBody, fragmentHome);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            //SnackBar.showValidationError(context, snackBarView, getString(R.string.in_progress));
                            break;

                        case 1:
                            Fragment fragmentPro = null;
                            fragmentPro = new DriverEarningFragment();
                            FragmentManager fragmentManager1 = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                            fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            fragmentTransaction1.replace(R.id.containerBody, fragmentPro);
                            fragmentTransaction1.addToBackStack(null);
                            fragmentTransaction1.commit();
                            //SnackBar.showValidationError(context, snackBarView, getString(R.string.in_progress));
                            break;

                        case 2:
                            Intent intentHelp = new Intent(context, HelpActivity.class);
                            startActivity(intentHelp);
                            //SnackBar.showValidationError(context, snackBarView, getString(R.string.in_progress));
                            break;

                        case 3:
                            Intent intentSetting = new Intent(context, SettingActivity.class);
                            startActivityForResult(intentSetting, 2000);
                            //  fragment = new HelpFragment();
                            break;


//                        default:
//                            break;
                    }

                    if (fragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.containerBody, fragment);
                        fragmentTransaction.commit();
                        mContentFragment = fragment;
                    }

                    drawer_layout.closeDrawer(drawerlist);
                    drawerLayoutAdapter.setSelectedIndex(pos);
                }
            }, 200);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2000) {
            callUserMeApi();
            if (!isGPSEnable()) {
                CheckGpsStatus();
            } else {
                //   if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                // }
                //   setListners();
            }
            //  CheckGpsStatus();
//            Glide.clear(ivProPicHome);
//            Glide.with(getApplicationContext())
//                    .load(strProfilePic)
//                    .centerCrop()
//                    .signature(new StringSignature(UUID.randomUUID().toString()))
//                    .crossFade().skipMemoryCache(true)
//                    .into(ivProPicHome);

            Glide.clear(ivPro);
            Glide.with(getApplicationContext())
                    .load(strProfilePic)
                    .centerCrop()
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .crossFade().skipMemoryCache(true)
                    .into(ivPro);


//            Glide.with(getApplicationContext()).load(strProfilePic).into(ivPro);
        } else if (requestCode == 1212) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        Bugsnag.notify(throwable);
                    }
                }
            }).run();

            if (!isGPSEnable()) {
                CheckGpsStatus();
            } else {
                //   if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                // }
                setListners();
            }
        }

    }

    public boolean isGPSEnable() {
        boolean isGPSEnable = false;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnable;
    }
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
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("For Greego service, we may collect location data while using an app. This improves pickups, support, and more.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();

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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("For Greego service, we may collect location data while using an app. This improves pickups, support, and more.")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(HomeActivity.this,
                                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

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
//                    Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
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
        if (isGPSEnable()) {
            Log.d(TAG, "onLocationChanged [" + location + "]");
            lastLocation = location;
            sendLatlong(lastLocation.getLatitude() + "", "" + lastLocation.getLongitude());
        } else {
            //CheckGpsStatus();
            if (!isGPSEnable()) {
                CheckGpsStatus();
            } else {
                //   if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                // }
                //  setListners();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //sapan-->location over*/
    class OnOffDriver extends AsyncTask<Void, Void, String> {

        String duety;

        OnOffDriver(String duety) {
            this.duety = duety;
        }

        @Override
        protected String doInBackground(Void... voids) {

            return "true";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void callApiForFee() {
        GetDriverData myDriver = new Gson().fromJson(getSharedPreferences("driverData", 0).getString("driver", "").toString(), GetDriverData.class);
        String address = myDriver.getData().getShipping_adress().getState();
        try {
            if (address != "") {
//                address = address.substring(address.indexOf("(") + 1, address.indexOf(")"));
            }

            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.TRIP.PARAM_STATE, address);

            Applog.E("request t State => " + jsonObject.toString());
            /*       MyProgressDialog.showProgressDialog(context);*/
            /*"http://kroslinkstech.in/greego/public/api/driver/express/pay"*/
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, WebFields.BASE_URL + WebFields.TRIP.MODE
                    , jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    SharedPreferences.Editor editor = getSharedPreferences("driverData", 0).edit();
                    editor.putString("fee", response.toString());
                    editor.commit();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    /*   MyProgressDialog.hideProgressDialog();*/
                    Applog.E("Error: " + error.getMessage());

                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            }) {
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

    }

    private void CheckGpsStatus() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GpsStatus == true) {
//            Toast.makeText(this, "Location Services Is Enabled", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Location Services Is Disabled", Toast.LENGTH_SHORT).show();
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Please enable GPS first.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 1212);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
