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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Adapter.TripHistoryAdapter;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Interface.RecyclerViewItemClickListener;
import com.greegoapp.greegodriver.Model.TripHistoryDetailsModel;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityHelpBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHelpBinding binding;
    Context context;
    private View snackBarView;
    private RecyclerView rvDriverTripHistoryList;
    private TripHistoryAdapter historyAdapter;
    TripHistoryDetailsModel tripHistory;
    private ArrayList<TripHistoryDetailsModel.DataBean> tripList;
    RecyclerViewItemClickListener mListener;
    public int listSize;
    int count = 0;
    int totalList;
    int tripPosition = 1;
    Button btnViewMore,btnHelpCenter,btnLegal;
    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        context = HelpActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        bindView();
        setListners();
        getTripHistory();
        mListener = new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                int tripHistoryPosition = position;
                Intent in = new Intent(context, TripHistoryDetailActivity.class);
                /*in.putExtra("trip_id", tripList.get(tripHistoryPosition).getTransaction_id());
                in.putExtra("pick_up_time", tripList.get(tripHistoryPosition).getCreated_at());
                in.putExtra("drop_off_time", tripList.get(tripHistoryPosition).getUpdated_at());
                in.putExtra("drive_earning", String.valueOf(tripList.get(tripHistoryPosition).getTotal_estimated_trip_cost()));
                in.putExtra("to_lat", String.valueOf(tripList.get(tripHistoryPosition).getTo_lat()));
                in.putExtra("to_lng", String.valueOf(tripList.get(tripHistoryPosition).getTo_lng()));
                in.putExtra("from_lat", String.valueOf(tripList.get(tripHistoryPosition).getFrom_lat()));
                in.putExtra("from_lng", String.valueOf(tripList.get(tripHistoryPosition).getFrom_lng()));
          //      in.putExtra("trip_time", String.valueOf(tripList.get(tripHistoryPosition).getTotal_estimated_travel_time()));
                in.putExtra("distance",String.valueOf(tripList.get(tripHistoryPosition).getActual_trip_miles()));
                in.putExtra("start_time",tripList.get(tripHistoryPosition).getStart_time());
                in.putExtra("end_time",tripList.get(tripHistoryPosition).getEnd_time());
                //in.putExtra("trip_id", String.valueOf(tripList.get(tripHistoryPosition).getId()));*/

                in.putExtra("trip_id", String.valueOf(tripList.get(tripHistoryPosition).getId()));
                in.putExtra("transaction_id", tripList.get(tripHistoryPosition).getTransaction_id());
                in.putExtra("pick_up_time", tripList.get(tripHistoryPosition).getStart_time());
                in.putExtra("drop_off_time", tripList.get(tripHistoryPosition).getEnd_time());
                in.putExtra("drive_earning", String.valueOf(tripList.get(tripHistoryPosition).getTotal_estimated_trip_cost()));
                in.putExtra("to_lat", String.valueOf(tripList.get(tripHistoryPosition).getTo_lat()));
                in.putExtra("to_lng", String.valueOf(tripList.get(tripHistoryPosition).getTo_lng()));
                in.putExtra("from_lat", String.valueOf(tripList.get(tripHistoryPosition).getFrom_lat()));
                in.putExtra("from_lng", String.valueOf(tripList.get(tripHistoryPosition).getFrom_lng()));
          //      in.putExtra("trip_time", String.valueOf(tripList.get(tripHistoryPosition).getTotal_estimated_travel_time()));
                in.putExtra("trip_time", tripList.get(tripHistoryPosition).getTotal_estimated_travel_time());
                in.putExtra("distance",String.valueOf(tripList.get(tripHistoryPosition).getActual_trip_miles()));
                in.putExtra("request_time",tripList.get(tripHistoryPosition).getCreated_at());
                in.putExtra("actual_trip_amount",String.valueOf(tripList.get(tripHistoryPosition).getActual_trip_amount()));
                in.putExtra("tip_amount",String.valueOf(tripList.get(tripHistoryPosition).getTip_amount()));
                //in.putExtra("trip_id", String.valueOf(tripList.get(tripHistoryPosition).getId()));
                startActivity(in);

            }
        };

    }
    private void getTripHistory() {
        boolean ascending = true;
        ArrayList<String> spacecrafts = new ArrayList<>();
        tripList = new ArrayList<TripHistoryDetailsModel.DataBean>();
        final int[] time = new int[1];
        final Double[] amount = new Double[1];
        final Double[] lat1 = new Double[1];
        final Double[] lon1 = new Double[1];
        final Double[] lat2 = new Double[1];
        final Double[] lon2 = new Double[1];
        Double distance = null;

        //  final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, WebFields.BASE_URL + WebFields.TRIP_HISTORY.MODE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {

                    tripHistory = new Gson().fromJson(String.valueOf(response), TripHistoryDetailsModel.class);

                    if (tripHistory != null) {

                        listSize = tripHistory.getData().size();
                        try {
                            for (int i = 0; i < 1; i++) {
                                tripList.add(tripHistory.getData().get(i));
                                count++;
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }catch (Throwable throwable)
                        {
                            Bugsnag.notify(throwable);
                        }
                    }
                    totalList = tripHistory.getData().size();
                    Log.d("List size", String.valueOf(tripList.size()));
                    Collections.reverse(tripList);
                    historyAdapter = new TripHistoryAdapter(context, tripList, mListener);
                    rvDriverTripHistoryList.setLayoutManager(new LinearLayoutManager(context));
                    /*  Collections.reverse(tripList);*/
                    rvDriverTripHistoryList.setAdapter(historyAdapter);
                } else {
                    SnackBar.showError(context,snackBarView,"An error occurred. Please try again latter");
             //       Toast.makeText(context, "An error occurred. Please try again latter", Toast.LENGTH_LONG).show();
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
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                GlobalValues.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    private void setListners() {
        rvDriverTripHistoryList.setOnClickListener(this);
        btnViewMore.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        btnHelpCenter.setOnClickListener(this);
        btnLegal.setOnClickListener(this);
    }

    private void bindView() {
        rvDriverTripHistoryList = binding.rvRideHistoryList;
        btnViewMore = binding.btnViewMoreRides;
        ibBack = binding.ibBack;
        btnHelpCenter = binding.btnHelpCenter;
        btnLegal = binding.btnLegal;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ibBack:
                finish();
                break;
            case R.id.btnViewMoreRides:
                Log.e("listSize", String.valueOf(listSize));
                Log.e("count ", String.valueOf(count));
                if (tripPosition < listSize) {

                    tripList.add(null);
                    historyAdapter.notifyItemInserted(tripList.size());
                    //   remove progress item
                    tripList.remove(tripList.size() - 1);
                    historyAdapter.notifyItemRemoved(tripList.size());
                    //add items one by one
                    //  int start = 1;

                 //   int end = tripPosition + 5;
                    try {
                        for (int i = tripPosition; i <= listSize; i++) {
                            tripList.add(tripHistory.getData().get(i));
                            tripPosition++;
                            count++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }catch (Throwable throwable)
                    {
                        Bugsnag.notify(throwable);
                    }
                    // Collections.reverse(tripList);
                    historyAdapter = new TripHistoryAdapter(context, tripList, mListener);
                    rvDriverTripHistoryList.setLayoutManager(new LinearLayoutManager(context));
                    rvDriverTripHistoryList.setAdapter(historyAdapter);
                } else {
              //      Toast.makeText(context, "No More Trip History Found.", Toast.LENGTH_LONG).show();
                    SnackBar.showError(context,snackBarView,"No More Trip History Found.");
                }
                break;
            case R.id.btnHelpCenter:
                Intent intentHelpCenter = new Intent(this,HelpCenterActivity.class);
                startActivity(intentHelpCenter);
                break;
            case R.id.btnLegal:
                Intent intentLegal = new Intent(this,LegalActivity.class);
                startActivity(intentLegal);
                break;
        }

    }
}
