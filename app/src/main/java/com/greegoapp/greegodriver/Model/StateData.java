package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PublicKey;
import java.util.List;

public class StateData implements Parcelable{

    /**
     * data : [{"id":1,"state_name":"Alabama"},{"id":2,"state_name":"Alaska"},{"id":3,"state_name":"Arizona"},{"id":4,"state_name":"Arkansas"},{"id":5,"state_name":"California"},{"id":6,"state_name":"Colorado"},{"id":7,"state_name":"Connecticut"},{"id":8,"state_name":"Delaware"},{"id":9,"state_name":"District of Columbia"},{"id":10,"state_name":"Florida"},{"id":11,"state_name":"Georgia"},{"id":12,"state_name":"Hawaii"},{"id":13,"state_name":"Illinois"},{"id":14,"state_name":"Indiana"},{"id":15,"state_name":"Iowa"},{"id":16,"state_name":"Kansas"},{"id":17,"state_name":"Kentucky"},{"id":18,"state_name":"Louisiana"},{"id":19,"state_name":"Maine"},{"id":20,"state_name":"Montana"},{"id":21,"state_name":"Nebraska"},{"id":22,"state_name":"Nevada"},{"id":23,"state_name":"New Hampshire"},{"id":24,"state_name":"New Jersey"},{"id":25,"state_name":"New Mexico"},{"id":26,"state_name":"New York"},{"id":37,"state_name":"North Carolina"},{"id":38,"state_name":"North Dakota"},{"id":39,"state_name":"Ohio"},{"id":40,"state_name":"Oklahoma"},{"id":41,"state_name":"Oregon"},{"id":42,"state_name":"Maryland"},{"id":43,"state_name":"Massachusetts"},{"id":44,"state_name":"Michigan"},{"id":45,"state_name":"Minnesota"},{"id":46,"state_name":"Mississippi"},{"id":47,"state_name":"Missouri"},{"id":48,"state_name":"Pennsylvania"},{"id":49,"state_name":"Rhode Island"},{"id":50,"state_name":"South Carolina"},{"id":51,"state_name":"South Dakota"},{"id":52,"state_name":"Tennessee"},{"id":53,"state_name":"Texas"},{"id":54,"state_name":"Utah"},{"id":55,"state_name":"Vermont"},{"id":56,"state_name":"Virginia"},{"id":57,"state_name":"Washington"},{"id":58,"state_name":"West Virginia"},{"id":59,"state_name":"Wisconsin"},{"id":60,"state_name":"Wyoming"}]
     * error_code : 0
     * message :
     */

    private int error_code;
    private String message;
    private List<DataBean> data;

    protected StateData(Parcel in) {
        error_code = in.readInt();
        message = in.readString();
    }

    public static final Creator<StateData> CREATOR = new Creator<StateData>() {
        @Override
        public StateData createFromParcel(Parcel in) {
            return new StateData(in);
        }

        @Override
        public StateData[] newArray(int size) {
            return new StateData[size];
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
         * state_name : Alabama
         */

        private int id;
        private String state_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }
        public String toString(){
            return state_name;
        }
    }

}
