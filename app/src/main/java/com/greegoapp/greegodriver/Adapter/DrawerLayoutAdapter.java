package com.greegoapp.greegodriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greegoapp.greegodriver.R;

public class DrawerLayoutAdapter extends BaseAdapter {
    private Context mContext;
    private String[] menuTitle;
    private int[] menuImage;
    public static int notiSize = 0;

    public DrawerLayoutAdapter(Context context, String[] menus, int[] drawerImage) {
        mContext = context;
        this.menuTitle = menus;
        this.menuImage = drawerImage;
    }



    @Override
    public int getCount() {
        return menuTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private int index;

    public int getSelectedIndex() {
        return index;
    }

    public void setSelectedIndex(int index) {
        this.index = index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View drawerView = convertView;
        try {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            try {
                drawerView = inflater.inflate(R.layout.listview_with_text_image, parent, false);

                TextView txtMenuTitle = (TextView) drawerView.findViewById(R.id.listtextView);
                ImageView imgVwLog = (ImageView) drawerView.findViewById(R.id.imgVwLog);

                txtMenuTitle.setText(menuTitle[position]);
                imgVwLog.setImageResource(menuImage[position]);

//                notiSize = notificationsize;

                if (index == position) {
                    txtMenuTitle.setTextColor(mContext.getResources().getColor(R.color.app_bg));
                } else {
                    txtMenuTitle.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawerView;
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return position;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }
}