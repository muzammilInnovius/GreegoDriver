package com.greegoapp.greegodriver.Utils;


import com.stripe.android.Stripe;

public class WebFields {
    //http://innoviussoftware.com/greego/public/
   // public static final String BASE_URL = "http://innoviussoftware.com/greego/public/api/";
    // public static final String BASE_URL = "https://www.kroslinkstech.in/greego/public/api/";
     public static final String BASE_URL = "http://54.144.110.21/api/";

    public static final String USERID = "userId";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String PARAM_ACCEPT = "Accept";
    public static final String PARAM_AUTHOTIZATION = "Authorization";

    public static final class STATE_URL {
        //        http://kroslinkstech.in/greego/public/api/get/all_states
        public static final String MODE = "get/all_states";
    }


    public static final class DIST_URL {
        public static final String MODE = "https://maps.googleapis.com/maps/api/distancematrix/json?";//units=imperial&
        public static final String ORIGINS = "origin=";
        public static final String DESTINATION = "&destination=";
        public static final String KEY = "&key=AIzaSyAv9JCgBG0AyQocQvwYuqyrstGIhHZOEaw";
    }

    public static final class SIGN_IN {
        public static final String MODE = "login";
        public static final String PARAM_CONTACT_NO = "contact_number";
        public static final String PARAM_IS_PHONE_NO = "is_iphone";
        public static final String PARAM_USER_TYPE = "user_type";
        public static final String PARAM_DEVICE_ID = "device_id";

    }

    public static final class TRIP {
        public static final String MODE = "get/rates";
        public static final String PARAM_STATE = "state";
    }

    public static final class USER_ME {
        public static final String MODE = "driver/me";
    }

    public static final class SIGN_UP_PROFILE_UPDATE {
        public static final String MODE = "driver/update";

        public static final String PARAM_FIRST_NAME = "name";
        public static final String PARAM_LAST_NAME = "lastname";
        public static final String PARAM_PROMO_CODE = "promocode";
        public static final String PARAM_IS_AGGREED = "is_agreed";
        public static final String PARAM_EMAIL = "email";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_PRO_PIC = "profile_pic";


    }

    public static final class SIGN_UP_PERSONAL_INFO {
        public static final String MODE = "driver/update/personalinfo";

        public static final String PARAM_LEGAL_FNAME = "legal_firstname";
        public static final String PARAM_LEGAL_MNAME = "legal_middlename";
        public static final String PARAM_LEGAL_LNAME = "legal_lastname";
        public static final String PARAM_SOCIAL_SICURITY_NO = "social_security_number";
        public static final String PARAM_DATE_OF_BIRTH = "dob";

    }


    public static final class SIGN_UP_SHIPPING_ADDRESS {
        //public static final String MODE="https://www.kroslinkstech.in/greego/public/api/driver/update/shippingadress";
        public static final String MODE = "driver/update/shippingadress";

        public static final String PARAM_STREET = "street";
        public static final String PARAM_APT = "apt";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_ZIPCODE = "zipcode";
        public static final String PARAM_STATE = "state";

    }

    public static final class SIGN_UP_ATTACH_FILE {
        public static final String MODE = "driver/update/attachfile";

        public static final String PARAM_STREET = "street";
        public static final String PARAM_APT = "apt";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_ZIPCODE = "zipcode";
        public static final String PARAM_STATE = "state";

    }

    public static final class SIGN_UP_UPLOAD_FILE {
        public static final String MODE = "driver/update/document_base64";

        public static final String PARAM_DRIVING_LICENSE = "driving_license";
        public static final String PARAM_INSURANCE_CARD = "insurance_card";
        public static final String PARAM_HOME_INSURANCE = "home_insurance";
        public static final String PARAM_CURRENT_DRIVER = "current_driver";
    }

    public static final class SIGN_UP_BANK_INFO {
        public static final String MODE = "driver/update/bankinfo";

        public static final String PARAM_ACCOUNT_NO = "account_number";
        public static final String PARAM_ROUTING_NO = "routing_number";

    }

    public static final class SIGN_UP_PROFILE_PIC {
        public static final String MODE = "driver/update/profile_pic_base64";

        public static final String PARAM_IMAGE = "image";

    }


    public static final class SIGN_UP_DRIVER_TYPE {
        public static final String MODE = "driver/update/drivertype";

        public static final String PARAM_IS_SEDAN = "is_sedan";
        public static final String PARAM_IS_SUV = "is_suv";
        public static final String PARAM_IS_VAN = "is_van";
        public static final String PARAM_IS_AUTO = "is_auto";
        public static final String PARAM_IS_MANUAL = "is_manual";

    }

    public static final class ACCEPT_USER_REQUEST {
        public static final String MODE = "driver/view/request";
        public static final String request_id = "request_id";
    }

    public static final class ACCPET_TRIP {
        public static final String MODE = "driver/accept/request";
        public static final String request_id = "request_id";
    }

    public static final class SEND_LATLNG {
        public static final String MODE = "driver/update/location";
        public static final String lat = "lat";
        public static final String lng = "lng";
    }

    public static final class DRIVER_TRIP_STATUS {
        public static final String MODE = "driver/trip/change/status";
        public static final String trip_id = "trip_id";
        public static final String status = "status";
        public static final String actual_trip_amount = "actual_trip_amount";
        public static final String actual_trip_miles = "actual_trip_miles";
        public static final String actual_trip_travel_time = "actual_trip_travel_time";
    }

    //param : trip_id, tip_amount,trip_user_rating,transaction_id,user_card_id,payment_status,transaction_amount,transaction_description
    public static final class TRIP_TANSACTION {
        public static final String MODE = "driver/add/triptransaction";
        public static final String trip_id = "trip_id";
        public static final String tip_amount = "tip_amount";
        public static final String trip_user_rating = "trip_user_rating";
        public static final String transaction_id = "transaction_id";
        public static final String user_card_id = "user_card_id";
        public static final String payment_status = "payment_status";
        public static final String transaction_amount = "transaction_amount";
        public static final String transaction_description = "transaction_description";
    }

    public static final class TRIP_RATING {
        public static final String MODE = "driver/add/userrating";
        public static final String trip_id = "trip_id";
        public static final String trip_user_rating = "rating";
    }

    public static final class TRIP_HISTORY{
        public static  final String MODE="driver/get/triphistory";
    }
    public static final class EXPRESS_PAY{
        public static final String MODE="driver/express/pay";
        public static final String pay_amount = "pay_amount";
        public static final String driver_id = "driver_id";
    }

    public static final class CHANGE_DEVICE_ID
    {
        public static final String MODE = "driver/update/device";
        public static final String driver_on = "driver_on";
    }
    public static final class VERIFY_PROMO
    {
        public static final String MODE = "driver/verify/promocode";
        public static final String promo_code = "promocode";
    }
    public static final class PAYOUT_HISTORY
    {
        public static final String MODE = "driver/get/payout_history";
        public static final String driver_id = "driver_id";
    }
    public static final class VERIFY_EMAIL
    {
        public static final String MODE = "driver/verify/email";
    }
    public static final class TERMS_CONDITION
    {
        public static final String MODE = "get/texts";
    }
    public static final class DOCUMENT_UPLOAD
    {
        public static final String MODE = "driver/update/single_document_base64";
        public static final String image = "image";
        public static final String name = "name";
    }

}
