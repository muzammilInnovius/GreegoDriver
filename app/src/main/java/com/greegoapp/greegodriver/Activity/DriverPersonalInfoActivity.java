package com.greegoapp.greegodriver.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.greegoapp.greegodriver.Model.DriverPersnlInfoUpdateData;
import com.greegoapp.greegodriver.Model.ProfileStatus;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityDriverPersonalInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_PERSONAL_INFO;
import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_SHIPPING_INFO;

public class DriverPersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverPersonalInfoBinding binding;
    Context context;
    private View snackBarView;
    EditText tvDateOfBirth;

    TextInputEditText edtTvFirstName, edtTvMiddleName, edtTvLastName, edtTvSocSecNum;
    Button btnNext;
    ImageButton ibCancel;
    int mYear, mMonth, mDay;
    int profileStatus;

    String strFirstname, strMiddlename, strLastname, strSocSecNum, strDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_personal_info);
        context = DriverPersonalInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        bindView();
        setListners();
        tvDateOfBirth.setInputType(InputType.TYPE_NULL);
        edtTvSocSecNum.addTextChangedListener(new TextWatcher() {

            private boolean spaceDeleted;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                CharSequence charDeleted = s.subSequence(start, start + count);
                spaceDeleted = "-".equals(charDeleted.toString());
            }

            public void afterTextChanged(Editable editable) {

                edtTvSocSecNum.removeTextChangedListener(this);

                int cursorPosition = edtTvSocSecNum.getSelectionStart();
                String withSpaces = formatText(editable);
                edtTvSocSecNum.setText(withSpaces);

                edtTvSocSecNum.setSelection(cursorPosition + (withSpaces.length() - editable.length()));


                if (spaceDeleted) {
                    //  userNameET.setSelection(userNameET.getSelectionStart() - 1);
                    spaceDeleted = false;
                }


                edtTvSocSecNum.addTextChangedListener(this);
            }

            private String formatText(CharSequence text) {
                StringBuilder formatted = new StringBuilder();
                int count = 0;
                if (text.length() == 3 || text.length() == 6) {
                    if (!spaceDeleted)
                        formatted.append(text + "-");
                    else
                        formatted.append(text);
                } else
                    formatted.append(text);
                return formatted.toString();
            }
        });
    }

    private void setListners() {
        edtTvFirstName.setOnClickListener(this);
        edtTvMiddleName.setOnClickListener(this);
        edtTvLastName.setOnClickListener(this);
        edtTvSocSecNum.setOnClickListener(this);
        tvDateOfBirth.setOnClickListener(this);
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        edtTvFirstName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
        edtTvMiddleName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
        edtTvLastName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
        tvDateOfBirth.setFocusable(true);
        tvDateOfBirth.setFocusableInTouchMode(true);

        tvDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    tvDateOfBirth.performClick();
                    KeyboardUtility.hideKeyboard(context,tvDateOfBirth);
                }
            }


        });
    }

    private void bindView() {
        edtTvFirstName = binding.edtTvFirstName;
        edtTvLastName = binding.edtTvLastName;
        edtTvMiddleName = binding.edtTvMiddleName;
        edtTvSocSecNum = binding.edtTvSocialSecurityNumber;
        tvDateOfBirth = binding.tvDateOfBirth;
        btnNext = binding.btnNext;
        ibCancel = binding.ibCancel;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibCancel:

             /*   Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_PERSONAL_INFO,i);*/
                Intent in = new Intent(context, HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                        callPersonalInfoAPI();

                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
            case R.id.tvDateOfBirth:
                getDate();
                break;
        }

    }

    private void callPersonalInfoAPI() {
//        Intent in = new Intent(context, DriverShippingInfoActivity.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(in);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
        //strSocSecNum = strSocSecNum.replaceAll("-", "");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebFields.SIGN_UP_PERSONAL_INFO.PARAM_LEGAL_FNAME, strFirstname);
            jsonObject.put(WebFields.SIGN_UP_PERSONAL_INFO.PARAM_LEGAL_LNAME, strLastname);
            jsonObject.put(WebFields.SIGN_UP_PERSONAL_INFO.PARAM_LEGAL_MNAME, strMiddlename);
            jsonObject.put(WebFields.SIGN_UP_PERSONAL_INFO.PARAM_SOCIAL_SICURITY_NO, strSocSecNum);
            jsonObject.put(WebFields.SIGN_UP_PERSONAL_INFO.PARAM_DATE_OF_BIRTH, strDateOfBirth);

            Applog.E("request driver personal info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PERSONAL_INFO.MODE, jsonObject, new Response.Listener<JSONObject>() {

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
                           /* if(REQUEST_ADD_PERSONAL_INFO==1001)
                            {
                                Intent i= new Intent();
                                i.putExtra("profileStatus",profileStatus);
                                setResult(REQUEST_ADD_PERSONAL_INFO,i);
                            }*/
////
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


    private boolean isValid() {

        strFirstname = edtTvFirstName.getText().toString();
        strMiddlename = edtTvMiddleName.getText().toString();
        strLastname = edtTvLastName.getText().toString();
        strSocSecNum = edtTvSocSecNum.getText().toString();
        strDateOfBirth = tvDateOfBirth.getText().toString().trim();

        if (strFirstname.isEmpty()) {
            edtTvFirstName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_first_name));
            return false;
        } /*else if (strMiddlename.isEmpty()) {
            edtTvMiddleName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_middle_name));
            return false;
        }*/ else if (strLastname.isEmpty()) {
            edtTvLastName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_last_name));
            return false;
        } else if (strSocSecNum.isEmpty()) {
            edtTvSocSecNum.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_social_security_number));
            return false;
        } else if (strSocSecNum.length() < 11) {
            edtTvSocSecNum.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.valid_social_security_number));
            return false;
        } else if (strDateOfBirth.isEmpty()) {
            tvDateOfBirth.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.empty_date_of_birth));
            return false;
        }
        return true;
    }

    public static int age(Date birthday, Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(formatter.format(birthday));
        int d2 = Integer.parseInt(formatter.format(date));
        int age = (d2 - d1) / 10000;
        return age;
    }

    private void getDate() {

        try {
            // Get Current Date
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear + 1;
                            mDay = dayOfMonth;
                            String formattedMonth = "" + mMonth;
                            String formattedDayOfMonth = "" + mDay;
                            Calendar thatDay = Calendar.getInstance();
                            thatDay.set(Calendar.DAY_OF_MONTH, mDay);
                            thatDay.set(Calendar.MONTH, mMonth); // 0-11 so 1 less
                            thatDay.set(Calendar.YEAR, mYear);

                            Calendar today = Calendar.getInstance();

                            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
//                            Here's an approximation...

                            long days = diff / (24 * 60 * 60 * 1000);
                            long age = days / 365;
                            if (age >= 18 && age <= 100) {
                                String mon = String.valueOf(mMonth);
                                if (mon.length() < 2) {

                                    formattedMonth = "0" + (mMonth);
                                }
                                if (dayOfMonth < 10) {

                                    formattedDayOfMonth = "0" + mDay;
                                }
                                tvDateOfBirth.setText(new StringBuilder().append(year).append("-").append(formattedMonth).append("-").append(formattedDayOfMonth).append(" "));
                                //strFrom = txtVwWrkgSncFrom.getText().toString();
                            } else {
                                if(age < 18 ) {
                                    SnackBar.showError(context, snackBarView, "Driver age should be 18 or more.");
                                }else if(age > 100)
                                {
                                    SnackBar.showError(context, snackBarView, "Driver age should be 100 or less.");
                                }
                                }
                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }

    }
}
