package com.greegoapp.greegodriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.greegoapp.greegodriver.Interface.RecyclerViewItemClickListener;
import com.greegoapp.greegodriver.Model.PayoutData;
import com.greegoapp.greegodriver.Model.TripHistoryDetailsModel;
import com.greegoapp.greegodriver.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PayoutHistoryAdapter extends RecyclerView.Adapter<PayoutHistoryAdapter.ViewHolder>{
    private Context context;
    private ArrayList<PayoutData.DataBean> list;
    private boolean loading;
    RecyclerViewItemClickListener mListener;
    String payoutDateTime,payoutPrice;
    int paid_type;
    public  String formatted_date;

    public PayoutHistoryAdapter(Context context, ArrayList<PayoutData.DataBean> list, RecyclerViewItemClickListener mListener) {
        this.context = context;
        this.list = list;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payout, parent, false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            if(list!=null) {
                payoutPrice = String.valueOf(list.get(position).getActual_trip_amount());
                paid_type = list.get(position).getPaid_type();
                payoutDateTime = String.valueOf(list.get(position).getCreated_at());
                formatted_date = parseDateToddMMyyyy(payoutDateTime);
                holder.tvDateTime.setText(formatted_date);
                //holder.tvAmount.setText(new DecimalFormat("##.##").format(Double.valueOf(payoutPrice)));
                holder.tvAmount.setText(String.format("%.2f",Double.valueOf(payoutPrice)));
                if(paid_type==1)
                {
                   holder.tvExpressPay.setVisibility(View.VISIBLE);
                   holder.imgVwEPay.setVisibility(View.VISIBLE);
                }else
                {
                    holder.tvPaidType.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String parseDateToddMMyyyy(String payoutDateTime) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(payoutDateTime);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public int getItemCount() {
//        if (list.size() > 0 || list != null) {
            return list.size();
      /*  } else {
            return 0;
        }*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerViewItemClickListener mListener;
        TextView tvDateTime,tvAmount,tvPaidType,tvExpressPay;
        ImageView imgVwEPay;
        public ViewHolder(View itemView,RecyclerViewItemClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_payout_ride_date_time);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_payout_ride_money);
            tvPaidType = (TextView) itemView.findViewById(R.id.tv_payout_ride_type);
            tvExpressPay = (TextView)itemView.findViewById(R.id.tv_express_pay);
            imgVwEPay = (ImageView)itemView.findViewById(R.id.imgVwEpay);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
