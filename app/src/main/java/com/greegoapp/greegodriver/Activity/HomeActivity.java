package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Adapter.DrawerLayoutAdapter;
import com.greegoapp.greegodriver.AppController.AppController;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

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
    private String[] drawerTitle = {"Home", "Dashboard","Help", "Settings"};
    private int[] drawerImage={R.mipmap.ic_map,R.mipmap.dashboard,R.mipmap.ic_help,R.mipmap.ic_settings};
    public static int index = 0;

    //Priyanka 21-4
    GetDriverData.DataBean driverDetails;
    ArrayList<GetDriverData> alDriverList;
    String driverName;
    private Stack<Fragment> fragmentStack;
    FragmentManager fragmentManager;
    public static final int MY_PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerLayoutAdapter = new DrawerLayoutAdapter(HomeActivity.this, drawerTitle,drawerImage);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        context = HomeActivity.this;
        fragmentStack = new Stack<Fragment>();
        fragmentManager = this.getSupportFragmentManager();
        snackBarView = findViewById(android.R.id.content);

        manager = getFragmentManager();
        bindView();
        setListners();

        //priyanka 20-4
        if (ConnectivityDetector.isConnectingToInternet(context)) {
            callUserMeApi();
//            CheckGpsStatus();
        } else {
            Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
        }

        setHomeValues();
        slideMenu();
        drawerlist.requestDisallowInterceptTouchEvent(true);
        navHeader.requestDisallowInterceptTouchEvent(true);

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


                    driverDetails = new Gson().fromJson(String.valueOf(response), GetDriverData.DataBean.class);
                    GetDriverData driverDetail = new Gson().fromJson(String.valueOf(response), GetDriverData.class);
                    try {
                        MyProgressDialog.hideProgressDialog();
                        alDriverList = new ArrayList<>();

                        if (driverDetail.getError_code() == 0) {


                            alDriverList.add(driverDetail);

                            Applog.E("UserUpdate==>Dg==>" + driverDetail);

                            String userName = driverDetail.getData().getName();
                            tvDrawUsername.setText(userName);
                            Glide.with(context).load("http://www.innoviussoftware.com/greego/storage/app/documents/w47HhwzUJARP1nshJblCM2jgRTO0hSZwEvkfAhFY.jpeg")
                                    .into(ivProPicHome);
                            Glide.with(context).load("http://www.innoviussoftware.com/greego/storage/app/documents/w47HhwzUJARP1nshJblCM2jgRTO0hSZwEvkfAhFY.jpeg")
                                    .into(ivPro);


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
                        } else {
                            MyProgressDialog.hideProgressDialog();
                            SnackBar.showError(context, snackBarView, response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        }

    }

    private boolean checkSelfPermission() {
        int AccessCorasLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int AccessFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> permissionNeeded = new ArrayList<>();

        if (AccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (AccessCorasLocation != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }


        if (!permissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionNeeded.toArray(new String[permissionNeeded.size()]), MY_PERMISSION_REQUEST);
            return false;
        }
        return true;
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
        }
    }

    private void setListners() {
        ivProPicHome.setOnClickListener(this);
    }

    private void bindView() {
        navHeader = binding.navHeader;
        drawer_layout = binding.drawerLayout;
        ivProPicHome = binding.ivProPicHome;
        container_body = binding.containerBody;
        drawerlist = binding.drawerlist;
        ivPro = binding.ivPro;
        tvDrawUsername = binding.tvDrawUsername;
        menuList = binding.menuList;
   /*     tvDriveGreego = binding.tvDriveGreego;*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivProPicHome:
                if (alDriverList.size() != 0) {

                    for (GetDriverData userData : alDriverList) {
                        if (userData.getData().getProfile_pic() != null && userData.getData().getProfile_status()==7) {
                            openDrawer();
                        } else {
                            showCheckUserUpdateData("Please complete your updates before proceeding.");
                        }
                    }
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
                        SnackBar.showInternetError(context, snackBarView);
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
        }
    }

    private void closeDrawer() {
        try {
            if (drawer_layout.isDrawerOpen(drawerlist))
                drawer_layout.closeDrawer(drawerlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
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
                           SnackBar.showValidationError(context, snackBarView, getString(R.string.in_progress));
                            break;

                        case 1:
                            SnackBar.showValidationError(context, snackBarView, getString(R.string.in_progress));
                            break;

                        case 2:
                            SnackBar.showValidationError(context, snackBarView, getString(R.string.in_progress));
                            break;

                        case 3:
                            Intent intentSetting=new Intent(context,SettingActivity.class);
                            startActivity(intentSetting);
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
        }
    }
}
