package com.greegoapp.greegodriver.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.Activity.HomeActivity;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.FragmentUserNameSettingBinding;

import static android.app.Activity.RESULT_OK;
import static com.greegoapp.greegodriver.Activity.DriverProfileInfoActivity.encodeTobase64;


public class UserNameSettingFragment extends Fragment implements View.OnClickListener {
    FragmentUserNameSettingBinding binding;
    View snackBarView;
    Context context;
    ImageButton ibback;
    private Uri mImageUri;

    ImageView imgVwProPic;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private static int SELECT_CAMERA_PIC = 99;
    private static int SELECT_GALLERY_PIC = 101;

    private OnFragmentInteractionListener mListener;

    public UserNameSettingFragment() {
    }

    public static UserNameSettingFragment newInstance(String param1, String param2) {
        UserNameSettingFragment fragment = new UserNameSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_name_setting, container, false);

        View view = binding.getRoot();
        snackBarView = getActivity().findViewById(android.R.id.content);
        context = getActivity();
        Bugsnag.init(context);
        bindViews();

        setListner();


        return view;
    }

    private void setListner() {
        ibback.setOnClickListener(this);
        imgVwProPic.setOnClickListener(this);
    }

    private void bindViews() {
        ibback=binding.ibBack;
        imgVwProPic=binding.imgVwProPic;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ibBack:
//                Fragment fragment = new SettingDetailFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.containerSetting, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
                break;
            case R.id.imgVwProPic:
                if (requestPermission()) {
                    ImageCapturing();
                }
                break;
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

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, SELECT_CAMERA_PIC);
    }

    private boolean requestPermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result != PackageManager.PERMISSION_GRANTED || result1 != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_CAMERA_PIC);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_CAMERA_PIC);
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
            // user chose an image from the gallery
            // loadAsync(data.getData());
            if (requestCode == SELECT_GALLERY_PIC) {
                mImageUri = data.getData();
         /*       try {
                    bitmap = getBitmapFromUri(mImageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
                imgVwProPic.setImageURI(mImageUri);
      /*          strProPicBase64 = encodeTobase64(bitmap);*/

            } else if (requestCode == SELECT_CAMERA_PIC) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
           /*     imgVwProfile.setImageURI(getImageUri(photo));
                strProPicBase64 = encodeTobase64(photo);*/
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
