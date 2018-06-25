package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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
import com.greegoapp.greegodriver.databinding.ActivityLegalBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LegalActivity extends AppCompatActivity implements View.OnClickListener {


    ActivityLegalBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibBack;
    TextView tvtermCond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_legal);


        snackBarView = findViewById(android.R.id.content);
        context = LegalActivity.this;
        Bugsnag.init(context);
        bindViews();
        setListner();
        callTermsCondiAPI();
    }

    private void callTermsCondiAPI() {
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
                            tvtermCond.setText(Html.fromHtml(userDetail.getData().getTerms_conditions()));

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
    }

    private void bindViews() {
        ibBack = binding.ibBack;
        tvtermCond = binding.tvtermCond;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                finish();
                break;
        }
    }
}
