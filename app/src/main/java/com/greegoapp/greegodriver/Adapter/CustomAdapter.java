package com.greegoapp.greegodriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greegoapp.greegodriver.R;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int flags[];
    String[] countryNames;
    String[] countryCodes;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, int[] flags,String[] countryCodes, String[] countryNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
        this.countryCodes = countryCodes;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public int getCount() {
        return flags.length;
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.row_country_selection, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imgVwFlag);
        TextView names = (TextView) view.findViewById(R.id.tvCountryName);
        TextView codes =(TextView)view.findViewById(R.id.tvCountryCode);
        icon.setImageResource(flags[i]);
        codes.setText(countryCodes[i]);
        names.setText(countryNames[i]);

        return view;
    }
}
