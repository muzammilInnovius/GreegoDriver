package com.greegoapp.greegodriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.ProfileStatus;
import com.greegoapp.greegodriver.Model.StateData;
import com.greegoapp.greegodriver.R;

import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityDriverShippingInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_SHIPPING_INFO;

public class DriverShippingInfoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivityDriverShippingInfoBinding binding;
    Context context;
    private View snackBarView;
    Spinner  spinnerState;
    TextInputEditText edtTvStreetAddress, edtTvApt, edtTvZipCode,spinnerCity;
    ImageButton ibCancel;
    Button btnNext;
  //  List<String> state,state_id;
    String[] citys = {"Washington", "Chicago", "New York", "Oxford"};
    String[] states = {"District of Columbia (DC)", "New York (NY)", "Illinois (IL)", "North Carolina (NC)"};
    int profileStatus;
    private TextView tvCity;
    EditText edtTvState;
    int state_id;
    ArrayAdapter<StateData.DataBean> stateListBeanArrayAdapter;
    public ArrayList<StateData.DataBean> states1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_shipping_info);
     /*   state=new ArrayList<>();
        state_id=new ArrayList<>();*/
        loadData();
        context = DriverShippingInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        bindView();
        setListners();


    }
    private class State_Response{
        List<States> data;

        public List<States> getStatesList() {
            return data;
        }

        public void setStatesList(List<States> statesList) {
            this.data = statesList;
        }

        private class States {
            String id;
            String state_name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getState_name() {
                return state_name;
            }

            public void setState_name(String state_name) {
                this.state_name = state_name;
            }
        }
    }
    private void loadData() {
        try {
            JSONObject jsonObject = new JSONObject();
            Applog.E("request: " + jsonObject.toString());
          //  MyProgressDialog.showProgressDialog(context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    WebFields.BASE_URL + WebFields.STATE_URL.MODE, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                 //   State_Response state_response = new Gson().fromJson(response.toString(),State_Response.class);
                    Applog.E("success: " + response.toString());
                   /* for (State_Response.States stateResponse:state_response.getStatesList()){
                        state.add(stateResponse.state_name);
                        state_id.add(stateResponse.id);
                    }*/
                    StateData state_response = new Gson().fromJson(response.toString(),StateData.class);
                    //                hidepDialog();
                    if (state_response.getError_code() == 0) {
                        states1 = new ArrayList<>();
                        states1.addAll(state_response.getData());

                        StateData.DataBean questionDatum1 = new StateData.DataBean();
                        questionDatum1.setId(0);
                        questionDatum1.setState_name("State");
                        states1.add(0, questionDatum1);


                            stateListBeanArrayAdapter = new ArrayAdapter<StateData.DataBean>(context, android.R.layout.simple_spinner_dropdown_item, states1) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    // Disable the first item from Spinner
                                    // First item will be use for hint
                                    return false;
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if (position == 0) {
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };

                        stateListBeanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinnerState.setAdapter(stateListBeanArrayAdapter);
                        spinnerState.setOnItemSelectedListener(stateListBeanListener);


                    } else {
                        SnackBar.showError(context, snackBarView, state_response.getMessage());
                    }
                    // MyProgressDialog.hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                 //   MyProgressDialog.hideProgressDialog();
                    Applog.E("Error: " + error.getMessage() + ":" + error.getStackTrace());
                  //  Toast.makeText(context, "error" + error.getMessage() + error.getStackTrace(), Toast.LENGTH_SHORT).show();
                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
                }
            })/* {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put(WebFields.PARAM_ACCEPT, "application/json");
                    params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));

                    return params;
                }
            }*/;
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalValues.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(this); // this = context
            queue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }

    }
    private AdapterView.OnItemSelectedListener stateListBeanListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
