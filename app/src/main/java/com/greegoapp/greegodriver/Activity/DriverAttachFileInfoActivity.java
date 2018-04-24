package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.android.volley.request.SimpleMultiPartRequest;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.ProfileStatus;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityDriverAttachFileInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_FILES_INFO;
import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_SHIPPING_INFO;


public class DriverAttachFileInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverAttachFileInfoBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibCancel;
    Button btnNext;
    TextView tvHomeInsu,tvUberDriver;
    RelativeLayout rlHomeInsVer,rlUberDriverVer;
    ImageView imgVwReadyHomeInsDoc,imgVwReadyUberDoc,imgVwDriverVerification,imgVwDriverAutoInsurance,imgVwHomeInsurance,imgVwUberDriver;
    View viewHomeInsurance,viewUberDriver;
    private static int CAMARA_PERMIT = 1000;
    private static int GALLARY_PERMISSION = 2000;
    private static int SELECT_CAMERA_PIC = 99;
    private static int SELECT_GALLERY_PIC = 101;
    private String picturePath;
    static final int PICK_IMAGE_REQUEST = 1;
    int flag=0;
    int profileStatus;
    String filePath_driving_license,filePath_insurance_card,filePath_home_insurance,filePath_current_driver;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_driver_attach_file_info);
        context= DriverAttachFileInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvHomeInsu.setOnClickListener(this);
        tvUberDriver.setOnClickListener(this);
        imgVwDriverVerification.setOnClickListener(this);
        imgVwReadyHomeInsDoc.setOnClickListener(this);
        imgVwReadyUberDoc.setOnClickListener(this);
        imgVwUberDriver.setOnClickListener(this);
        imgVwDriverAutoInsurance.setOnClickListener(this);
        imgVwHomeInsurance.setOnClickListener(this);
    }

    private void bindView() {
        ibCancel=binding.ibCancel;
        btnNext=binding.btnNext;
        imgVwDriverVerification=binding.imgVwDriverVerification;
        tvHomeInsu=binding.tvHomeInsurance;
        imgVwReadyHomeInsDoc=binding.imgVwReadyHomeInsDoc;
        rlHomeInsVer=binding.rlHomeInsuranceVerification;
        viewHomeInsurance=binding.viewHomeInsurance;
        tvUberDriver=binding.tvUberDriver;
        imgVwReadyUberDoc=binding.imgVwReadyUberDoc;
        rlUberDriverVer=binding.rlUberDriverVerification;
        viewUberDriver=binding.viewUberDriver;
        imgVwDriverAutoInsurance = binding.imgVwDriverAutoInsurance;
        imgVwHomeInsurance=binding.imgVwHomeInsurance;
        imgVwUberDriver = binding.imgVwUberDriver;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ibCancel:
//                finish();
                Intent in = new Intent(context, HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
              /*  Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_FILES_INFO,i);*/
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
               /* if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                      *//*  *//*

                    *//*    saveProfileAccount();*//*
                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }*/
                if (filePath_current_driver != null && filePath_driving_license!=null && filePath_home_insurance!=null && filePath_insurance_card!=null) {

                    imageUpload(filePath_driving_license,filePath_insurance_card,filePath_home_insurance,filePath_current_driver);
//                    callAttachFileInfoAPI();
                } else {
                    Toast.makeText(getApplicationContext(), "Image not selected!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tvHomeInsurance:
                if (rlHomeInsVer .getVisibility() == View.VISIBLE) {
                    rlHomeInsVer.setVisibility(View.GONE);
                    imgVwReadyHomeInsDoc.setVisibility(View.GONE);
                    tvHomeInsu.setTextColor(getResources().getColor(R.color.black));
                    viewHomeInsurance.setBackgroundColor(getResources().getColor(R.color.black));
                }
                else {
                    rlHomeInsVer.setVisibility(View.VISIBLE);
                    imgVwReadyHomeInsDoc.setVisibility(View.VISIBLE);
                    tvHomeInsu.setTextColor(getResources().getColor(R.color.success_bg));
                    viewHomeInsurance.setBackgroundColor(getResources().getColor(R.color.success_bg));
                }
                break;
            case R.id.tvUberDriver:
                if (rlUberDriverVer .getVisibility() == View.VISIBLE) {
                    rlUberDriverVer.setVisibility(View.GONE);
                    imgVwReadyUberDoc.setVisibility(View.GONE);
                    tvUberDriver.setTextColor(getResources().getColor(R.color.black));
                    viewUberDriver.setBackgroundColor(getResources().getColor(R.color.black));
                }
                else {
                    rlUberDriverVer.setVisibility(View.VISIBLE);
                    imgVwReadyUberDoc.setVisibility(View.VISIBLE);
                    tvUberDriver.setTextColor(getResources().getColor(R.color.success_bg));
                    viewUberDriver.setBackgroundColor(getResources().getColor(R.color.success_bg));
                }
                break;
            case R.id.imgVwDriverVerification:
                flag =1;
                if (requestPermission()) {
                    /*ImageCapturing();*/
                    imageBrowse();
                }
                break;
            case R.id.imgVwDriverAutoInsurance:
                flag=2;
                if (requestPermission()) {
                    /*ImageCapturing();*/
                    imageBrowse();
                }
                break;
            case R.id.imgVwHomeInsurance:
                flag=3;
                if (requestPermission()) {
                  /*  ImageCapturing();*/
                    imageBrowse();
                }
                break;
            case R.id.imgVwUberDriver:
                flag=4;
                if (requestPermission()) {
                   /* ImageCapturing();*/
                    imageBrowse();
                }
                break;

        }
    }
    private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void imageUpload(String filePath_driving_license, String filePath_insurance_card, String filePath_home_insurance, String filePath_current_driver) {
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, WebFields.BASE_URL + WebFields.SIGN_UP_UPLOAD_FILE.MODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");

                            ProfileStatus userDetails = new Gson().fromJson(String.valueOf(response), ProfileStatus.class);
                            if (userDetails.getError_code() == 0) {
                                 profileStatus = userDetails.getData().getProfile_status();
//                            //getIs_agreed = 0 new user
                                setProfileScreen(profileStatus);
                              /*  if(REQUEST_ADD_FILES_INFO==1003)
                                {
                                    Intent i= new Intent();
                                    i.putExtra("profileStatus",profileStatus);
                                    setResult(REQUEST_ADD_FILES_INFO,i);
                                }*/
                              //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(WebFields.PARAM_ACCEPT, "application/json");
                params.put(WebFields.PARAM_AUTHOTIZATION, GlobalValues.BEARER_TOKEN + SessionManager.getToken(context));

                return params;
            }};

        smr.addFile(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_DRIVING_LICENSE,filePath_driving_license);
        smr.addFile(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_INSURANCE_CARD,filePath_insurance_card);
        smr.addFile(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_HOME_INSURANCE,filePath_home_insurance);
        smr.addFile(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_CURRENT_DRIVER,filePath_current_driver);
        AppController.getInstance().addToRequestQueue(smr);

    }

    private boolean isValid() {
        return true;
    }

    private void callAttachFileInfoAPI() {
//        Intent in = new Intent(context, DriverBankInfoActivity.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(in);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);

        try {
            JSONObject jsonObject = new JSONObject();

//            jsonObject.put(WebFields.SIGN_UP_ATTACH_FILE.PARAM_STREET, strStreet);
//            jsonObject.put(WebFields.SIGN_UP_ATTACH_FILE.PARAM_APT, strApt);
//            jsonObject.put(WebFields.SIGN_UP_ATTACH_FILE.PARAM_CITY, strCity);
//            jsonObject.put(WebFields.SIGN_UP_ATTACH_FILE.PARAM_STATE, strState);
//            jsonObject.put(WebFields.SIGN_UP_ATTACH_FILE.PARAM_ZIPCODE, strZipCode);

            Applog.E("request driver shipping info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_ATTACH_FILE.MODE, jsonObject, new Response.Listener<JSONObject>() {

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
                            int profileStatus = userDetails.getData().getProfile_status();
//                            //getIs_agreed = 0 new user
                            setProfileScreen(profileStatus);
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
    private void ImageCapturing() {

        final String[] DialogOption = {"Camera", "Cancel"};
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(context);
        dialog1.setTitle("Select Option");
        dialog1.setCancelable(false);
        //dialog1.setIcon(getResources().getDrawable(R.drawable.ic_menu_camera));

        dialog1.setItems(DialogOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (DialogOption[i].equals("Camera")) {
                    Intent int1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(int1, CAMARA_PERMIT);
                }/* else if (DialogOption[i].equals("From Gallary")) {
                    Intent int1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(int1, GALLARY_PERMISSION);
                } */else if (DialogOption[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }

            }
        });
        dialog1.show();

    }

    public boolean requestPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result != PackageManager.PERMISSION_GRANTED || result1 != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_CAMERA_PIC);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_CAMERA_PIC);
            }
            return false;
        } else {

            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

            /*    filePath = getPath(picUri);
                filePath1 =getPath(picUri);
                filePath2 =getPath(picUri);
                filePath3 =getPath(picUri);
                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);*/
                if(flag==1)
                {
                    imgVwDriverVerification.setVisibility(View.VISIBLE);
                    imgVwDriverVerification.setImageURI(picUri);
                    filePath_driving_license = getPath(picUri);
                    Toast.makeText(context,filePath_driving_license,Toast.LENGTH_LONG).show();
                }
                if(flag==2)
                {
                    imgVwDriverAutoInsurance.setVisibility(View.VISIBLE);
                    imgVwDriverAutoInsurance.setImageURI(picUri);
                    filePath_insurance_card =getPath(picUri);
                }
                if(flag==3)
                {
                    imgVwHomeInsurance.setVisibility(View.VISIBLE);
                    imgVwHomeInsurance.setImageURI(picUri);
                    filePath_home_insurance = getPath(picUri);
                }
                if(flag==4)
                {
                    imgVwUberDriver.setVisibility(View.VISIBLE);
                    imgVwUberDriver.setImageURI(picUri);
                    filePath_current_driver = getPath(picUri);
                }
            }

        }

        /* if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMARA_PERMIT) {

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                picturePath = storingImage(photo);
                if(flag==1)
                {
                    imgVwDriverVerification.setVisibility(View.VISIBLE);
                    imgVwDriverVerification.setImageBitmap(photo);
                    filePath_driving_license = data.getData().getPath();

                }
                if(flag==2)
                {
                    imgVwDriverAutoInsurance.setVisibility(View.VISIBLE);
                    imgVwDriverAutoInsurance.setImageBitmap(photo);
                    filePath_insurance_card =data.getData().getPath();
                }
                if(flag==3)
                {
                    imgVwHomeInsurance.setVisibility(View.VISIBLE);
                    imgVwHomeInsurance.setImageBitmap(photo);
                    filePath_home_insurance = data.getData().getPath();
                }
                if(flag==4)
                {
                    imgVwUberDriver.setVisibility(View.VISIBLE);
                    imgVwUberDriver.setImageBitmap(photo);
                    filePath_current_driver = data.getData().getPath();
                }

                uri = data.getData();
                // Toast.makeText(getActivity(), picturePath, Toast.LENGTH_LONG).show();
            } else if (requestCode == GALLARY_PERMISSION) {
                uri = data.getData();
                String[] imageFile = {MediaStore.Images.Media.DATA};

                Cursor cursor = getApplicationContext().getContentResolver().query(uri, imageFile, null, null, null);
                cursor.moveToFirst();
                int colIndex = cursor.getColumnIndex(imageFile[0]);
                String photo = cursor.getString(colIndex);
                cursor.close();

                //Bitmap image=(BitmapFactory.decodeFile(photo));
                //DisplayImage.setImageBitmap(image);
                if(flag==1)
                {
                    imgVwDriverVerification.setVisibility(View.VISIBLE);
                    imgVwDriverVerification.setImageURI(uri);
                    imgVwDriverVerification.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    filePath_driving_license = uri.getPath();
                }
                if(flag==2)
                {
                    imgVwDriverAutoInsurance.setVisibility(View.VISIBLE);
                    imgVwDriverAutoInsurance.setImageURI(uri);
                    imgVwDriverAutoInsurance.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    filePath_insurance_card = uri.getPath();
                }
                if(flag==3)
                {
                    imgVwHomeInsurance.setVisibility(View.VISIBLE);
                    imgVwHomeInsurance.setImageURI(uri);
                    imgVwHomeInsurance.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    filePath_home_insurance = uri.getPath();
                }
                if(flag==4)
                {
                    imgVwUberDriver.setVisibility(View.VISIBLE);
                    imgVwUberDriver.setImageURI(uri);
                    imgVwUberDriver.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    filePath_current_driver = uri.getPath();
                }
               // picturePath = uri.getPath();
                // Toast.makeText(getActivity(), picturePath, Toast.LENGTH_LONG).show();

            }


        }*/

    }
    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        Log.e("result path",result);
        return result;
    }

    String storingImage(Bitmap bitmap) {
        int i = 10000;
        String path = null;
        //i = 1 + 5;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root, "Greego");
        myDir.mkdirs();
        Random random = new Random();
        i = random.nextInt();
        File outputFile = new File(myDir, "photo_" + i + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            path = outputFile.getPath();

            //Toast.makeText(getActivity(), path, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }



}
