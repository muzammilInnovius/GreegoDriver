package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TripHistoryDetailsModel implements Parcelable {

    /**
     * data : [{"id":169,"from_address":"test","from_lat":23.0101,"from_lng":72.5626,"to_address":"to test","to_lat":23.0035,"to_lng":72.5616,"total_estimated_travel_time":0,"total_estimated_trip_cost":0,"created_at":"2018-05-08 10:30:29","updated_at":"2018-05-08 12:02:07","payout_status":2},{"id":187,"from_address":"5, Mahalaxmi Five Rd, Chetan Society, Paldi, Ahmedabad, Gujarat 380007, India","from_lat":23.01,"from_lng":72.5626,"to_address":"8, Pankaj Society, SBK Society, Paldi, Ahmedabad, Gujarat 380007, India","to_lat":23.0031,"to_lng":72.5589,"total_estimated_travel_time":12,"total_estimated_trip_cost":13.42,"created_at":"2018-05-08 11:45:55","updated_at":"2018-05-08 11:46:16","payout_status":0},{"id":197,"from_address":"12, Paldi, Ahmedabad, Gujarat 380007, India","from_lat":23.0146,"from_lng":72.5605,"to_address":"Shivalik 5, Paldi, Ahmedabad, Gujarat 380007, India","to_lat":23.0108,"to_lng":72.5624,"total_estimated_travel_time":0,"total_estimated_trip_cost":10.2265,"created_at":"2018-05-08 13:28:00","updated_at":"2018-05-08 13:28:08","payout_status":2}]
     * error_code : 0
     * message : The trip details
     */

    private int error_code;
    private String message;
    private List<DataBean> data;

    protected TripHistoryDetailsModel(Parcel in) {
        error_code = in.readInt();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(error_code);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripHistoryDetailsModel> CREATOR = new Creator<TripHistoryDetailsModel>() {
        @Override
        public TripHistoryDetailsModel createFromParcel(Parcel in) {
            return new TripHistoryDetailsModel(in);
        }

        @Override
        public TripHistoryDetailsModel[] newArray(int size) {
            return new TripHistoryDetailsModel[size];
        }
    };

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 169
         * from_address : test
         * from_lat : 23.0101
         * from_lng : 72.5626
         * to_address : to test
         * to_lat : 23.0035
         * to_lng : 72.5616
         * total_estimated_travel_time : 0
         * total_estimated_trip_cost : 0
         * created_at : 2018-05-08 10:30:29
         * updated_at : 2018-05-08 12:02:07
         * payout_status : 2
         */

        private int id;
        private String from_address;
        private double from_lat;
        private double from_lng;
        private String to_address;
        private double to_lat;
        private double to_lng;
        private String total_estimated_travel_time;
        private double total_estimated_trip_cost;
        private String created_at;
        private String updated_at;
        private int payout_status;
        private String transaction_id;
        private String start_time;
        private String end_time;
        private int transaction_status;
        private double actual_trip_amount;
        private double tip_amount;

        public double getActual_trip_amount() {
            return actual_trip_amount;
        }

        public void setActual_trip_amount(double actual_trip_amount) {
            this.actual_trip_amount = actual_trip_amount;
        }

        public double getTip_amount() {
            return tip_amount;
        }

        public void setTip_amount(double tip_amount) {
            this.tip_amount = tip_amount;
        }

        public int getTransaction_status() {
            return transaction_status;
        }

        public void setTransaction_status(int transaction_status) {
            this.transaction_status = transaction_status;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public double getActual_trip_miles() {
            return actual_trip_miles;
        }

        public void setActual_trip_miles(double actual_trip_miles) {
            this.actual_trip_miles = actual_trip_miles;
        }

        private double actual_trip_miles;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFrom_address() {
            return from_address;
        }

        public void setFrom_address(String from_address) {
            this.from_address = from_address;
        }

        public double getFrom_lat() {
            return from_lat;
        }

        public void setFrom_lat(double from_lat) {
            this.from_lat = from_lat;
        }

        public double getFrom_lng() {
            return from_lng;
        }

        public void setFrom_lng(double from_lng) {
            this.from_lng = from_lng;
        }

        public String getTo_address() {
            return to_address;
        }

        public void setTo_address(String to_address) {
            this.to_address = to_address;
        }

        public double getTo_lat() {
            return to_lat;
        }

        public void setTo_lat(double to_lat) {
            this.to_lat = to_lat;
        }

        public double getTo_lng() {
            return to_lng;
        }

        public void setTo_lng(double to_lng) {
            this.to_lng = to_lng;
        }

        public String getTotal_estimated_travel_time() {
            return total_estimated_travel_time;
        }

        public void setTotal_estimated_travel_time(String total_estimated_travel_time) {
            this.total_estimated_travel_time = total_estimated_travel_time;
        }

        public double getTotal_estimated_trip_cost() {
            return total_estimated_trip_cost;
        }

        public void setTotal_estimated_trip_cost(double total_estimated_trip_cost) {
            this.total_estimated_trip_cost = total_estimated_trip_cost;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public int getPayout_status() {
            return payout_status;
        }

        public void setPayout_status(int payout_status) {
            this.payout_status = payout_status;
        }
    }
}