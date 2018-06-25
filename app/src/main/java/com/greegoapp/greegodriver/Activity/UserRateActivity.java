package com.greegoapp.greegodriver.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.RoundedImageView;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityUserRateBinding;
import com.stripe.model.Charge;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserRateActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityUserRateBinding binding;
    public static boolean active = false;

    Context context;
    private View snackBarView;
    Button btnDone;
    String rate;
    TextView tvPrice, tvTipPrice, tvTotalPrice;
    RelativeLayout rlAddTip;
    Dialog dialogRequest;
    private String card_token;
    private String trip_disc;
    private String trip_id;
    private String profilePic, username;
    private RoundedImageView imgVwUserProfile;
    public String strDriverTip;
    String tripStartTime;
    RatingBar ratingBar;
    PackageManager packageManager;
    Float totalCost, totalPrice, tipsPrice;
    private int REQUEST_UBER = 0010;
    TextView tvReview;
    ArrayList<String> appName = new ArrayList<>();
    private String actual_trip_amount, est_trip_cost;
    private String base_fee;
    private String actual_cost;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_rate);
        context = UserRateActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        rate = getIntent().getStringExtra("rate");
        trip_id = getIntent().getStringExtra("trip_id");
        profilePic = getIntent().getStringExtra("profilePic");
        username = getIntent().getStringExtra("uname");
        trip_disc = getIntent().getStringExtra("trip_disc");
        card_token = getIntent().getStringExtra("card_token");
        actual_trip_amount = getIntent().getStringExtra("actual_trip_amount");
        est_trip_cost = getIntent().getStringExtra("est_trip_cost");
        base_fee = getIntent().getStringExtra("base_fee");
        bindView();
        setListners();

