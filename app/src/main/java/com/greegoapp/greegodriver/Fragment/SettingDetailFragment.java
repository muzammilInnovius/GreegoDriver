package com.greegoapp.greegodriver.Fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bugsnag.android.Bugsnag;
import com.greegoapp.greegodriver.Activity.HomeActivity;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.databinding.FragmentSettingDetailBinding;


public class SettingDetailFragment extends Fragment implements View.OnClickListener {

    FragmentSettingDetailBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    TextView tvTitle;
    View snackBarView;
    Context context;
    ImageButton ibback;

    RelativeLayout rlUserName,rlUserEmail,rlUserPhone,rlPayment,rlNavigate;

    public SettingDetailFragment() {
    }

     public static SettingDetailFragment newInstance(String param1, String param2) {
        SettingDetailFragment fragment = new SettingDetailFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_detail, container, false);

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
        rlUserName.setOnClickListener(this);
        rlUserEmail.setOnClickListener(this);
        rlUserPhone.setOnClickListener(this);
        rlPayment.setOnClickListener(this);
        rlNavigate.setOnClickListener(this);
    }

    private void bindViews() {
        ibback=binding.ibBack;
        rlUserName=binding.rlName;
        rlUserEmail=binding.rlEmail;
        rlUserPhone=binding.rlPhone;
        rlPayment=binding.rlPayment;
        rlNavigate=binding.rlNavigate;

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
            case R.id.rlName:
//                Fragment fragment = new UserNameSettingFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.containerSetting, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
                break;
            case R.id.rlEmail:
//                Fragment fragmentUserEmail = new UserEmailSettingFragment();
//                FragmentManager FMUserEmail = getActivity().getSupportFragmentManager();
//                FragmentTransaction FTUserEmail = FMUserEmail.beginTransaction();
//                FTUserEmail.replace(R.id.containerSetting, fragmentUserEmail);
//                FTUserEmail.addToBackStack(null);
//                FTUserEmail.commit();
                break;
            case R.id.rlPhone:
                break;
            case R.id.rlPayment:
                break;
            case R.id.rlNavigate:
                break;
            case R.id.ibBack:
                Intent i=new Intent(context, HomeActivity.class);
                startActivity(i);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
