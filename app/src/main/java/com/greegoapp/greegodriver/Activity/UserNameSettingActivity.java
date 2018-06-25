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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
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
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import com.greegoapp.greegodriver.databinding.ActivityUserNameSettingBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.greegoapp.greegodriver.Activity.SettingActivity.REQUEST_USER_NAME;
import static com.greegoapp.greegodriver.Fragment.MapHomeFragment.REQUEST_ADD_PROFILE_INFO;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class UserNameSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static int MAX_IMAGE_DIMENSION = 60 * 60;
    ActivityUserNameSettingBinding binding;
    Context context;
    View snackBarView;
    Boolean flag;
    ImageButton ibBack;
    private static int SELECT_CAMERA_PIC = 99;
    private static int SELECT_GALLERY_PIC = 101;
    private Uri mImageUri;
    private Bitmap bitmap = null;
    private String strProPicBase64 = "";
    String filePath;
    ImageView imgVwProPic;
    TextInputEditText edtTvFirstName, edtTvLastName;
    Button btnUpdate;
    String strFname, strLname, strEmail, promoCode, is_agreed;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1100;
    String profilePic;
    private int PICK_IMAGE_REQUEST=1;
    private CropImageView mCropImageView;
    private Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_name_setting);
        context = UserNameSettingActivity.this;
        Bugsnag.init(context);
        snackBarView = findViewById(android.R.id.content);
        strFname = getIntent().getStringExtra("fname");
        strLname = getIntent().getStringExtra("lname");
        strEmail = getIntent().getStringExtra("email");
        promoCode = getIntent().getStringExtra("promo");
        is_agreed = getIntent().getStringExtra("is_agreed");

        flag = false;
        bindViews();
        setListner();
        edtTvFirstName.setText(strFname);
        edtTvLastName.setText(strLname);
        Glide.with(context).load(getIntent().getStringExtra("profile")).into(imgVwProPic);
    }

    private void setListner() {
        ibBack.setOnClickListener(this);
        imgVwProPic.setOnClickListener(this);
        edtTvFirstName.setOnClickListener(this);
        edtTvLastName.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        edtTvFirstName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        edtTvLastName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        mCropImageView.setOnClickListener(this);
    }

    private void bindViews() {
        ibBack = binding.ibBack;
        imgVwProPic = binding.imgVwProPic;
        edtTvFirstName = binding.edtTvProfileFname;
        edtTvLastName = binding.edtTvProfileLname;
        btnUpdate = binding.btnUpdate;
        mCropImageView = binding.iVwProPic1;
    }
    private void checkPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)

                {
                    ActivityCompat.requestPermissions(UserNameSettingActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CAMERA);


                } else {
                    // showPicProfileDialog();
                    flag = true;
                    mCropImageView.performClick();
                }
            } else {
                //  showPicProfileDialog();
                flag = true;
                mCropImageView.performClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri picUri = data.getData();

                filePath = getPath(picUri);

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);

                imgVwProPic.setImageURI(picUri);

            }
            if (requestCode == SELECT_GALLERY_PIC) {

//                Uri picUri = data.getData();
//                ivProPic.setImageURI(picUri);
//                filePath_driving_license = getPath(picUri);
                mImageUri = data.getData();
                try {
                    bitmap = getBitmapFromUri(mImageUri);
                    bitmap = scaleImage(this, mImageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }catch (Throwable throwable)
                {
                    Bugsnag.notify(throwable);
                }

                imgVwProPic.setImageURI(mImageUri);
                strProPicBase64 = encodeTobase64(bitmap);

                if (ConnectivityDetector.isConnectingToInternet(context)) {
                    imageUpload(strProPicBase64);

                } else {
                    SnackBar.showInternetError(context, snackBarView);
                }

//
            } else if (requestCode == SELECT_CAMERA_PIC) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                imgVwProPic.setImageBitmap(photo);

              /*  Uri tempUri = getImageUri(photo);
                File finalFile = new File(getRealPathFromURI(tempUri));

                imgVwProPic.setImageURI(getImageUri(imageOreintationValidator(photo, String.valueOf(finalFile))));*/

                strProPicBase64 = encodeTobase64(photo);

                if (ConnectivityDetector.isConnectingToInternet(context)) {
                    imageUpload(strProPicBase64);

                } else {
                    SnackBar.showInternetError(context, snackBarView);

                }

               /* Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivProPic.setImageURI(getImageUri(photo));
                strProPicBase64 = encodeTobase64(photo);

                if (ConnectivityDetector.isConnectingToInternet(context)) {
                    callUserProUpdate();

                } else {
                    SnackBar.showInternetError(context, snackBarView);
                }
*/
            }
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
//            mCropImageView.setImageUriAsync(imageUri);
//            mCropImageView.setImageBitmap();
                Log.d("sapan", "cropimage : " + (CropImage.getActivityResult(data)).getUri());
                mCropImageView.setVisibility(View.GONE);

                ((ImageView) findViewById(R.id.imgVwProPic)).setImageURI(CropImage.getActivityResult(data).getUri());
               // ((ImageView) findViewById(R.id.imgVwProPic)).setVisibility(View.VISIBLE);
                try {
                    imageUpload(encodeTobase64(getBitmapFromUri(CropImage.getActivityResult(data).getUri())));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            Log.d("sapan", "onactivityresult" + requestCode);
        }
    }

    //Get image using Gallary
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
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
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

    private String getPath(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        Log.e("result", result);
        return result;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpdate:
                KeyboardUtility.hideKeyboard(context, view);
                if (isValid()) {
                    strLname = edtTvLastName.getText().toString();
                    strFname = edtTvFirstName.getText().toString();

                    Intent intent = new Intent(this, SettingActivity.class);
                    intent.putExtra("name", strFname);
                    intent.putExtra("profile", profilePic);
                    setResult(REQUEST_USER_NAME, intent);

                    callAcceptAgreementAPI();
                    SnackBar.showSuccess(context, snackBarView, "Profile updated successfully.");
                }
                break;
            case R.id.ibBack:
                KeyboardUtility.hideKeyboard(context, view);

//                Intent back = new Intent(context, SettingActivity.class);
//                startActivity(back);
                finish();
                break;
            case R.id.imgVwProPic:
               /* if(myrequestPermission()) {
                    flag = true;
                    mCropImageView.performClick();
                }*//*if (requestPermission()) {
                    *//*  ImageCapturing();*//*
                    checkPermissions();
                    *//*imageBrowse();*//*
                }*/
                checkPermission();
                break;
            case R.id.iVwProPic1:
                 CropImage.startPickImageActivity(this);
                break;
        }
    }
    public boolean myrequestPermission() {

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

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }
    private void checkPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)

                {
                    ActivityCompat.requestPermissions(UserNameSettingActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CAMERA);


                } else {
                    showPicProfileDialog();
                }
            } else {
                showPicProfileDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {                    // check whether storage permission granted or not.
                    //do what you want;
                 //   showPicProfileDialog();
                    flag = true;
                    mCropImageView.performClick();
                }
                break;
            case CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE:
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);

                } else {
                //    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void showPicProfileDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Greego App").setMessage("Choose profile pic");


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

    private void inserttodatabase() {
        if (ConnectivityDetector
                .isConnectingToInternet(context)) {

            try {
                imageUpload(strProPicBase64);

            } catch (Exception e) {
            } catch (Throwable throwable) {
                Bugsnag.notify(throwable);
            }
            /*  callDriverProfileInfoAPI();*/
        } else {
            SnackBar.showInternetError(context, snackBarView);
        }

    }

    private void imageUpload(String filePath) {

        Log.e("aj", "@@" + filePath);


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", filePath);
            MyProgressDialog.showProgressDialog(context);

            Applog.E("request driver personal info=> " + jsonObject.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_PIC.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    profilePic = response.optJSONObject("data").optString("profile_pic");
                    MyProgressDialog.hideProgressDialog();
                    // finish();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    MyProgressDialog.hideProgressDialog();
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }
    }

    private boolean isValid() {
        strFname = edtTvFirstName.getText().toString();
        strLname = edtTvLastName.getText().toString();
        if (strFname.isEmpty()) {
            edtTvFirstName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_first_name));
            return false;
        } else if (strLname.isEmpty()) {
            edtTvLastName.requestFocus();
            SnackBar.showValidationError(context, snackBarView, getString(R.string.enter_last_name));
            return false;
        }
        return true;
    }

    private void callAcceptAgreementAPI() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_FIRST_NAME, strFname);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_LAST_NAME, strLname);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_EMAIL, strEmail);
         /*   jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_CITY, "");
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PRO_PIC, "");*/
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_PROMO_CODE, promoCode);
            jsonObject.put(WebFields.SIGN_UP_PROFILE_UPDATE.PARAM_IS_AGGREED, is_agreed);


            Applog.E("request DigitCode=> " + jsonObject.toString());
            MyProgressDialog.showProgressDialog(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    WebFields.BASE_URL + WebFields.SIGN_UP_PROFILE_UPDATE.MODE, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Applog.E("success: " + response.toString());
                    MyProgressDialog.hideProgressDialog();
                    if (flag == true) {
                        inserttodatabase();
                    } else {
                        finish();
                        MyProgressDialog.hideProgressDialog();
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
        } catch (Throwable throwable) {
            Bugsnag.notify(throwable);
        }

    }
}
