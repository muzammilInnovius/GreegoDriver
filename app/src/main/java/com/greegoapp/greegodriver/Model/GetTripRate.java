package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GetTripRate implements Parcelable {

    /**
     * data : {"id":1,"usa_state_id":"26","base_fee":"10","time_fee":"0.45","mile_fee":"2","cancel_fee":"25","overmile_fee":"2.5","is_active":"1","created_at":"2018-04-15 11:54:40","updated_at":null}
     * error_code : 0
     * message :
     */

    private DataBean data;
    private int error_code;
    private String message;

    protected GetTripRate(Parcel in) {
        error_code = in.readInt();
        message = in.readString();
    }

    public static final Creator<GetTripRate> CREATOR = new Creator<GetTripRate>() {
        @Override
        public GetTripRate createFromParcel(Parcel in) {
            return new GetTripRate(in);
        }

        @Override
        public GetTripRate[] newArray(int size) {
            return new GetTripRate[size];
        }
    };

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(error_code);
        parcel.writeString(message);
    }

    public static class DataBean {
        /**
         * id : 1
         * usa_state_id : 26
         * base_fee : 10
         * time_fee : 0.45
         * mile_fee : 2
         * cancel_fee : 25
         * overmile_fee : 2.5
         * is_active : 1
         * created_at : 2018-04-15 11:54:40
         * updated_at : null
         */

        private int id;
        private String usa_state_id;
        private String base_fee;
        private String time_fee;
        private String mile_fee;
        private String cancel_fee;
        private String overmile_fee;
        private String is_active;
        private String created_at;
        private Object updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsa_state_id() {
            return usa_state_id;
        }

        public void setUsa_state_id(String usa_state_id) {
            this.usa_state_id = usa_state_id;
        }

        public String getBase_fee() {
            return base_fee;
        }

        public void setBase_fee(String base_fee) {
            this.base_fee = base_fee;
        }

        public String getTime_fee() {
            return time_fee;
        }

        public void setTime_fee(String time_fee) {
            this.time_fee = time_fee;
        }

        public String getMile_fee() {
            return mile_fee;
        }

        public void setMile_fee(String mile_fee) {
            this.mile_fee = mile_fee;
        }

        public String getCancel_fee() {
            return cancel_fee;
        }

        public void setCancel_fee(String cancel_fee) {
            this.cancel_fee = cancel_fee;
        }

        public String getOvermile_fee() {
            return overmile_fee;
        }

        public void setOvermile_fee(String overmile_fee) {
            this.overmile_fee = overmile_fee;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public Object getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Object updated_at) {
            this.updated_at = updated_at;
        }
    }
}
