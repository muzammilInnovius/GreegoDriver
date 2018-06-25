package com.greegoapp.greegodriver.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Activity.AcceptUserRequestActivity;
import com.greegoapp.greegodriver.Activity.DriverAttachFileInfoActivity;
import com.greegoapp.greegodriver.Activity.DriverBankInfoActivity;
import com.greegoapp.greegodriver.Activity.DriverPersonalInfoActivity;
import com.greegoapp.greegodriver.Activity.DriverProfileInfoActivity;
import com.greegoapp.greegodriver.Activity.DriverShippingInfoActivity;
import com.greegoapp.greegodriver.Activity.DriverTypeInfoActivity;
import com.greegoapp.greegodriver.Activity.HomeActivity;
import com.greegoapp.greegodriver.Activity.MainActivity;
import com.greegoapp.greegodriver.Activity.SignUpEmailActivity;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.FragmentMapHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.databinding.FragmentMapHomeBinding;

public class MapHomeFragment extends Fragment implements View.OnClickListener {
    FrameLayout frameLayout;
    FragmentMapHomeBinding binding;
    GetDriverData userDetail;
    View snackBarView;
    Context context;
    Button slideButton;
    SlidingDrawer slidingDrawer;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //priyanka 20-4
    ArrayList<GetDriverData> userDetails = new ArrayList<>();
    TextView tvPersonalInfo, tvShippingInfo, tvAttachFiles, tvBankInfo, tvDriverType, tvProfile;
    public static final int REQUEST_ADD_PERSONAL_INFO = 1001;
    public static final int REQUEST_ADD_SHIPPING_INFO = 1002;
    public static final int REQUEST_ADD_FILES_INFO = 1003;
    public static final int REQUEST_ADD_BANK_INFO = 1004;
    public static final int REQUEST_ADD_DRIVER_INFO = 1005;
    public static final int REQUEST_ADD_PROFILE_INFO = 1006;
    int profileStatus;
    TextView tvPercentage;
    Button btnFinish;
    RelativeLayout rlVwUpdateMain, rlContentLayout;
    ImageView imgVwRemainMark, imgVwAlert;
    LinearLayout linlay1, linlay2, linlay3;

    int open = 0;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int REQ_PERMISSION = 999;
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    int per;

    public MapHomeFragment() {
    }

    public static MapHomeFragment newInstance(String param1, String param2) {
        MapHomeFragment fragment = new MapHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.close")) {
                showCustomAlert(intent.getStringExtra("msg")+".");
                callDriverMeApi();
                Animation bottomDown = AnimationUtils.loadAnimation(getContext(),
                        R.anim.bottom_down);
                rlVwUpdateMain.startAnimation(bottomDown);
                rlContentLayout.startAnimation(bottomDown);
                rlContentLayout.setVisibility(View.GONE);
                rlVwUpdateMain.setVisibility(View.GONE);
                imgVwAlert.setVisibility(View.GONE);
                imgVwRemainMark.setVisibility(View.GONE);
                rlContentLayout.setVisibility(View.GONE);
               /* Intent i = new Intent(context,HomeActivity.class);
                startActivity(i);*/
            }
            Log.d("message", "message");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*   mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        if (getArguments() != null) {
            userDetails = getArguments().getParcelableArrayList("userData");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_home, container, false);

        View view = binding.getRoot();
        snackBarView = getActivity().findViewById(android.R.id.content);
        context = getActivity();
        Bugsnag.init(context);
        bindViews();

        setListner();
        /*  setFirstTimeRegister();*/
        callDriverMeApi();

     /*   slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                // Change button text when slider is open
                slideButton.setText(getResources().getString(R.string.update));

            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {

                // Change button text when slider is close
                slideButton.setText(getResources().getString(R.string.update));
         *//*       slideButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_remain_mark,0);*//*
            }
        });*/

