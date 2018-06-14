package com.greegoapp.greegodriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greegoapp.greegodriver.Model.TripHistoryDetailsModel;
import com.greegoapp.greegodriver.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TripHistoryDetailsModel.DataBean> list;
    private boolean loading;

    public TripHistoryAdapter(Context context, ArrayList<TripHistoryDetailsModel.DataBean> list) {
        this.context = context;
        this.list = list;
    }
//priyanka
    public void setLoaded() {
        loading = false;
    }

    @Override
    public TripHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_single_ride_histroy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripHistoryAdapter.ViewHolder holder, int position) {
        int time;
        Double amount;
        Double lat1, lon1, lat2, lon2, distance = null;

        amount = (Double) list.get(position).getRequest().getTotal_estimated_trip_cost();
        time = (int) list.get(position).getRequest().getTotal_estimated_travel_time();
        String date=list.get(position).getCreated_at();
        String formatted_date=parseDateToddMMyyyy(date);
        lat1 = list.get(position).getRequest().getFrom_lat();
        lat2 = list.get(position).getRequest().getTo_lat();
        lon1 = list.get(position).getRequest().getFrom_lng();
        lon2 = list.get(position).getRequest().getTo_lng();
        distance = distance(lat1, lon1, lat2, lon2);
        holder.tv_ride_money.setText(new DecimalFormat("##.##").format(amount));
        holder.tv_ride_date_time.setText(formatted_date);
        holder.tv_ride_mi_time.setText(new DecimalFormat("##.##").format(distance)+"mi " + " " + String.valueOf(time)+"m");

    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0 || list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ride_date_time, tv_ride_mi_time, tv_ride_money;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_ride_date_time = (TextView) itemView.findViewById(R.id.tv_ride_date_time);
            tv_ride_mi_time = (TextView) itemView.findViewById(R.id.tv_ride_mi_time);
            tv_ride_money = (TextView) itemView.findViewById(R.id.tv_ride_money);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
