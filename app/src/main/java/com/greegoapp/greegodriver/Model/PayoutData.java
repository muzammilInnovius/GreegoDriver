package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PayoutData implements Parcelable{
    /**
     * data : [{"id":37,"created_at":"2018-05-31 07:09:47","actual_trip_amount":28.6578,"tip_amount":4.29867,"paid_type":1,"greego_fee":5,"express_fee":0.99},{"id":38,"created_at":"2018-05-31 07:14:17","actual_trip_amount":33.2635,"tip_amount":4.98952,"paid_type":1,"greego_fee":5,"express_fee":0.99},{"id":45,"created_at":"2018-05-31 07:28:51","actual_trip_amount":31.8113,"tip_amount":0,"paid_type":1,"greego_fee":5,"express_fee":0.99},{"id":51,"created_at":"2018-05-31 07:37:01","actual_trip_amount":30.2248,"tip_amount":4.53372,"paid_type":1,"greego_fee":5,"express_fee":0.99},{"id":52,"created_at":"2018-05-31 07:41:12","actual_trip_amount":30.7813,"tip_amount":4.61719,"paid_type":1,"greego_fee":5,"express_fee":0.99},{"id":62,"created_at":"2018-05-31 08:03:10","actual_trip_amount":44.9231,"tip_amount":0,"paid_type":1,"greego_fee":5,"express_fee":0.99},{"id":65,"created_at":"2018-05-31 08:05:44","actual_trip_amount":39.3496,"tip_amount":0,"paid_type":1,"greego_fee":5,"express_fee":0.99}]
     * error_code : 0
     * message : Payout details
     */

    private int error_code;
    private String message;
    private List<DataBean> data;

    protected PayoutData(Parcel in) {
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

    public static final Creator<PayoutData> CREATOR = new Creator<PayoutData>() {
        @Override
        public PayoutData createFromParcel(Parcel in) {
            return new PayoutData(in);
        }

        @Override
        public PayoutData[] newArray(int size) {
            return new PayoutData[size];
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
         * id : 37
         * created_at : 2018-05-31 07:09:47
         * actual_trip_amount : 28.6578
         * tip_amount : 4.29867
         * paid_type : 1
         * greego_fee : 5
         * express_fee : 0.99
         */

        private int id;
        private String created_at;
        private double actual_trip_amount;
        private double tip_amount;
        private int paid_type;
        private int greego_fee;
        private double express_fee;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

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

        public int getPaid_type() {
            return paid_type;
        }

        public void setPaid_type(int paid_type) {
            this.paid_type = paid_type;
        }

        public int getGreego_fee() {
            return greego_fee;
        }

        public void setGreego_fee(int greego_fee) {
            this.greego_fee = greego_fee;
        }

        public double getExpress_fee() {
            return express_fee;
        }

        public void setExpress_fee(double express_fee) {
            this.express_fee = express_fee;
        }
    }

    /**
     * data : [{"id":16,"created_at":"2018-05-12 05:48:22","actual_trip_amount":10.6602,"tip_amount":3,"paid_type":0},{"id":23,"created_at":"2018-05-12 12:54:22","actual_trip_amount":100,"tip_amount":1,"paid_type":0}]
     * error_code : 0
     * message : Payout details
     *//*

    private int error_code;
    private String message;
    private List<DataBean> data;

    protected PayoutData(Parcel in) {
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

    public static final Creator<PayoutData> CREATOR = new Creator<PayoutData>() {
        @Override
        public PayoutData createFromParcel(Parcel in) {
            return new PayoutData(in);
        }

        @Override
        public PayoutData[] newArray(int size) {
            return new PayoutData[size];
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
        *//**
         * id : 16
         * created_at : 2018-05-12 05:48:22
         * actual_trip_amount : 10.6602
         * tip_amount : 3
         * paid_type : 0
         *//*

        private int id;
        private String created_at;
        private double actual_trip_amount;
        private int tip_amount;
        private int paid_type;
        private double greego_fee;

        public double getGreego_fee() {
            return greego_fee;
        }

        public void setGreego_fee(double greego_fee) {
            this.greego_fee = greego_fee;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public double getActual_trip_amount() {
            return actual_trip_amount;
        }

        public void setActual_trip_amount(double actual_trip_amount) {
            this.actual_trip_amount = actual_trip_amount;
        }

        public int getTip_amount() {
            return tip_amount;
        }

        public void setTip_amount(int tip_amount) {
            this.tip_amount = tip_amount;
        }

        public int getPaid_type() {
            return paid_type;
        }

        public void setPaid_type(int paid_type) {
            this.paid_type = paid_type;
        }
    }*/
}
