package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ExpressPayData implements Parcelable{

    /**
     * data : {"id":"po_1CQSkuIyJz9zAr4DSGtoIoDY","object":"payout","amount":10000,"arrival_date":1526012480,"automatic":false,"balance_transaction":"txn_1CQSkuIyJz9zAr4Dm2Cxxy4h","created":1526012480,"currency":"usd","description":null,"destination":"ba_1COPFRIyJz9zAr4D8A52eX6i","failure_balance_transaction":null,"failure_code":null,"failure_message":null,"livemode":false,"metadata":[],"method":"standard","source_type":"card","statement_descriptor":null,"status":"pending","type":"bank_account"}
     * error_code : 0
     * message : Payout successfully
     */

    private DataBean data;
    private int error_code;
    private String message;

    protected ExpressPayData(Parcel in) {
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

    public static final Creator<ExpressPayData> CREATOR = new Creator<ExpressPayData>() {
        @Override
        public ExpressPayData createFromParcel(Parcel in) {
            return new ExpressPayData(in);
        }

        @Override
        public ExpressPayData[] newArray(int size) {
            return new ExpressPayData[size];
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
         * id : po_1CQSkuIyJz9zAr4DSGtoIoDY
         * object : payout
         * amount : 10000
         * arrival_date : 1526012480
         * automatic : false
         * balance_transaction : txn_1CQSkuIyJz9zAr4Dm2Cxxy4h
         * created : 1526012480
         * currency : usd
         * description : null
         * destination : ba_1COPFRIyJz9zAr4D8A52eX6i
         * failure_balance_transaction : null
         * failure_code : null
         * failure_message : null
         * livemode : false
         * metadata : []
         * method : standard
         * source_type : card
         * statement_descriptor : null
         * status : pending
         * type : bank_account
         */

        private String id;
        private String object;
        private int amount;
        private int arrival_date;
        private boolean automatic;
        private String balance_transaction;
        private int created;
        private String currency;
        private Object description;
        private String destination;
        private Object failure_balance_transaction;
        private Object failure_code;
        private Object failure_message;
        private boolean livemode;
        private String method;
        private String source_type;
        private Object statement_descriptor;
        private String status;
        private String type;
        private List<?> metadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getArrival_date() {
            return arrival_date;
        }

        public void setArrival_date(int arrival_date) {
            this.arrival_date = arrival_date;
        }

        public boolean isAutomatic() {
            return automatic;
        }

        public void setAutomatic(boolean automatic) {
            this.automatic = automatic;
        }

        public String getBalance_transaction() {
            return balance_transaction;
        }

        public void setBalance_transaction(String balance_transaction) {
            this.balance_transaction = balance_transaction;
        }

        public int getCreated() {
            return created;
        }

        public void setCreated(int created) {
            this.created = created;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public Object getFailure_balance_transaction() {
            return failure_balance_transaction;
        }

        public void setFailure_balance_transaction(Object failure_balance_transaction) {
            this.failure_balance_transaction = failure_balance_transaction;
        }

        public Object getFailure_code() {
            return failure_code;
        }

        public void setFailure_code(Object failure_code) {
            this.failure_code = failure_code;
        }

        public Object getFailure_message() {
            return failure_message;
        }

        public void setFailure_message(Object failure_message) {
            this.failure_message = failure_message;
        }

        public boolean isLivemode() {
            return livemode;
        }

        public void setLivemode(boolean livemode) {
            this.livemode = livemode;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getSource_type() {
            return source_type;
        }

        public void setSource_type(String source_type) {
            this.source_type = source_type;
        }

        public Object getStatement_descriptor() {
            return statement_descriptor;
        }

        public void setStatement_descriptor(Object statement_descriptor) {
            this.statement_descriptor = statement_descriptor;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<?> getMetadata() {
            return metadata;
        }

        public void setMetadata(List<?> metadata) {
            this.metadata = metadata;
        }
    }
}