        return view;
    }

  /*  private void setFirstTimeRegister() {
            if(profileStatus==1)
            {
                tvPersonalInfo.setTextColor(getResources().getColor(R.color.app_bg));
                tvPersonalInfo.setClickable(false);
            }

    }*/

    private void callDriverMeApi() {
        try {
            JSONObject jsonObject = new JSONObject();

            Applog.E("request: " + jsonObject.toString());
//            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.USER_ME.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    try {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("driverData", 0).edit();
                        editor.putString("driver", response.toString());
//                   GetUserData.DataBean userDtl = new Gson().fromJson(String.valueOf(response), GetUserData.DataBean.class);

                        userDetail = new Gson().fromJson(String.valueOf(response), GetDriverData.class);
                        try {
                            MyProgressDialog.hideProgressDialog();
                            userDetails = new ArrayList<>();

                            if (userDetail.getError_code() == 0) {


                                userDetails.add(userDetail);
                                profileStatus = userDetail.getData().getProfile_status();
                                String profileaprrove = userDetail.getData().getIs_approve();
                                int status = calculatePercentage(profileStatus);
                                if (status == 100) {
                                    if (profileaprrove.equals("1")) {
                                        rlVwUpdateMain.setVisibility(View.GONE);
                                        imgVwAlert.setVisibility(View.GONE);
                                        imgVwRemainMark.setVisibility(View.GONE);
                                    } else {
                                        String message = "Your application is under review";
                                        tvPercentage.setText(message);
                                        rlVwUpdateMain.setVisibility(View.VISIBLE);
                                        imgVwRemainMark.setVisibility(View.VISIBLE);
                                        imgVwAlert.setVisibility(View.VISIBLE);
                                        btnFinish.setVisibility(View.GONE);
                                    }
                                } else {
                                    String message = "Your application is " + status + "% complete!";
                                    tvPercentage.setText(message);
                                    rlVwUpdateMain.setVisibility(View.VISIBLE);
                                    imgVwRemainMark.setVisibility(View.VISIBLE);
                                    imgVwAlert.setVisibility(View.VISIBLE);
                                }
//                            //getIs_agreed = 0 new user
                                //     setProfileScreen(profileStatus);
                                /*      setFirstTimeRegister();*/

//                            SessionManager.saveUserData(context, userDetails);
//                            SnackBar.showSuccess(context, snackBarView, response.getString("message"));
//
                                //getIs_agreed = 0 new user

//
                            } else {
                                MyProgressDialog.hideProgressDialog();
                                SnackBar.showError(context, snackBarView, response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Throwable throwable)
                        {
                            Bugsnag.notify(throwable);
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }catch (Throwable throwable)
                    {
                        Bugsnag.notify(throwable);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());

                    SnackBar.showError(getActivity(), snackBarView, getResources().getString(R.string.something_went_wrong));
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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }

    }

    Intent in;

    private void setProfileScreen(int profileStatus) {

        if (profileStatus == 0) {
            in = new Intent(context, SignUpEmailActivity.class);
        } else if (profileStatus == 1) {
            in = new Intent(context, DriverPersonalInfoActivity.class);
            startActivityForResult(in, REQUEST_ADD_PERSONAL_INFO);
        } else if (profileStatus == 2) {
            in = new Intent(context, DriverShippingInfoActivity.class);
            startActivityForResult(in, REQUEST_ADD_SHIPPING_INFO);
        } else if (profileStatus == 3) {
            in = new Intent(context, DriverAttachFileInfoActivity.class);
            startActivityForResult(in, REQUEST_ADD_FILES_INFO);
        } else if (profileStatus == 4) {
            in = new Intent(context, DriverBankInfoActivity.class);
            startActivityForResult(in, REQUEST_ADD_BANK_INFO);
        } else if (profileStatus == 5) {
            in = new Intent(context, DriverTypeInfoActivity.class);
            startActivityForResult(in, REQUEST_ADD_DRIVER_INFO);
        } else if (profileStatus == 6) {
            in = new Intent(context, DriverProfileInfoActivity.class);
            startActivityForResult(in, REQUEST_ADD_PROFILE_INFO);
        } else if (profileStatus == 7) {
            in = new Intent(context, HomeActivity.class);
        }
/*        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);*/
    }


    private void setListner() {

        rlVwUpdateMain.setOnClickListener(this);
        rlContentLayout.setOnClickListener(this);
      /*  tvPersonalInfo.setOnClickListener(this);
        tvShippingInfo.setOnClickListener(this);
        tvAttachFiles.setOnClickListener(this);
        tvDriverType.setOnClickListener(this);
        tvBankInfo.setOnClickListener(this);
        tvProfile.setOnClickListener(this);*/
        //sapan-->googlemap
        /*  mapFragment.getMapAsync(this);*/
        tvPercentage.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        linlay1.setOnClickListener(this);
        linlay2.setOnClickListener(this);
        linlay3.setOnClickListener(this);


    }

    private void bindViews() {

        rlVwUpdateMain = binding.rlVwUpdateMain;
        rlContentLayout = binding.contentLayout;
        tvPercentage = binding.tvPercentage;
        btnFinish = binding.btnfinish;
        imgVwRemainMark = binding.imgVwRemainMark;
        imgVwAlert = binding.imgVwAlert;

        linlay1 = binding.lenlay1;
        linlay2 = binding.lenlay2;
        linlay3 = binding.lenlay3;

        frameLayout = binding.containerTab;
    /*    tvPersonalInfo = binding.tvPersonalInfo;
        tvShippingInfo = binding.tvShippingInfo;
        tvAttachFiles = binding.tvAttachFiles;
        tvDriverType = binding.tvDriverType;
        tvBankInfo = binding.tvBankInfo;
        tvProfile = binding.tvProfile;*/
        //sapan-->googlemap
        /*     mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mgooleMap);*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    Dialog dialog;

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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        // DriverEarningFragment fragment = null;
        Intent intent;
        switch (view.getId()) {
            case R.id.btnBackHome:
                dialog.dismiss();
                getActivity().finish();
                Intent i = new Intent(context, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.lenlay1:
                try {
                    ///   callUserMeApi();
                    //if (userData.getData().getProfile_pic() != null && userData.getData().getProfile_status() == 7) {
                    if (userDetail.getData().getIs_approve().equals("1")) {
                        //showCheckUserUpdateData("Please complete your updates before proceeding.");
                        linlay1.setAlpha(1);
                        linlay2.setAlpha((float) 0.3);
                        linlay3.setAlpha((float) 0.3);
                        frameLayout.setVisibility(View.GONE);
                    } else {

//                startActivity(new Intent(getActivity(), AcceptUserRequestActivity.class).putExtra("message","191"));
                /*Fragment fragmentPro = null;
                fragmentPro = new MapHomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.containerBody, fragmentPro);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                    }
                    //  openDrawer();

                } catch (Exception e) {
                    e.printStackTrace();
                }catch (Throwable throwable)
                {
                    Bugsnag.notify(throwable);
                }
                break;
            case R.id.lenlay2:
                try {
                    ///   callUserMeApi();
                    //if (userData.getData().getProfile_pic() != null && userData.getData().getProfile_status() == 7) {
                    if (userDetail.getData().getIs_approve().equals("1")) {
                        linlay1.setAlpha((float) 0.3);
                        linlay2.setAlpha(1);
                        linlay3.setAlpha((float) 0.3);
                        frameLayout.setVisibility(View.VISIBLE);
                        DriverEarningFragment fragment3 = new DriverEarningFragment();
                        FragmentTransaction transaction3 = getFragmentManager().beginTransaction();
                        transaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction3.replace(R.id.containerTab, fragment3);
                        transaction3.addToBackStack(null);
                        transaction3.commit();
                        //showCheckUserUpdateData("Please complete your updates before proceeding.");
                    } else {

                    }
                    //  openDrawer();

                } catch (Exception e) {
                    e.printStackTrace();
                }catch (Throwable throwable)
                {
                    Bugsnag.notify(throwable);
                }

                break;
            case R.id.lenlay3:
                try {
                    ///   callUserMeApi();
                    //if (userData.getData().getProfile_pic() != null && userData.getData().getProfile_status() == 7) {
                    if (userDetail.getData().getIs_approve().equals("1")) {
                        //showCheckUserUpdateData("Please complete your updates before proceeding.");\
                        linlay1.setAlpha((float) 0.3);
                        linlay2.setAlpha((float) 0.3);
                        linlay3.setAlpha(1);
                        frameLayout.setVisibility(View.VISIBLE);
                        DriverManageFragment fragment2 = new DriverManageFragment(getActivity(), userDetail);
                        FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                        transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction2.replace(R.id.containerTab, fragment2);
                        transaction2.addToBackStack(null);
                        transaction2.commit();
                    } else {

                    }
//  openDrawer();

                } catch (Exception e) {
                    e.printStackTrace();
                }catch (Throwable throwable)
                {
                    Bugsnag.notify(throwable);
                }
                break;
            case R.id.tvPercentage:

                break;
            case R.id.btnfinish:
                setProfileScreen(profileStatus);
                break;
            case R.id.rlVwUpdateMain:
                if (open == 0) {
                    Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                            R.anim.bottom_up);
                    rlVwUpdateMain.startAnimation(bottomUp);
                    rlContentLayout.startAnimation(bottomUp);
                    rlContentLayout.setVisibility(View.VISIBLE);
                    open = 1;
                } else {
                    Animation bottomDown = AnimationUtils.loadAnimation(getContext(),
                            R.anim.bottom_down);
                    rlVwUpdateMain.startAnimation(bottomDown);
                    rlContentLayout.startAnimation(bottomDown);
                    rlContentLayout.setVisibility(View.GONE);
                    open = 0;
                }
               /* RelativeLayout hiddenPanel = (RelativeLayout) findViewById(R.id.hidden_panel);
                hiddenPanel.startAnimation(bottomUp);
                hiddenPanel.setVisibility(View.VISIBLE);*/
                break;
           /* case R.id.tvPersonalInfo:
                setProfileScreen(profileStatus);
               *//* intent = new Intent(context, DriverPersonalInfoActivity.class);
                startActivityForResult(intent, REQUEST_ADD_PERSONAL_INFO);*//*
                break;
            case R.id.tvShippingInfo:
                setProfileScreen(profileStatus);
               *//* intent = new Intent(context, DriverShippingInfoActivity.class);
                startActivityForResult(intent, REQUEST_ADD_SHIPPING_INFO);*//*
                break;
            case R.id.tvAttachFiles:
                setProfileScreen(profileStatus);
                *//*intent = new Intent(context, DriverAttachFileInfoActivity.class);
                startActivityForResult(intent, REQUEST_ADD_FILES_INFO);*//*
                break;
            case R.id.tvBankInfo:
                setProfileScreen(profileStatus);
              *//*  intent = new Intent(context, DriverBankInfoActivity.class);
                startActivityForResult(intent, REQUEST_ADD_BANK_INFO);*//*
                break;
            case R.id.tvDriverType:
                setProfileScreen(profileStatus);
                *//*intent = new Intent(context, DriverTypeInfoActivity.class);
                startActivityForResult(intent, REQUEST_ADD_DRIVER_INFO);*//*
                break;
            case R.id.tvProfile:
                setProfileScreen(profileStatus);
               *//* intent = new Intent(context, DriverProfileInfoActivity.class);
                startActivityForResult(intent, REQUEST_ADD_PROFILE_INFO);*//*
                break;*/
        }
        /*if (fragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.containerBody, fragment);
            transaction.addToBackStack(null);
            Toast.makeText(context, "hiiii", Toast.LENGTH_SHORT).show();
            transaction.commit();


        }*/
    }

    private int calculatePercentage(int profileStatus) {
        per = (profileStatus * 100) / 7;
        return per;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PERSONAL_INFO) {
            if (data != null) {
                String profileStatus = data.getStringExtra("profileStatus");
                setProfileScreen(Integer.parseInt(profileStatus));
            }
            /*callDriverMeApi();*/
        } else if (requestCode == REQUEST_ADD_SHIPPING_INFO) {
            if (data != null) {
                tvShippingInfo.setTextColor(getResources().getColor(R.color.app_bg));
                tvShippingInfo.setClickable(false);
                String profileStatus = data.getStringExtra("profileStatus");
                setProfileScreen(Integer.parseInt(profileStatus));
            }
            /* callDriverMeApi();*/
        } else if (requestCode == REQUEST_ADD_FILES_INFO) {
            if (data != null) {
                tvAttachFiles.setTextColor(getResources().getColor(R.color.app_bg));
                tvAttachFiles.setClickable(false);
                String profileStatus = data.getStringExtra("profileStatus");
                setProfileScreen(Integer.parseInt(profileStatus));
            }
            /* callDriverMeApi();*/
        } else if (requestCode == REQUEST_ADD_DRIVER_INFO) {
            if (data != null) {
                tvDriverType.setTextColor(getResources().getColor(R.color.app_bg));
                tvDriverType.setClickable(false);
                String profileStatus = data.getStringExtra("profileStatus");
                setProfileScreen(Integer.parseInt(profileStatus));
            }
            /*callDriverMeApi();*/
        } else if (requestCode == REQUEST_ADD_BANK_INFO) {
            if (data != null) {
                tvBankInfo.setTextColor(getResources().getColor(R.color.app_bg));
                tvBankInfo.setClickable(false);
                String profileStatus = data.getStringExtra("profileStatus");
                setProfileScreen(Integer.parseInt(profileStatus));
            }
            /*    callDriverMeApi();*/
        } else if (requestCode == REQUEST_ADD_PROFILE_INFO) {
            if (data != null) {
                tvProfile.setTextColor(getResources().getColor(R.color.app_bg));
                tvProfile.setClickable(false);
                String profileStatus = data.getStringExtra("profileStatus");
                setProfileScreen(Integer.parseInt(profileStatus));
            }
            /*   callDriverMeApi();*/
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        callDriverMeApi();
    }

}
