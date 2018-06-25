package com.greegoapp.greegodriver.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.AppController.AppController;
import com.greegoapp.greegodriver.GlobleFields.GlobalValues;
import com.greegoapp.greegodriver.Model.DocumentUploadModel;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.SessionManager.SessionManager;
import com.greegoapp.greegodriver.Utils.Applog;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityDriverAttachFileInfoBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_PROFILE_INFO;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;


public class DriverAttachFileInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static int MAX_IMAGE_DIMENSION = 60 * 60;
    ActivityDriverAttachFileInfoBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibCancel;
    Button btnNext;
    TextView tvHomeInsu, tvUberDriver;
    RelativeLayout rlHomeInsVer, rlUberDriverVer;
    ImageView imgVwReadyHomeInsDoc, imgVwReadyUberDoc, imgVwDriverVerification, imgVwDriverAutoInsurance, imgVwHomeInsurance, imgVwUberDriver;
    View viewHomeInsurance, viewUberDriver;


    private static final int SELECT_CAMERA_PIC = 99;
    private static int SELECT_GALLERY_PIC = 101;


    int flag = 0;
    int profileStatus;
    private Uri mImageUri;
    String filePath_driving_license, filePath_insurance_card, filePath_home_insurance, filePath_current_driver;
    private Bitmap bitmap = null;
    Uri uri;
    private int flag_driving_license, flag_insurance_card, flag_home_insurance, flag_current_driver = 0;
    DocumentUploadModel data;
    CropImageView cImgVwDriverVerification, cImgVwDriverAutoInsurance, cImgVwHomeInsurance, cImgVwUberDriver;
    private Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_attach_file_info);
        context = DriverAttachFileInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
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
        cImgVwDriverVerification.setOnClickListener(this);
        cImgVwDriverAutoInsurance.setOnClickListener(this);
        cImgVwHomeInsurance.setOnClickListener(this);
        cImgVwUberDriver.setOnClickListener(this);
    }

    private void bindView() {
        ibCancel = binding.ibCancel;
        btnNext = binding.btnNext;
        imgVwDriverVerification = binding.imgVwDriverVerification;
        tvHomeInsu = binding.tvHomeInsurance;
        imgVwReadyHomeInsDoc = binding.imgVwReadyHomeInsDoc;
        rlHomeInsVer = binding.rlHomeInsuranceVerification;
        viewHomeInsurance = binding.viewHomeInsurance;
        tvUberDriver = binding.tvUberDriver;
        imgVwReadyUberDoc = binding.imgVwReadyUberDoc;
        rlUberDriverVer = binding.rlUberDriverVerification;
        viewUberDriver = binding.viewUberDriver;
        imgVwDriverAutoInsurance = binding.imgVwDriverAutoInsurance;
        imgVwHomeInsurance = binding.imgVwHomeInsurance;
        imgVwUberDriver = binding.imgVwUberDriver;
        filePath_driving_license = "";
        filePath_insurance_card = "";
        filePath_current_driver = "";
        filePath_home_insurance = "";
        cImgVwDriverVerification = binding.cImgVwDriverVerification;
        cImgVwDriverAutoInsurance = binding.cImgVwDriverAutoInsurance;
        cImgVwHomeInsurance = binding.cImgVwHomeInsurance;
        cImgVwUberDriver = binding.cImgVwUberDriver;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibCancel:
                Intent in = new Intent(context, HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);

                try {
                    if (flag_driving_license != 0 && flag_insurance_card != 0) {
                        if (flag_driving_license == 1 || flag_insurance_card == 1 || flag_home_insurance == 1 || flag_current_driver == 1) {

                            SnackBar.showSuccess(context, snackBarView, "Document uploading...");
                        } else if (flag_driving_license == 2 && flag_insurance_card == 2) {
                            SessionManager.setIsUserLoggedin(context, true);
                            setProfileScreen(profileStatus);
                            if (REQUEST_ADD_PROFILE_INFO == 1006) {
                                Intent i = new Intent();
                                i.putExtra("profileStatus", profileStatus);
                                setResult(REQUEST_ADD_PROFILE_INFO, i);
                            }
                        }
                    } else {
                        SnackBar.showValidationError(context, snackBarView, getString(R.string.select_image));
                    }
                    //  imageUpload(filePath_driving_license,filePath_current_driver,filePath_home_insurance,filePath_insurance_card);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    Bugsnag.notify(throwable);
                }

                break;
            case R.id.tvHomeInsurance:
                if (rlHomeInsVer.getVisibility() == View.VISIBLE) {
                    rlHomeInsVer.setVisibility(View.GONE);
                    imgVwReadyHomeInsDoc.setVisibility(View.GONE);
                    tvHomeInsu.setTextColor(getResources().getColor(R.color.black));
                    viewHomeInsurance.setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    rlHomeInsVer.setVisibility(View.VISIBLE);
                    imgVwReadyHomeInsDoc.setVisibility(View.VISIBLE);
                    tvHomeInsu.setTextColor(getResources().getColor(R.color.success_bg));
                    viewHomeInsurance.setBackgroundColor(getResources().getColor(R.color.success_bg));
                }
                break;
            case R.id.tvUberDriver:
                if (rlUberDriverVer.getVisibility() == View.VISIBLE) {
                    rlUberDriverVer.setVisibility(View.GONE);
                    imgVwReadyUberDoc.setVisibility(View.GONE);
                    tvUberDriver.setTextColor(getResources().getColor(R.color.black));
                    viewUberDriver.setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    rlUberDriverVer.setVisibility(View.VISIBLE);
                    imgVwReadyUberDoc.setVisibility(View.VISIBLE);
                    tvUberDriver.setTextColor(getResources().getColor(R.color.success_bg));
                    viewUberDriver.setBackgroundColor(getResources().getColor(R.color.success_bg));
                }
                break;
            case R.id.imgVwDriverVerification:
                flag = 1;
                if (requestPermission()) {
                    /*ImageCapturing();*/

                    //priyanka 6-6
                    //imageBrowse();
                    // showPicProfileDialog();
                    cImgVwDriverVerification.performClick();

                    /*  getImage();*/
                }
                break;
            case R.id.imgVwDriverAutoInsurance:
                flag = 2;
                if (requestPermission()) {
                    /*ImageCapturing();*/
                    //imageBrowse();
                    // showPicProfileDialog();
                    cImgVwDriverAutoInsurance.performClick();
                }
                break;
            case R.id.imgVwHomeInsurance:
                flag = 3;
                if (requestPermission()) {
                    /*  ImageCapturing();*/
                    // imageBrowse();
//                    showPicProfileDialog();
                    cImgVwHomeInsurance.performClick();
                }
                break;
            case R.id.imgVwUberDriver:
                flag = 4;
                if (requestPermission()) {
                    /* ImageCapturing();*/
                    //   imageBrowse();
//                    showPicProfileDialog();
                    cImgVwUberDriver.performClick();
                }
                break;
            case R.id.cImgVwDriverVerification:
                CropImage.startPickImageActivity(this);
                break;
            case R.id.cImgVwDriverAutoInsurance:
                CropImage.startPickImageActivity(this);
                break;

            case R.id.cImgVwHomeInsurance:
                CropImage.startPickImageActivity(this);
                break;
            case R.id.cImgVwUberDriver:
                CropImage.startPickImageActivity(this);
                break;

        }
    }

    private void showPicProfileDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Greego App").setMessage("Choose Document");


            builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_GALLERY_PIC);
                }
            });

            builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    openCamera();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, SELECT_CAMERA_PIC);
    }

    class DocumentUpload extends AsyncTask<Void, Void, String> {

        String name, image;
        Uri uri;

        DocumentUpload(String name, String image, Uri uri) {
            this.name = name;
            this.image = image;
            this.uri = uri;
        }

        @Override
        protected String doInBackground(Void... voids) {


            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put(WebFields.DOCUMENT_UPLOAD.name, name);
                jsonObject.put(WebFields.DOCUMENT_UPLOAD.image, image);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        WebFields.BASE_URL + WebFields.DOCUMENT_UPLOAD.MODE, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("aj", name + " sucess: " + response.toString());
                        data = new Gson().fromJson(String.valueOf(response), DocumentUploadModel.class);
                        if (data.getError_code() == 0) {
                            if (name.equals(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_DRIVING_LICENSE)) {
                                flag_driving_license = 2;
                                Glide.with(getApplicationContext()).load(uri).into(imgVwDriverVerification);
                            } else if (name.equals(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_INSURANCE_CARD)) {
                                Glide.with(getApplicationContext()).load(uri).into(imgVwDriverAutoInsurance);
                                flag_insurance_card = 2;
                            } else if (name.equals(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_HOME_INSURANCE)) {
                                Glide.with(getApplicationContext()).load(uri).into(imgVwHomeInsurance);
                                flag_home_insurance = 2;
                            } else if (name.equals(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_CURRENT_DRIVER)) {
                                Glide.with(getApplicationContext()).load(uri).into(imgVwUberDriver);
                                flag_current_driver = 2;
                            }
                            profileStatus = data.getData().getProfile_status();
                        } else {
                            SnackBar.showError(context, snackBarView, data.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DocumentUpload catTypeDriver = new DocumentUpload(name, image,uri);
                        catTypeDriver.execute();
                        Log.e("aj", "recall");
                        Log.e("aj", "Error: " + error.getMessage());

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
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
                Bugsnag.notify(e);
            }

            return "true";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case SELECT_CAMERA_PIC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {                    // check whether storage permission granted or not.
                    //do what you want;
                    // imageBrowse();
                    //showPicProfileDialog();
                    if(flag==1)
                    {
                        cImgVwDriverVerification.performClick();
                    }else if(flag==2)
                    {
                        cImgVwDriverAutoInsurance.performClick();
                    }else if(flag==3)
                    {
                        cImgVwHomeInsurance.performClick();
                    }else if(flag==4)
                    {
                        cImgVwUberDriver.performClick();
                    }
                }
                break;
            case CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE:
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);

                } else {
          //          Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            //priyanka(15-5) start
            /*if (requestCode == SELECT_GALLERY_PIC) {
                try {
                    if (flag == 1) {
                        flag_driving_license = 1;
                        mImageUri = data.getData();
                        try {
                            bitmap = getBitmapFromUri(mImageUri);
                            bitmap = scaleImage(this, mImageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            Bugsnag.notify(throwable);
                        }
                        imgVwDriverVerification.setImageURI(mImageUri);
                        filePath_driving_license = encodeTobase64(bitmap);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_DRIVING_LICENSE, filePath_driving_license);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                    if (flag == 2) {
                       *//* mImageUri = data.getData();
                        bitmap = getBitmapFromUri(mImageUri);
                        bitmap = scaleImage(this, mImageUri);
                        imgVwDriverAutoInsurance.setImageURI(mImageUri);
                        filePath_insurance_card = encodeTobase64(bitmap);*//*
                        flag_insurance_card = 1;
                        mImageUri = data.getData();
                        try {
                            bitmap = getBitmapFromUri(mImageUri);
                            bitmap = scaleImage(this, mImageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            Bugsnag.notify(throwable);
                        }
                        imgVwDriverAutoInsurance.setImageURI(mImageUri);
                        filePath_insurance_card = encodeTobase64(bitmap);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_INSURANCE_CARD, filePath_insurance_card);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                    if (flag == 3) {
                      *//*  mImageUri = data.getData();
                        bitmap = getBitmapFromUri(mImageUri);
                        bitmap = scaleImage(this, mImageUri);
                        imgVwHomeInsurance.setImageURI(mImageUri);
                        filePath_home_insurance = encodeTobase64(bitmap);*//*
                        flag_home_insurance = 1;
                        mImageUri = data.getData();
                        try {
                            bitmap = getBitmapFromUri(mImageUri);
                            bitmap = scaleImage(this, mImageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            Bugsnag.notify(throwable);
                        }
                        imgVwHomeInsurance.setImageURI(mImageUri);
                        filePath_home_insurance = encodeTobase64(bitmap);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_HOME_INSURANCE, filePath_home_insurance);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                    if (flag == 4) {
                      *//*  mImageUri = data.getData();
                        bitmap = getBitmapFromUri(mImageUri);
                        bitmap = scaleImage(this, mImageUri);
                        imgVwUberDriver.setImageURI(mImageUri);
                        filePath_current_driver = encodeTobase64(bitmap);*//*
                        flag_current_driver = 1;
                        mImageUri = data.getData();
                        try {
                            bitmap = getBitmapFromUri(mImageUri);
                            bitmap = scaleImage(this, mImageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            Bugsnag.notify(throwable);
                        }
                        imgVwUberDriver.setImageURI(mImageUri);
                        filePath_current_driver = encodeTobase64(bitmap);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_CURRENT_DRIVER, filePath_current_driver);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    Bugsnag.notify(throwable);
                }
            } else if (requestCode == SELECT_CAMERA_PIC) {
                try {


                    if (flag == 1) {
                        *//*Bitmap photo = (Bitmap) data.getExtras().get("data");

                        imgVwProPic.setImageBitmap(photo);

                        Uri tempUri = getImageUri(photo);
                        File finalFile = new File(getRealPathFromURI(tempUri));

                        imgVwProPic.setImageURI(getImageUri(imageOreintationValidator(photo, String.valueOf(finalFile))));

                        strProPicBase64 = encodeTobase64(photo);

                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            imageUpload(strProPicBase64);

                        } else {
                            SnackBar.showInternetError(context, snackBarView);

                        }*//*
                        flag_driving_license = 1;
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        imgVwDriverVerification.setImageBitmap(photo);

                     *//*   Uri tempUri = getImageUri(photo);
                        File finalFile = new File(getRealPathFromURI(tempUri));
                        imgVwDriverVerification.setImageURI(getImageUri(imageOreintationValidator(photo, String.valueOf(finalFile))));*//*

                        filePath_driving_license = encodeTobase64(photo);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_DRIVING_LICENSE, filePath_driving_license);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                    if (flag == 2) {
                        flag_insurance_card = 1;
                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                        imgVwDriverAutoInsurance.setImageBitmap(photo);

                      *//*  Uri tempUri = getImageUri(photo);
                        File finalFile = new File(getRealPathFromURI(tempUri));
                        imgVwDriverAutoInsurance.setImageURI(getImageUri(imageOreintationValidator(photo, String.valueOf(finalFile))));*//*

                        filePath_insurance_card = encodeTobase64(photo);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_INSURANCE_CARD, filePath_insurance_card);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                    if (flag == 3) {
                        flag_home_insurance = 1;
                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                        imgVwHomeInsurance.setImageBitmap(photo);

                       *//* Uri tempUri = getImageUri(photo);
                        File finalFile = new File(getRealPathFromURI(tempUri));

                        imgVwHomeInsurance.setImageURI(getImageUri(imageOreintationValidator(photo, String.valueOf(finalFile))));
*//*
                        filePath_home_insurance = encodeTobase64(photo);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_HOME_INSURANCE, filePath_home_insurance);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }
                    }
                    if (flag == 4) {
                        flag_current_driver = 1;
                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                        imgVwUberDriver.setImageBitmap(photo);

                      *//*  Uri tempUri = getImageUri(photo);
                        File finalFile = new File(getRealPathFromURI(tempUri));

                        imgVwUberDriver.setImageURI(getImageUri(imageOreintationValidator(photo, String.valueOf(finalFile))));
*//*
                        filePath_current_driver = encodeTobase64(photo);
                        if (ConnectivityDetector.isConnectingToInternet(context)) {
                            //    imageUpload();
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_CURRENT_DRIVER, filePath_current_driver);
                            catTypeDriver.execute();
                        } else {
                            SnackBar.showInternetError(context, snackBarView);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    Bugsnag.notify(throwable);
                }


            }*/
            Uri imageUri = null;
            // handle result of pick image chooser
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                imageUri = CropImage.getPickImageResultUri(this, data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    // no permissions required or already granted, can start crop image activity
                    startCropImageActivity(imageUri);
                }
            } else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    if(flag == 1)
                    {
                        flag_driving_license = 1;
//                        ((ImageView) findViewById(R.id.imgVwDriverVerification)).setImageURI(CropImage.getActivityResult(data).getUri());
                        Glide.with(getApplicationContext()).load("https://www.hkparts.net/shop/pc/images/uploading.gif").into(imgVwDriverVerification);
                        try {
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_DRIVING_LICENSE, encodeTobase64(getBitmapFromUri(CropImage.getActivityResult(data).getUri())), CropImage.getActivityResult(data).getUri());
                            catTypeDriver.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(flag == 2)
                    {
                        flag_insurance_card = 1;
//                        ((ImageView) findViewById(R.id.imgVwDriverAutoInsurance)).setImageURI(CropImage.getActivityResult(data).getUri());
                        Glide.with(getApplicationContext()).load("https://www.hkparts.net/shop/pc/images/uploading.gif").into(imgVwDriverAutoInsurance);
                        try {
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_INSURANCE_CARD, encodeTobase64(getBitmapFromUri(CropImage.getActivityResult(data).getUri())),CropImage.getActivityResult(data).getUri());
                            catTypeDriver.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(flag == 3)
                    {
                        flag_home_insurance = 1;
//                        ((ImageView) findViewById(R.id.imgVwHomeInsurance)).setImageURI(CropImage.getActivityResult(data).getUri());
                        Glide.with(getApplicationContext()).load("https://www.hkparts.net/shop/pc/images/uploading.gif").into(imgVwHomeInsurance);
                        try {
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_HOME_INSURANCE, encodeTobase64(getBitmapFromUri(CropImage.getActivityResult(data).getUri())), CropImage.getActivityResult(data).getUri());
                            catTypeDriver.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(flag ==4)
                    {
                        flag_current_driver = 1;
//                        ((ImageView) findViewById(R.id.imgVwUberDriver)).setImageURI(CropImage.getActivityResult(data).getUri());
                        Glide.with(getApplicationContext()).load("https://www.hkparts.net/shop/pc/images/uploading.gif").into(imgVwUberDriver);
                        try {
                            DocumentUpload catTypeDriver = new DocumentUpload(WebFields.SIGN_UP_UPLOAD_FILE.PARAM_CURRENT_DRIVER, encodeTobase64(getBitmapFromUri(CropImage.getActivityResult(data).getUri())), CropImage.getActivityResult(data).getUri());
                            catTypeDriver.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


            }
        }


    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
        return bitmap;
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Applog.E(imageEncoded);
        return imageEncoded;
    }

    public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /*/ it's on the external media. /*/
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Uri getImageUri(Bitmap inImage) {
        String path = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

        return Uri.parse(path);
    }

    //Get Image using Camera
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


}
