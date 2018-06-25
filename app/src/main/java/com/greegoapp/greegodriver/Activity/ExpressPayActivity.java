package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.ExpressPayData;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.GetTripRate;
import com.greegoapp.greegodriver.Model.Login;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityExpressPayBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.greegoapp.greegodriver.Fragment.DriverEarningFragment.REQUEST_INTENT_EXPRESS_PAY;

public class ExpressPayActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityExpressPayBinding binding;
    Context context;
    private View snackBarView;

    TextView tvClose;
    ImageView ivExpressPay;
    TextView tvDrivePayAmt, tvGreegoFeeAmt, tvExpressFeeAmt, tvTotalDepositAmt, tvAboutPayBonus, tvCashOutTime;
    RelativeLayout rlExpress;
    Button btnEditCard, btnPayConfirm;

    String strDriverAmount;
    public Double greego_fee, express_fee;
    GetDriverData.DataBean driverDetails;
    ArrayList<GetDriverData> alDriverList;
    private String strState;
    private String strShortName;
    Double total_deposit_amount;
    Double driveAmount;
    private String account_number;
    private double greego_fee_per;
    public Double greego_fee_amount;
    String s, cardNo;
    private boolean flag;


    //  public static String strGreegoFee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_express_pay);
        context = ExpressPayActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        bindView();
        setListners();
        Intent i = getIntent();
        strDriverAmount = i.getStringExtra("driverAmount");
        driveAmount = Double.valueOf(strDriverAmount);
