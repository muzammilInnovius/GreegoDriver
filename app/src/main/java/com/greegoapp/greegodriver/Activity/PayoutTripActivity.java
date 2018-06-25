package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Adapter.PayoutHistoryAdapter;
import com.greegoapp.greegodriver.Adapter.TripHistoryAdapter;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Interface.RecyclerViewItemClickListener;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.PayoutData;
import com.greegoapp.greegodriver.Model.TripHistoryDetailsModel;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityPayoutTripBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PayoutTripActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPayoutTripBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibBack;
    RecyclerView rVwPayout;
    private PayoutHistoryAdapter historyAdapter;
    PayoutData tripHistory;
    private ArrayList<PayoutData.DataBean> payoutList;
    RecyclerViewItemClickListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payout_trip);
        context = PayoutTripActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        Bugsnag.init(context);
        setListners();
        getPayoutHistory();

        mListener = new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                int tripHistoryPosition = position;
                Intent in = new Intent(context, PayoutDetailActivity.class);
                in.putExtra("type",String.valueOf(payoutList.get(tripHistoryPosition).getPaid_type()));
                in.putExtra("cash_out_time", payoutList.get(tripHistoryPosition).getCreated_at());
                in.putExtra("drive_amount",String.valueOf(payoutList.get(tripHistoryPosition).getActual_trip_amount()));
                in.putExtra("greego_fee",String.valueOf(payoutList.get(tripHistoryPosition).getGreego_fee()));
                in.putExtra("bonus",String.valueOf(payoutList.get(tripHistoryPosition).getTip_amount()));
                startActivity(in);

            }
        };
    }

    private void getPayoutHistory() {
        GetDriverData getDriverData = new Gson().fromJson(getSharedPreferences("driverData", 0).getString("driver", ""), GetDriverData.class);
        Integer id = getDriverData.getData().getId();

        payoutList = new ArrayList<PayoutData.DataBean>();
        JSONObject jsonObject = new JSONObject();
        MyProgressDialog.showProgressDialog(context);
        try {
            jsonObject.put(WebFields.PAYOUT_HISTORY.driver_id,id);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }

        Applog.E("request payout history => " + jsonObject.toString());
        /*       MyProgressDialog.showProgressDialog(context);*/
        /*"http://kroslinkstech.in/greego/public/api/driver/express/pay"*/
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, WebFields.BASE_URL + WebFields.PAYOUT_HISTORY.MODE
                , jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    MyProgressDialog.hideProgressDialog();
                    PayoutData payoutData = new Gson().fromJson(String.valueOf(response), PayoutData.class);
                    try {
                        MyProgressDialog.hideProgressDialog();
                        payoutList = new ArrayList<>();

                        if (payoutData.getError_code() == 0) {
                            payoutList.addAll(payoutData.getData());
                           // Collections.reverse(payoutList);
                            for(int ij=0;ij<payoutList.size();ij++){
                                int i=0;
                                i++;
                            }
                            Log.e("payoutHistory", String.valueOf(payoutList));
                            historyAdapter = new PayoutHistoryAdapter(context, payoutList, mListener);
                            rVwPayout.setLayoutManager(new LinearLayoutManager(context));
                            /*  Collections.reverse(tripList);*/
                            rVwPayout.setAdapter(historyAdapter);
                      //     rVwPayout.notify();
                            historyAdapter.notifyDataSetChanged();
                        } else {
                           
                            SnackBar.showError(context, snackBarView, response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }catch (Throwable throwable)
                    {
                        Bugsnag.notify(throwable);
                    }
                    Applog.E("response"+response.toString());
                } else {
                    MyProgressDialog.hideProgressDialog();
                    SnackBar.showError(context,snackBarView,"An error occurred. Please try again latter");
//                    Toast.makeText(context, "An error occurred. Please try again latter", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SnackBar.showError(context,snackBarView,"An error occurred. Please try again latter");
                //                Toast.makeText(context, "An error occurred. Please try again latter", Toast.LENGTH_LONG).show();
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
    }

    private void setListners() {
        ibBack.setOnClickListener(this);
    }

    private void bindView() {
        ibBack = binding.ibBack;
        rVwPayout = binding.rlPayout;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.ibBack:
                finish();
                break;
        }
    }
}
