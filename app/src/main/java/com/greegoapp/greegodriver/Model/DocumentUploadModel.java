package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentUploadModel implements Parcelable{

    /**
     * data : {"profile_status":4}
     * error_code : 0
     * message : Document upload saved.
     */

    private DataBean data;
    private int error_code;
    private String message;

    protected DocumentUploadModel(Parcel in) {
        error_code = in.readInt();
        message = in.readString();
    }

    public static final Creator<DocumentUploadModel> CREATOR = new Creator<DocumentUploadModel>() {
        @Override
        public DocumentUploadModel createFromParcel(Parcel in) {
            return new DocumentUploadModel(in);
        }

        @Override
        public DocumentUploadModel[] newArray(int size) {
            return new DocumentUploadModel[size];
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
         * profile_status : 4
         */

        private int profile_status;

        public int getProfile_status() {
            return profile_status;
        }

        public void setProfile_status(int profile_status) {
            this.profile_status = profile_status;
        }
    }
}