//        callDriverMe();
        callApiForFee();
        //    callApiForFee();
        setValues();
     /*   strGreegoFee = String.valueOf(greego_fee);
        Log.d("strGreegoFee",strGreegoFee);*/
    }


    private void setValues() {
        tvDrivePayAmt.setText("$" + String.format("%.2f",driveAmount));
//        tvDrivePayAmt.setText("$" + new DecimalFormat("##.##").format(driveAmount));
    }

    private void setListners() {
        btnPayConfirm.setOnClickListener(this);
        btnEditCard.setOnClickListener(this);
        tvClose.setOnClickListener(this);
    }

    private void bindView() {
        tvClose = binding.tvClose;
        ivExpressPay = binding.ivExpressPay;
        tvDrivePayAmt = binding.tvDrivePaymentAmount;
        tvGreegoFeeAmt = binding.tvGreegoFeeAmount;
        tvExpressFeeAmt = binding.tvExpressFeeAmount;
        tvTotalDepositAmt = binding.tvTotalDepositeAmount;
        tvAboutPayBonus = binding.tvAboutPayBonuse;
        rlExpress = binding.rlExpress;
        btnEditCard = binding.btnEditCard;
        btnPayConfirm = binding.btnPayConfirm;
        tvCashOutTime = binding.tvCashOutTime;
        flag = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPayConfirm:
                callExpressFeeApi();
                break;
            case R.id.tvClose:

                Intent i = new Intent();
                if (flag == true) {
                    i.putExtra("driverAmount", "00");
                    setResult(0,i);
                }

                finish();
                break;
        }
    }

    private void callExpressFeeApi() {
        try {
            GetDriverData getDriverData = new Gson().fromJson(getSharedPreferences("driverData", 0).getString("driver", ""), GetDriverData.class);
            Integer id = getDriverData.getData().getId();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.EXPRESS_PAY.pay_amount, total_deposit_amount);
            jsonObject.put(WebFields.EXPRESS_PAY.driver_id, id);
            Applog.E("request: " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.EXPRESS_PAY.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("success: " + response.toString());
                    if (response.optString("error_code").equals("0")) {
                        try {
                            ExpressPayData expressPayData = new Gson().fromJson(String.valueOf(response), ExpressPayData.class);
                            MyProgressDialog.hideProgressDialog();
                            if (expressPayData.getError_code() == 0) {
                                flag = true;
                                ivExpressPay.setVisibility(View.GONE);
                                rlExpress.setVisibility(View.VISIBLE);
                                btnEditCard.setVisibility(View.GONE);
                                tvAboutPayBonus.setVisibility(View.VISIBLE);
                                btnPayConfirm.setVisibility(View.GONE);
                                Calendar c = Calendar.getInstance();
                                int day = c.get(Calendar.DAY_OF_MONTH);
                                int year = c.get(Calendar.YEAR);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                                String time = simpleDateFormat.format(c.getTime());
                                String monthName = new SimpleDateFormat("MMM").format(c.getTime());
                                String date = "Cashout at " + time + " on " + monthName + " " + day + "," + year;
                                tvCashOutTime.setText(date);
                                Log.w("curr_date", date);
                                SnackBar.showSuccess(context, snackBarView, response.getString("message"));

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
                    } else {
                        MyProgressDialog.hideProgressDialog();
                        SnackBar.showError(context, snackBarView, response.optString("message"));
                        //                       Toast.makeText(context, response.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();

                    Applog.E("Error: " + error.getMessage());

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

    /*private void callDriverMe() {
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

                            strState = driverDetail.getData().getShipping_adress().getState();
                            strShortName = strState.substring(strState.indexOf("(")+1,strState.indexOf(")"));
                            if(strShortName!= "")
                            {
                                callApiForFee();
                            }

//
                        } else {
                            MyProgressDialog.hideProgressDialog();
                        }
                    } catch (*//*JSON*//*Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());

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

    }*/

    private void callApiForFee() {


        GetDriverData getDriverData = new Gson().fromJson(getSharedPreferences("driverData", 0).getString("driver", ""), GetDriverData.class);
        try {
            if (getDriverData.getError_code() == 0) {
                account_number = getDriverData.getData().getBank_information().getAccount_number();
                //     account_number = String.valueOf(Base64.decode(account_number,Base64.DEFAULT));
                byte[] data = Base64.decode(account_number, Base64.DEFAULT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    cardNo = new String(data, StandardCharsets.UTF_8);
                }
                s = cardNo;

                String number = s.substring(8, 12);
                btnEditCard.setText("Edit ACC(****" + number + ")");
            } else {
                /*         MyProgressDialog.hideProgressDialog();*/
//                SnackBar.showError(context, snackBarView, response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }


       /* try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.TRIP.PARAM_STATE, strShortName);

            Applog.E("request Rate State => " + jsonObject.toString());
     *//*       MyProgressDialog.showProgressDialog(context);*//*
         *//*"http://kroslinkstech.in/greego/public/api/driver/express/pay"*//*
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,WebFields.BASE_URL+WebFields.EXPRESS_PAY.MODE
                    , jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {*/


//        Applog.E("success: " + response.toString());
//        GetTripRate tripRateData = new Gson().fromJson(String.valueOf(response), GetTripRate.class);
        String string = getSharedPreferences("driverData", 0).getString("fee", "");
        GetTripRate tripRateData = new Gson().fromJson(string, GetTripRate.class);
        try {
            MyProgressDialog.hideProgressDialog();
            if (tripRateData.getError_code() == 0) {
                greego_fee_per = Double.parseDouble("" + tripRateData.getData().getGreego_fee());
                express_fee = Double.parseDouble("" + tripRateData.getData().getExpress_fee());
                //   CalculateMile(str,elapsedTime);
                greego_fee_amount = getGreegoFee(driveAmount, greego_fee_per);
                tvGreegoFeeAmt.setText("-$" + String.format("%.2f",greego_fee_amount));
            //    tvGreegoFeeAmt.setText("-$" + new DecimalFormat("##.##").format(greego_fee_amount));
                tvExpressFeeAmt.setText("-$"+String.format("%.2f",express_fee));
              //  tvExpressFeeAmt.setText("-$" + new DecimalFormat("##.##").format(express_fee));
                Log.e("greego_fee", String.valueOf(greego_fee_per));
                Log.e("express_fee", String.valueOf(express_fee));
                Log.e("drive_fee", String.valueOf(driveAmount));
                total_deposit_amount = driveAmount - greego_fee_amount - express_fee;
                tvTotalDepositAmt.setText("$"+String.format("%.2f",total_deposit_amount));
//                tvTotalDepositAmt.setText("$" + new DecimalFormat("##.##").format(total_deposit_amount));

            } else {
                /*         MyProgressDialog.hideProgressDialog();*/
//                SnackBar.showError(context, snackBarView, response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
   /*             }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                 *//*   MyProgressDialog.hideProgressDialog();*//*
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
        }
*/
    }

    private Double getGreegoFee(Double driveAmount, double greego_fee_per) {
        greego_fee = (driveAmount * greego_fee_per) / 100;
        return greego_fee;
    }
}
