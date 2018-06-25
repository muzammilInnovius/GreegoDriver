package com.greegoapp.greegodriver.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.GetTripRate;
import com.greegoapp.greegodriver.Model.Login;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityTripPriceBinding;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TripPriceActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityTripPriceBinding binding;
    EditText distance,elapsedTime;
    Button find;
    TextView price;
    Context context;
    private View snackBarView;
    float base_fee,time_fee,mile_fee,trip_price,over_mile_fee;
    public static final int TIME_OUT = 15000;
    public static final String strState = "state";
    public static final String param = "NY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_trip_price);
        context= TripPriceActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();

    }

    private void setListners() {
        find.setOnClickListener(this);
    }

    private void bindView() {
        distance=binding.editTextDistance;
        elapsedTime=binding.editTextTime;
        find=binding.button;
        price=binding.textView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                callapi();
                String strDistance = distance.getText().toString();
                String strTime=elapsedTime.getText().toString();
                float pricedata = CalculateMile(strDistance, strTime);
                price.setText("$ "+pricedata);
                break;
        }
    }

    private void callapi() {

        try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put(WebFields.TRIP.PARAM_STATE, param);

                Applog.E("request Trip Price => " + jsonObject.toString());
                MyProgressDialog.showProgressDialog(context);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        WebFields.BASE_URL + WebFields.TRIP.MODE, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Applog.E("success: " + response.toString());
                        GetTripRate tripRateData = new Gson().fromJson(String.valueOf(response), GetTripRate.class);
                        try {
                            MyProgressDialog.hideProgressDialog();
                            if (tripRateData.getError_code() == 0) {
                                base_fee = Float.parseFloat(""+tripRateData.getData().getBase_fee());
                                time_fee = Float.parseFloat(""+tripRateData.getData().getTime_fee());
                                mile_fee = Float.parseFloat(""+tripRateData.getData().getMile_fee());
                                over_mile_fee =Float.parseFloat(""+tripRateData.getData().getOvermile_fee());
                             //   CalculateMile(str,elapsedTime);

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
                    String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImE4MTc0OTQxMjA5YWQxNGRiYzhkMzBjNTI1ZWY3ZWQ4ZWI0YzhiYjEzNzY4YTYyNDU5YTM0NDg5NTFhYjNmYTE3MjUyOTI4NzJkNGY1NjRkIn0.eyJhdWQiOiIxIiwianRpIjoiYTgxNzQ5NDEyMDlhZDE0ZGJjOGQzMGM1MjVlZjdlZDhlYjRjOGJiMTM3NjhhNjI0NTlhMzQ0ODk1MWFiM2ZhMTcyNTI5Mjg3MmQ0ZjU2NGQiLCJpYXQiOjE1MjM3MDM5MzgsIm5iZiI6MTUyMzcwMzkzOCwiZXhwIjoxNTU1MjM5OTM4LCJzdWIiOiI0Iiwic2NvcGVzIjpbXX0.CZNL841VWZnk_Pd7CMCtcfqP0NtefI8Y6-M4qVCkmr5ryYmC9EwR7R5446zpwr7f6h-XrII92F_6Q3frd_86RG_9j0Ye_oTOuk2QscPULU2CACsyChC6quzZWiEQDj7MKwsVogNeTqhNFgo_-TtpTxMBRlaIjqSjrydiWczrc9hCh4iD5kZEPBU_5GxcFlBI4SgVHiJTZcc1CNyb_iLc4zq0OHQHWlczJELGH9V8wVIEm8qrc1wQYqRsHo3Sb1uSlbyoqEPKLsPspqHAG-xwTKS5b0__6-KfteeDnfapCjKl6Ll-_U-jpdzhrsvPv67nMcYMW0arQUVl1AWuhO5B6tUEvQI12mBB8Pyfd9ayHJsmc27oBaB_cEQlAR31Jz2nrfgIisST8mZ8ylNtJnX1Xk0f5LBDst8E5UFEX5OtWdzOeQHIt78WEzVSyV6pQHhESK_XiizoBaHndOZ3Yac6W8EzB3SWWTJ10bQiyBt88UOpgHkc_dNrc0GCtOkN1n6SUGd24RsS2zPXk3vSEL2rncT5JXzo82RZVCtdGqFdl8mXuta6hAtyeknRo94xPjAkwI2qbWiztw28eN_bQIPoSCBbNkoyVbojDIAI-0Ww0mLhh5_zVQCYMjXtJIrYPXDDSBL7crxgxUahoblGCsLmumybBizPgZeuA9E6tHiYnHg";
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(WebFields.PARAM_ACCEPT, "application/json");
                        params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + token);
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

    private float CalculateMile(String strDistance, String strTime) {
        float dis = Float.parseFloat(strDistance);
        float time= Float.parseFloat(strTime);
        if (dis <= 10) {
            trip_price = base_fee + (time * time_fee) + ( dis * mile_fee);
        } else
        {
            trip_price=base_fee+(time*time_fee)+(10*mile_fee)+( ( dis - 10 ) * over_mile_fee);
        }
        return trip_price;
    }
}
