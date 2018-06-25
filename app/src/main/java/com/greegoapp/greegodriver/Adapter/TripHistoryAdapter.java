package com.greegoapp.greegodriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bugsnag.android.Bugsnag;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Interface.RecyclerViewItemClickListener;
import com.greegoapp.greegodriver.Model.GetTripRate;
import com.greegoapp.greegodriver.Model.TripHistoryDetailsModel;
import com.greegoapp.greegodriver.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TripHistoryDetailsModel.DataBean> list;
    private boolean loading;
    RecyclerViewItemClickListener mListener;
    public String strDistance;
    public String formatted_date;
    private double trip_amount,tip_amount;
    private double greego_fee_per;
    private double total;

    public TripHistoryAdapter(Context context, ArrayList<TripHistoryDetailsModel.DataBean> list, RecyclerViewItemClickListener mListener) {
        this.context = context;
        this.list = list;
        this.mListener = mListener;
        //Collections.reverse(list);
    }

    //priyanka
    public void setLoaded() {
        loading = false;
    }

    public TripHistoryAdapter(RecyclerViewItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_single_ride_histroy, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int time;
        Double amount;
        Double lat1, lon1, lat2, lon2, distance = null;
        try {
            if (list != null) {
                amount = list.get(position).getTotal_estimated_trip_cost();
                //time = (int) list.get(position).getTotal_estimated_travel_time();
                String date = list.get(position).getCreated_at();
                formatted_date = parseDateToddMMyyyy(date);
                lat1 = list.get(position).getFrom_lat();
                lat2 = list.get(position).getTo_lat();
                lon1 = list.get(position).getFrom_lng();
                lon2 = list.get(position).getTo_lng();
                trip_amount = list.get(position).getActual_trip_amount();
                tip_amount = list.get(position).getTip_amount();
                distance = distance(lat1, lon1, lat2, lon2);
                total = getGreegoFee();
              //  strDistance = new DecimalFormat("##.##").format(list.get(position).getActual_trip_miles()) + "mi ";
                //strDistance = new DecimalFormat("##.##").format(list.get(position).getActual_trip_miles()) + "mi " + " " +  list.get(position).getTotal_estimated_travel_time();
                strDistance = String.format("%.2f",list.get(position).getActual_trip_miles()) + "mi " + " " +  list.get(position).getTotal_estimated_travel_time();

                //holder.tv_ride_money.setText(new DecimalFormat("##.##").format(amount));

             //   holder.tv_ride_money.setText(new DecimalFormat("##.##").format(total));
                holder.tv_ride_money.setText(String.format("%.2f",total));
                holder.tv_ride_date_time.setText(formatted_date);
              //  holder.tv_ride_mi_time.setText(new DecimalFormat("##.##").format(list.get(position).getActual_trip_miles()) + "mi ");
              //  holder.tv_ride_mi_time.setText(new DecimalFormat("##.##").format(list.get(position).getActual_trip_miles()) + "mi " + " " + list.get(position).getTotal_estimated_travel_time());
                holder.tv_ride_mi_time.setText(String.format("%.2f",list.get(position).getActual_trip_miles()) + "mi " + " " + list.get(position).getTotal_estimated_travel_time());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private double getGreegoFee() {
        GetTripRate tripRateData = new Gson().fromJson(context.getSharedPreferences("driverData", 0).getString("fee", ""), GetTripRate.class);
        try {
            if (tripRateData.getError_code() == 0) {
                greego_fee_per = Double.valueOf(tripRateData.getData().getGreego_fee());
                double total_greego_fee = (trip_amount * greego_fee_per) / 100;
                total = trip_amount - total_greego_fee + tip_amount;
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
        return total;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_ride_date_time, tv_ride_mi_time, tv_ride_money;
        RecyclerViewItemClickListener mListener;

        public ViewHolder(View itemView, RecyclerViewItemClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
            tv_ride_date_time = (TextView) itemView.findViewById(R.id.tv_ride_date_time);
            tv_ride_mi_time = (TextView) itemView.findViewById(R.id.tv_ride_mi_time);
            tv_ride_money = (TextView) itemView.findViewById(R.id.tv_ride_money);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
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
