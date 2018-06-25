package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.TermsCondition;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityHelpCenterBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HelpCenterActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHelpCenterBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibBack;
   // TextView tvHelpContition;
    TextView tvLostFound,tvReport,tvHowPayCal,tvAnimalPolicy;
    public String strLostFound,strReport,strHowPayCal,strAnimalPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help_center);

        snackBarView = findViewById(android.R.id.content);
        context = HelpCenterActivity.this;
        Bugsnag.init(context);
        bindViews();
        setListner();
        callHelpAPI();
    }

    private void callHelpAPI() {
        try {
            JSONObject jsonObject = new JSONObject();

            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    WebFields.BASE_URL + WebFields.TERMS_CONDITION.MODE, null, new Response.Listener<JSONObject>() {




                @Override
                public void onResponse(JSONObject response) {

                    TermsCondition userDetail = new Gson().fromJson(String.valueOf(response), TermsCondition.class);
                    try {
                        if (userDetail.getError_code() == 0) {
                            Applog.E("success: " + response.toString());
                            MyProgressDialog.hideProgressDialog();
                            strLostFound = userDetail.getData().getLost_found();
                            strReport = userDetail.getData().getSafety();
                            strHowPayCal = userDetail.getData().getDriver_pay();
                            strAnimalPolicy = userDetail.getData().getService_animal_policy();
                  //          tvHelpContition.setText(Html.fromHtml(userDetail.getData().getLost_found()));

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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    private void setListner() {
        ibBack.setOnClickListener(this);
        tvLostFound.setOnClickListener(this);
        tvReport.setOnClickListener(this);
        tvHowPayCal.setOnClickListener(this);
        tvAnimalPolicy.setOnClickListener(this);
    }

    private void bindViews() {
        ibBack = binding.ibBack;
    //    tvHelpContition = binding.tvHelpContition;
        tvLostFound = binding.tvLostFound;
        tvReport = binding.tvReportIncident;
        tvHowPayCal = binding.tvPayCal;
        tvAnimalPolicy = binding.tvPolicy;
    }

    @Override
    public void onClick(View view) {
        try {

            switch (view.getId()) {
                case R.id.ibBack:
                    finish();
                    break;
                case R.id.tv_lost_found:
                    Intent i = new Intent(context, HelpCenterDetailActivity.class);
                    i.putExtra("title", getResources().getString(R.string.lost_found));
                    i.putExtra("detail", strLostFound);
                    startActivity(i);
                    break;
                case R.id.tv_report_incident:
                    Intent i1 = new Intent(context, HelpCenterDetailActivity.class);
                    i1.putExtra("title", getResources().getString(R.string.report_safety));
                    i1.putExtra("detail", strReport);
                    startActivity(i1);
                    break;
                case R.id.tv_pay_cal:
                    Intent i2 = new Intent(context, HelpCenterDetailActivity.class);
                    i2.putExtra("title", getResources().getString(R.string.how_pay_cal));
                    i2.putExtra("detail", strHowPayCal);
                    startActivity(i2);
                    break;
                case R.id.tv_policy:
                    Intent i3 = new Intent(context, HelpCenterDetailActivity.class);
                    i3.putExtra("title", getResources().getString(R.string.tv_policy));
                    i3.putExtra("detail", strAnimalPolicy);
                    startActivity(i3);
                    break;
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }
}
