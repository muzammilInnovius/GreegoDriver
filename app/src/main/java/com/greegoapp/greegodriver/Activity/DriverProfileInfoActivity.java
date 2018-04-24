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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.KeyboardUtility;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.RoundedImageView;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.Utils.WebFields;
import com.greegoapp.greegodriver.databinding.ActivityDriverProfileInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.greegoapp.greegodriver.Activity.DriverAttachFileInfoActivity.PICK_IMAGE_REQUEST;
import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_PROFILE_INFO;

public class DriverProfileInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverProfileInfoBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibCancel;
    Button btnNext;
    private static int SELECT_CAMERA_PIC = 99;
    private static int SELECT_GALLERY_PIC = 101;
    ImageView imgVwProfile;
    private Uri mImageUri;
    private Bitmap bitmap = null;
    private String strProPicBase64 = "";
    int profileStatus;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_profile_info);
        context = DriverProfileInfoActivity.this;
        snackBarView = findViewById(android.R.id.content);
        bindView();
        setListners();
    }

    private void setListners() {
        ibCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        imgVwProfile.setOnClickListener(this);
    }

    private void bindView() {
        ibCancel = binding.ibCancel;
        btnNext = binding.btnNext;
        imgVwProfile = binding.imgVwProfile;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibCancel:
            /*    Intent i= new Intent();
                i.putExtra("profileStatus",profileStatus);
                setResult(REQUEST_ADD_PROFILE_INFO,i);*/
                Intent back = new Intent(this, HomeActivity.class);
                back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(back);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
//                finish();
                break;
            case R.id.btnNext:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    if (ConnectivityDetector
                            .isConnectingToInternet(context)) {
                  /*      callDriverProfileInfoAPI();*/
                        imageUpload(filePath);
                      /*  callDriverProfileInfoAPI();*/
                    } else {
                        SnackBar.showInternetError(context, snackBarView);
                    }
                }
                break;
            case R.id.imgVwProfile:
                if (requestPermission()) {
                  /*  ImageCapturing();*/
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
    private void imageUpload(String filePath) {

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, WebFields.BASE_URL+WebFields.SIGN_UP_PROFILE_PIC.MODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            //String message = jObj.getString("message");

                            ProfileStatus userDetails = new Gson().fromJson(String.valueOf(response), ProfileStatus.class);

                                if (userDetails.getError_code() == 0) {

                                    Applog.E("UserDetails" + userDetails);
//                            callUserMeApi();

                                    SessionManager.setIsUserLoggedin(context, true);
//                            int profileStatus = userDetails.getData().getProfile_status();
//
                                     profileStatus = userDetails.getData().getProfile_status();
//                            //getIs_agreed = 0 new user
                                    setProfileScreen(profileStatus);
                                  /*  if(REQUEST_ADD_PROFILE_INFO==1006)
                                    {
                                        Intent i= new Intent();
                                        i.putExtra("profileStatus",profileStatus);
                                        setResult(REQUEST_ADD_PROFILE_INFO,i);
                                    }*/
////
//                            Intent in = new Intent(context, DriverPersonalInfoActivity.class);
//                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(in);
//                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
                                } else {
                                    MyProgressDialog.hideProgressDialog();
                                    SnackBar.showError(context, snackBarView, getResources().getString(R.string.something_went_wrong));
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

        smr.addFile("image",filePath);
        AppController.getInstance().addToRequestQueue(smr);

    }


    private void callDriverProfileInfoAPI() {
//        Intent in = new Intent(context, HomeActivity.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(in);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PRO_PIC, strProPicBase64);

            Applog.E("request driver Image info=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_UPDATE.MODE, jsonObject, new Response.Listener<JSONObject>() {

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
                           /* setProfileScreen(profileStatus);*/
////
                            if(REQUEST_ADD_PROFILE_INFO==105)
                            {
                                Intent i= new Intent();
                                i.putExtra("profileStatus",profileStatus);
                                setResult(REQUEST_ADD_PROFILE_INFO,i);
                            }
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

    private void ImageCapturing() {

        final String[] DialogOption = {"Camera", "Gallary", "Cancel"};

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Select Option");
        alert.setCancelable(false);
        alert.setItems(DialogOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (DialogOption[i].equals("Camera")) {
                    openCamera();
                } else if (DialogOption[i].equals("Gallary")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_GALLERY_PIC);
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

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

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, SELECT_CAMERA_PIC);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == RESULT_OK) {
            // user chose an image from the gallery
            // loadAsync(data.getData());
            if (requestCode == SELECT_GALLERY_PIC) {
                mImageUri = data.getData();
                try {
                    bitmap = getBitmapFromUri(mImageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                filePath = mImageUri.getPath();
                Toast.makeText(getApplicationContext(),filePath,Toast.LENGTH_LONG).show();
                imgVwProfile.setImageURI(mImageUri);
                strProPicBase64 = encodeTobase64(bitmap);

            } else if (requestCode == SELECT_CAMERA_PIC) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgVwProfile.setImageURI(getImageUri(photo));
                strProPicBase64 = encodeTobase64(photo);
            }
        }*/
        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

                filePath = getPath(picUri);

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);

                imgVwProfile.setImageURI(picUri);

            }

        }
    }
    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        Log.e("result",result);
        return result;
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
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


    private boolean isValid() {
        return true;
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
