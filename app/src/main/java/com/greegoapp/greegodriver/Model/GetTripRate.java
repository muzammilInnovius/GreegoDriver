package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GetTripRate implements Parcelable {

    /**
     * data : {"id":1,"usa_state_id":26,"base_fee":10,"time_fee":0.45,"mile_fee":2,"cancel_fee":25,"overmile_fee":2.5,"is_active":1,"created_at":"2018-04-15 17:24:40","updated_at":null,"greego_fee":5,"express_fee":15.5}
     * {
     "data": {
     "id": 3,
     "usa_state_id": 9,
     "base_fee": 30,
     "time_fee": 0.4699999999999999733546474089962430298328399658203125,
     "mile_fee": 2,
     "cancel_fee": 27,
     "overmile_fee": 2.70000000000000017763568394002504646778106689453125,
     "is_active": 1,
     "created_at": "2018-04-15 17:24:42",
     "updated_at": null,
     "greego_fee": 5,
     "express_fee": 0.9899999999999999911182158029987476766109466552734375
     },
     "error_code": 0,
     "message": ""
     }
     *
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(error_code);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public static class DataBean {
        /**
         * "id": 3,
         "usa_state_id": 9,
         "base_fee": 30,
         "time_fee": 0.4699999999999999733546474089962430298328399658203125,
         "mile_fee": 2,
         "cancel_fee": 27,
         "overmile_fee": 2.70000000000000017763568394002504646778106689453125,
         "is_active": 1,
         "created_at": "2018-04-15 17:24:42",
         "updated_at": null,
         "greego_fee": 5,
         "express_fee": 0.9899999999999999911182158029987476766109466552734375
         */

        private double id;
        private double usa_state_id;
        private double base_fee;
        private double time_fee;
        private double mile_fee;
        private double cancel_fee;
        private double overmile_fee;
        private double is_active;
        private String created_at;
        private Object updated_at;
        private double greego_fee;
        private double express_fee;

        public double getId() {
            return id;
        }

        public void setId(double id) {
            this.id = id;
        }

        public double getUsa_state_id() {
            return usa_state_id;
        }

        public void setUsa_state_id(double usa_state_id) {
            this.usa_state_id = usa_state_id;
        }

        public double getBase_fee() {
            return base_fee;
        }

        public void setBase_fee(double base_fee) {
            this.base_fee = base_fee;
        }

        public double getTime_fee() {
            return time_fee;
        }

        public void setTime_fee(double time_fee) {
            this.time_fee = time_fee;
        }

        public double getMile_fee() {
            return mile_fee;
        }

        public void setMile_fee(double mile_fee) {
            this.mile_fee = mile_fee;
        }

        public double getCancel_fee() {
            return cancel_fee;
        }

        public void setCancel_fee(double cancel_fee) {
            this.cancel_fee = cancel_fee;
        }

        public double getOvermile_fee() {
            return overmile_fee;
        }

        public void setOvermile_fee(double overmile_fee) {
            this.overmile_fee = overmile_fee;
        }

        public double getIs_active() {
            return is_active;
        }

        public void setIs_active(double is_active) {
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

        public double getGreego_fee() {
            return greego_fee;
        }

        public void setGreego_fee(double greego_fee) {
            this.greego_fee = greego_fee;
        }

        public double getExpress_fee() {
            return express_fee;
        }

        public void setExpress_fee(double express_fee) {
            this.express_fee = express_fee;
        }
    }
}
