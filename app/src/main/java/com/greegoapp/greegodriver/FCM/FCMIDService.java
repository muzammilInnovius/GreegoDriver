package com.greegoapp.greegodriver.FCM;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Activity.SignUpDigitCodeActivity;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.Login;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;

import org.json.JSONException;
import org.json.JSONObject;

public class FCMIDService extends Service {
    private String registerFCMKey,strMobileNo;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerFCMKey = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("regId", "");
        strMobileNo = SessionManager.getMobileNo(getApplicationContext());
        Applog.E("FCM updated "+strMobileNo+"with "+registerFCMKey);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        registerFCMKey = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("regId", "");
        strMobileNo = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("contact_number", "");
        Applog.E("FCM updated with "+registerFCMKey);
//        new Runnable(){
//
//            @Override
//            public void run() {
//                callMobileNumberAPI();
//                try {
//                    Applog.E("FCM updated with "+registerFCMKey);
//                    this.wait(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.run();
        return null;
    }
    private void callMobileNumberAPI() {
       /* Intent in = new Intent(context, SignUpDigitCodeActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);*/
        try {
            JSONObject jsonObject = new JSONObject();
//            String token = SessionManager.getToken(context);
//            Applog.E("Token" + token);

            jsonObject.put(WebFields.SIGN_IN.PARAM_CONTACT_NO, strMobileNo);
            jsonObject.put(WebFields.SIGN_IN.PARAM_IS_PHONE_NO, 0);
            jsonObject.put(WebFields.SIGN_IN.PARAM_USER_TYPE, GlobalValues.USER_TYPE);
            jsonObject.put(WebFields.SIGN_IN.PARAM_DEVICE_ID, registerFCMKey);
            Applog.E("request: " + jsonObject.toString());
            //MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_IN.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage());
                }
            });
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