//            tvPrice.setText("" + rate.substring(0, 5));
        try {
            /*if (rate.equals(base_fee)) {
                tvPrice.setText(String.format("%.2f", Double.valueOf(actual_trip_amount)));
            } else {*/
                tvPrice.setText(String.format("%.2f", Double.valueOf(rate)));
          //  }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*tvTotalPrice.setText("" + rate.substring(0, 5));*/
        // tvPrice.setText("" + String.format("%.2f", Double.valueOf(actual_cost)));

        /*  tvTotalPrice.setText("10.00");*/


        /*  tvTipPrice.setText("00.00");*/
        tvReview.setText(getIntent().getStringExtra("uname"));
        if (profilePic != null) {
            Glide.clear(imgVwUserProfile);
            // Glide.clear(imgVwUserDropProfile);
            Glide.with(context)
                    .load(profilePic)
                    .centerCrop()
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .crossFade().skipMemoryCache(true)
                    .into(imgVwUserProfile);
//            Glide.with(context)
//                    .load(url)
//                    .centerCrop()
//                    .signature(new StringSignature(UUID.randomUUID().toString()))
//                    .crossFade().skipMemoryCache(true)
//                    .into(imgVwUserDropProfile);

        } else {
//            imgVwUserDropProfile.setImageResource(R.mipmap.ic_user_profile);
            imgVwUserProfile.setImageResource(R.mipmap.ic_user_profile);
        }

    }


    private void setListners() {
//        rlAddTip.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    private void bindView() {
        btnDone = binding.btnDone;
        imgVwUserProfile = binding.imgVwUserProfile;
        tvPrice = binding.tvPrice;
        /*tvTipPrice = binding.tvTipPrice;
        rlAddTip = binding.rlAddTip;
        tvTotalPrice = binding.tvTotalPrice;*/
        ratingBar = binding.ratingBar;
        tvReview = binding.tvReview;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDone:
                //stripePay();
                transactionApicall("1");
                break;
            case R.id.btnBack:
                finish();
                Intent i = new Intent(context, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.rlRequest:
                uberLyftScreen();
              /*  Intent intentRlRequest = new Intent(this, RequestLyftUberActivity.class);
                startActivity(intentRlRequest);*/
                break;
            case R.id.imgVwLyftUber:
                uberLyftScreen();
                break;

            /*case R.id.rlAddTip:
                callAddTrip();
                break;*/
        }
    }

    private void uberLyftScreen() {
        Intent navigationIntent1 = new Intent(context, RequestLyftUberActivity.class);

        for (ApplicationInfo applicationInfo : findUber()) {
            appName.add(applicationInfo.loadLabel(packageManager).toString());
        }
        for (ApplicationInfo applicationInfo : findLyft()) {
            appName.add(applicationInfo.loadLabel(packageManager).toString());
        }
        navigationIntent1.putStringArrayListExtra("appList", appName);
        startActivityForResult(navigationIntent1, REQUEST_UBER);
        finish();

    }

    List<ApplicationInfo> findUber() {
        Boolean flag = false;
        packageManager = getPackageManager();
        List<ApplicationInfo> appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (ApplicationInfo applist : appList) {
            if (applist.loadLabel(packageManager).toString().contains("Uber"))
                flag = true;
        }
        return appList;
    }

    List<ApplicationInfo> findLyft() {
        Boolean flag = false;
        packageManager = getPackageManager();
        List<ApplicationInfo> appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (ApplicationInfo applist : appList) {
            if (applist.loadLabel(packageManager).toString().contains("Lyft"))
                flag = true;
        }
        return appList;
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {

                if (!info.packageName.equals("com.google.android.googlequicksearchbox")) {
                    if (!info.packageName.equals("com.metrobit.parentalcontrol")) {
                        if (!info.packageName.contains("launcher3")) {
                            if (!info.packageName.contains("launcher")) {//com.google.android.googlequicksearchbox
                                if (!info.packageName.contains("trebuchet")) {
                                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                                        applist.add(info);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                Bugsnag.notify(throwable);
            }
        }

        return applist;
    }

    private void callAddTrip() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_driver_trip);
        final EditText edtaddDriverTrip = dialog.findViewById(R.id.edtaddDriverTrip);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tipsDriveName = dialog.findViewById(R.id.tipsDriveName);

        tipsDriveName.setText(username);

        /*btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    KeyboardUtility.hideKeyboard(context,v);
                    strDriverTip = edtaddDriverTrip.getText().toString();
                    if (strDriverTip.contains("."))
                        tvTipPrice.setText(strDriverTip);
                    else
                        tvTipPrice.setText(strDriverTip + ".00");
                    totalPrice = Float.parseFloat(tvPrice.getText().toString());
                    tipsPrice = Float.parseFloat(tvTipPrice.getText().toString());
                    totalCost = totalPrice + tipsPrice;

                    tvTotalPrice.setText(totalCost.toString().substring(0, 4) + "0");
                    dialog.dismiss();
                }catch (Exception e)
                {

                }
            }
        });*/


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    void cloaseActivity() {
        dialogRequest = new Dialog(this);
        dialogRequest.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRequest.setContentView(R.layout.dialog_request_lyft_uber);
        Button btnBack = dialogRequest.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        RelativeLayout rlRequest = dialogRequest.findViewById(R.id.rlRequest);
        rlRequest.setOnClickListener(this);
        ImageView imgRequest = dialogRequest.findViewById(R.id.imgVwLyftUber);
        imgRequest.setOnClickListener(this);
        dialogRequest.show();
    }

    void stripePay() {

      /*  Float num1 = Float.parseFloat(tvPrice.getText().toString());
        Float num2 = Float.parseFloat(tvTipPrice.getText().toString());
        Float sum = num1 + num2;
*/
        Applog.E("Total sum = > " + tvPrice.getText().toString());

        MyProgressDialog.showProgressDialog(context);
        final Map<String, Object> parames = new HashMap<>();
//        double price=0;
        String price1 = tvTotalPrice.getText().toString().trim();
        int price = Integer.parseInt(price1.replace(".", ""));
        parames.put("amount", price);
        parames.put("currency", "usd");
        parames.put("description", trip_disc);
        parames.put("customer", card_token);


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
                        SnackBar.showSuccess(context, snackBarView, "thanks for transaction" + charge.getId());
//                        Toast.makeText(getApplicationContext(), "thanks for transaction" + charge.getId(), Toast.LENGTH_SHORT).show();
                        transactionApicall(charge.getId());
                    } else {
                        SnackBar.showError(context, snackBarView, "Payment fail");
//                        Toast.makeText(context, "payment fail", Toast.LENGTH_SHORT).show();
                    }
                }

            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }


    }

    private void transactionApicall(String id) {
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);
            jsonObject.put(WebFields.TRIP_RATING.trip_id, trip_id);
           /* jsonObject.put(WebFields.TRIP_TANSACTION.transaction_id, id);
            jsonObject.put(WebFields.TRIP_TANSACTION.transaction_description, trip_disc);
//            jsonObject.put(WebFields.TRIP_TANSACTION.transaction_amount, tvTotalPrice.getText().toString().trim());
            jsonObject.put(WebFields.TRIP_TANSACTION.transaction_amount, tvPrice.getText().toString().trim());
            jsonObject.put(WebFields.TRIP_TANSACTION.payment_status, "1");
            jsonObject.put(WebFields.TRIP_TANSACTION.user_card_id, card_token);
//            jsonObject.put(WebFields.TRIP_TANSACTION.tip_amount, tvTipPrice.getText().toString().trim());
            jsonObject.put(WebFields.TRIP_TANSACTION.tip_amount, "0");*/
            jsonObject.put(WebFields.TRIP_RATING.trip_user_rating, ratingBar.getRating());


            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.TRIP_RATING.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    cloaseActivity();
                    MyProgressDialog.hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage() + ":" + error.getStackTrace());
                    //     Toast.makeText(context, "error" + error.getMessage() + error.getStackTrace(), Toast.LENGTH_SHORT).show();
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
}
//sk_live_rncLYeKMLrWETWa3hAXBYjMb
//