//                spinnerQue1.setVisibility(View.INVISIBLE);
                if (position > 0) {

                    final StateData.DataBean questionDatum = (StateData.DataBean) spinnerState.getItemAtPosition(position);
                  //  Log.e("state:" , String.valueOf(questionDatum.getId()));
                    state_id = questionDatum.getId();
                    edtTvState.setText(questionDatum.getState_name());
//                edtTxtState.setBackground(ContextCompat.getDrawable(context,R.drawable.green_rounded_edittext));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private void setListners() {
        edtTvStreetAddress.setOnClickListener(this);
        edtTvApt.setOnClickListener(this);
        spinnerState.setOnItemSelectedListener(this);
     /*   spinnerCity.setOnItemSelectedListener(this);*/
        edtTvZipCode.setOnClickListener(this);
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        edtTvState.setOnClickListener(this);
        spinnerState.setFocusable(true);
        spinnerState.setFocusableInTouchMode(true);

        spinnerState.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DriverShippingInfoActivity.this.spinnerState.performClick();
                    KeyboardUtility.hideKeyboard(context,spinnerState);
                    edtTvZipCode.requestFocus();
                }
            }


        });
    }

    private void bindView() {
        edtTvStreetAddress = binding.edtTvStreetAddress;
        edtTvApt = binding.edtTvAptName;
        spinnerState = binding.spinnerState;
        spinnerCity = binding.spinnerCity;
        edtTvZipCode = binding.edtTvZipCode;
        ibCancel = binding.ibCancel;
        btnNext = binding.btnNext;
        edtTvState = binding.edtTvState;
     //   tvCity = binding.tvCity;
       /* ArrayAdapter arrayAdapterCity = new ArrayAdapter(this, android.R.layout.simple_spinner_item, citys);
        arrayAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(arrayAdapterCity);*/

     /*   ArrayAdapter arrayAdapterState = new ArrayAdapter(this, android.R.layout.simple_spinner_item, state1);
        arrayAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(arrayAdapterState);*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtTvState:
                String edtState = edtTvState.getText().toString();
                if (edtState != null) {
                    spinnerState.performClick();
                } else {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        loadData();
                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
            case R.id.ibCancel:
                /*Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_SHIPPING_INFO,i);*/
                Intent in = new Intent(context, HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);


//                finish();
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        callShippingInfoAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
        }

    }

    String strStreet, strApt, strZipCode, strCity, strState,strState_id;

    private boolean isValid() {

        strStreet = edtTvStreetAddress.getText().toString();
        strApt = edtTvApt.getText().toString();
        strZipCode = edtTvZipCode.getText().toString();
        strCity=spinnerCity.getText().toString();
        strState=edtTvState.getText().toString();
        if (strStreet.isEmpty()) {
            edtTvStreetAddress.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_street_address));
            return false;
        } else if (strCity.isEmpty()) {
//            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_city));
            return false;
        } else if (strState.isEmpty()) {
//            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_state));
            return false;
        } else if (strZipCode.isEmpty()) {
            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_zip_code));
            return false;
        } else if (strZipCode.length() < 5) {
            edtTvZipCode.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_zip_code));
            return false;
        }
        return true;
    }

    private void callShippingInfoAPI() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_STREET, strStreet);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_APT, strApt);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_CITY, strCity);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_STATE, state_id);
            jsonObject.put(WebFields.SIGN_UP_SHIPPING_ADDRESS.PARAM_ZIPCODE, strZipCode);

            Applog.E("request driver shipping info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_SHIPPING_ADDRESS.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());

                    ProfileStatus userDetails = new Gson().fromJson(String.valueOf(response), ProfileStatus.class);
                    try {
                        MyProgressDialog.hideProgressDialog();
//
                        if (userDetails.getError_code() == 0) {

                            Applog.E("UserDetails" + userDetails);
//                            callUserMeApi();

                            SessionManager.setIsUserLoggedin(context, true);
//                            int profileStatus = userDetails.getData().getProfile_status();
//
                             profileStatus = userDetails.getData().getProfile_status();
//                            //getIs_agreed = 0 new user
                            setProfileScreen(profileStatus);
                           /* if(REQUEST_ADD_SHIPPING_INFO==1002)
                            {
                                Intent i= new Intent();
                                i.putExtra("profileStatus",profileStatus);
                                setResult(REQUEST_ADD_SHIPPING_INFO,i);
                            }*/
//                            Intent in = new Intent(context, DriverPersonalInfoActivity.class);
//                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(in);
//                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
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
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        strState_id=state_id.get(spinnerState.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
      //  strState_id=state_id.get(0);
    }

    Intent in;

    private void setProfileScreen(int profileStatus) {

        if (profileStatus == 0) {
            in = new Intent(context, SignUpEmailActivity.class);
        } else if (profileStatus == 1) {
            in = new Intent(context, DriverPersonalInfoActivity.class);
        } else if (profileStatus == 2) {
            in = new Intent(context, DriverShippingInfoActivity.class);
        } else if (profileStatus == 3) {
            in = new Intent(context, DriverAttachFileInfoActivity.class);
        } else if (profileStatus == 4) {
            in = new Intent(context, DriverBankInfoActivity.class);
        } else if (profileStatus == 5) {
            in = new Intent(context, DriverTypeInfoActivity.class);
        } else if (profileStatus == 6) {
            in = new Intent(context, DriverProfileInfoActivity.class);
        } else if (profileStatus == 7) {
            in = new Intent(context, HomeActivity.class);
        }
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
    }

}
