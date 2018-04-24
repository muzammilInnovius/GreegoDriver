package com.greegoapp.greegodriver.Utils;

public class WebFields {
    public static final String BASE_URL = "http://innoviussoftware.com/greego/public/api/";

    public static final String USERID = "userId";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String PARAM_ACCEPT = "Accept";
    public static final String PARAM_AUTHOTIZATION = "Authorization";

    public static final class SIGN_IN {
        public static final String MODE = "login";
        public static final String PARAM_CONTACT_NO = "contact_number";
        public static final String PARAM_IS_PHONE_NO = "is_iphone";
        public static final String PARAM_USER_TYPE = "user_type";
        public static final String PARAM_DEVICE_ID = "device_id";

    }
    public static final class TRIP{
        public static  final String MODE = "user/get/rates";
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
        public static final String MODE = "driver/update/document";

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
        public static final String MODE = "driver/update/profile_pic";

        public static final String PARAM_IMAGE = "image";

    }


    public static final class SIGN_UP_DRIVER_TYPE{
        public static final String MODE = "driver/update/drivertype";

        public static final String PARAM_IS_SEDAN = "is_sedan";
        public static final String PARAM_IS_SUV = "is_suv";
        public static final String PARAM_IS_VAN = "is_van";
        public static final String PARAM_IS_AUTO = "is_auto";
        public static final String PARAM_IS_MANUAL = "is_manual";

    }

    public static final class ACCEPT_USER_REQUEST{
        public static final String MODE="driver/view/request";
        public static final String request_id="request_id";
    }

    public static final class ACCPET_TRIP{
        public static final String MODE="driver/accept/request";
        public static final String request_id="request_id";
    }

}
