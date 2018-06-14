package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TripHistoryDetailsModel implements Parcelable {

    /**
     * data : [{"id":6,"request_id":15,"user_id":1,"driver_id":3,"status":4,"actual_trip_amount":0,"actual_trip_miles":0,"tip_amount":0,"actual_trip_travel_time":0,"trip_driver_rating":0,"trip_user_rating":0,"created_at":"2018-05-05 09:45:14","updated_at":"2018-05-05 09:45:55","request":{"id":15,"user_id":1,"user_vehicle_id":2,"from_address":"5, Mahalaxmi Five Rd, Chetan Society, Paldi, Ahmedabad, Gujarat 380007, India","from_lat":23.01,"from_lng":72.5626,"to_address":"2, Fatehpura, Shantivan, Motinagar, Paldi, Ahmedabad, Gujarat 380007, India","to_lat":23.0044,"to_lng":72.5602,"total_estimated_travel_time":12,"total_estimated_trip_cost":12.69,"request_status":1,"created_at":"2018-05-05 09:45:08","updated_at":"2018-05-05 09:45:14"}},{"id":7,"request_id":17,"user_id":2,"driver_id":3,"status":4,"actual_trip_amount":0,"actual_trip_miles":0,"tip_amount":0,"actual_trip_travel_time":0,"trip_driver_rating":0,"trip_user_rating":0,"created_at":"2018-05-05 09:48:22","updated_at":"2018-05-05 09:53:35","request":{"id":17,"user_id":2,"user_vehicle_id":1,"from_address":"Chimanlal Girdharlal Road, Paldi","from_lat":23.0115,"from_lng":72.5622,"to_address":"7-f, Paldi Road, Paldi Gaam, Paldi","to_lat":23.0119,"to_lng":72.5634,"total_estimated_travel_time":1,"total_estimated_trip_cost":3,"request_status":1,"created_at":"2018-05-05 09:48:18","updated_at":"2018-05-05 09:48:22"}}]
     * error_code : 0
     * message : Success
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
         * id : 6
         * request_id : 15
         * user_id : 1
         * driver_id : 3
         * status : 4
         * actual_trip_amount : 0
         * actual_trip_miles : 0
         * tip_amount : 0
         * actual_trip_travel_time : 0
         * trip_driver_rating : 0
         * trip_user_rating : 0
         * created_at : 2018-05-05 09:45:14
         * updated_at : 2018-05-05 09:45:55
         * request : {"id":15,"user_id":1,"user_vehicle_id":2,"from_address":"5, Mahalaxmi Five Rd, Chetan Society, Paldi, Ahmedabad, Gujarat 380007, India","from_lat":23.01,"from_lng":72.5626,"to_address":"2, Fatehpura, Shantivan, Motinagar, Paldi, Ahmedabad, Gujarat 380007, India","to_lat":23.0044,"to_lng":72.5602,"total_estimated_travel_time":12,"total_estimated_trip_cost":12.69,"request_status":1,"created_at":"2018-05-05 09:45:08","updated_at":"2018-05-05 09:45:14"}
         */

        private int id;
        private int request_id;
        private int user_id;
        private int driver_id;
        private int status;
        private int actual_trip_amount;
        private int actual_trip_miles;
        private int tip_amount;
        private int actual_trip_travel_time;
        private int trip_driver_rating;
        private int trip_user_rating;
        private String created_at;
        private String updated_at;
        private RequestBean request;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRequest_id() {
            return request_id;
        }

        public void setRequest_id(int request_id) {
            this.request_id = request_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(int driver_id) {
            this.driver_id = driver_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getActual_trip_amount() {
            return actual_trip_amount;
        }

        public void setActual_trip_amount(int actual_trip_amount) {
            this.actual_trip_amount = actual_trip_amount;
        }

        public int getActual_trip_miles() {
            return actual_trip_miles;
        }

        public void setActual_trip_miles(int actual_trip_miles) {
            this.actual_trip_miles = actual_trip_miles;
        }

        public int getTip_amount() {
            return tip_amount;
        }

        public void setTip_amount(int tip_amount) {
            this.tip_amount = tip_amount;
        }

        public int getActual_trip_travel_time() {
            return actual_trip_travel_time;
        }

        public void setActual_trip_travel_time(int actual_trip_travel_time) {
            this.actual_trip_travel_time = actual_trip_travel_time;
        }

        public int getTrip_driver_rating() {
            return trip_driver_rating;
        }

        public void setTrip_driver_rating(int trip_driver_rating) {
            this.trip_driver_rating = trip_driver_rating;
        }

        public int getTrip_user_rating() {
            return trip_user_rating;
        }

        public void setTrip_user_rating(int trip_user_rating) {
            this.trip_user_rating = trip_user_rating;
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

        public RequestBean getRequest() {
            return request;
        }

        public void setRequest(RequestBean request) {
            this.request = request;
        }

        public static class RequestBean {
            /**
             * id : 15
             * user_id : 1
             * user_vehicle_id : 2
             * from_address : 5, Mahalaxmi Five Rd, Chetan Society, Paldi, Ahmedabad, Gujarat 380007, India
             * from_lat : 23.01
             * from_lng : 72.5626
             * to_address : 2, Fatehpura, Shantivan, Motinagar, Paldi, Ahmedabad, Gujarat 380007, India
             * to_lat : 23.0044
             * to_lng : 72.5602
             * total_estimated_travel_time : 12
             * total_estimated_trip_cost : 12.69
             * request_status : 1
             * created_at : 2018-05-05 09:45:08
             * updated_at : 2018-05-05 09:45:14
             */

            private int id;
            private int user_id;
            private int user_vehicle_id;
            private String from_address;
            private double from_lat;
            private double from_lng;
            private String to_address;
            private double to_lat;
            private double to_lng;
            private int total_estimated_travel_time;
            private double total_estimated_trip_cost;
            private int request_status;
            private String created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public int getUser_vehicle_id() {
                return user_vehicle_id;
            }

            public void setUser_vehicle_id(int user_vehicle_id) {
                this.user_vehicle_id = user_vehicle_id;
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

            public int getTotal_estimated_travel_time() {
                return total_estimated_travel_time;
            }

            public void setTotal_estimated_travel_time(int total_estimated_travel_time) {
                this.total_estimated_travel_time = total_estimated_travel_time;
            }

            public double getTotal_estimated_trip_cost() {
                return total_estimated_trip_cost;
            }

            public void setTotal_estimated_trip_cost(double total_estimated_trip_cost) {
                this.total_estimated_trip_cost = total_estimated_trip_cost;
            }

            public int getRequest_status() {
                return request_status;
            }

            public void setRequest_status(int request_status) {
                this.request_status = request_status;
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
        }
    }
}