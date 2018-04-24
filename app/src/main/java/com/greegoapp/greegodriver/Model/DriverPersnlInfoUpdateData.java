package com.greegoapp.greegodriver.Model;

public class DriverPersnlInfoUpdateData {


    /**
     * data : {"id":5,"name":"vishal","lastname":"gosia","email":"vishal","dob":null,"contact_number":"9876867876","city":null,"profile_pic":null,"promocode":"1234","legal_firstname":null,"legal_middlename":null,"legal_lastname":null,"social_security_number":null,"is_sedan":"0","is_suv":"0","is_van":"0","is_auto":"0","is_manual":"0","current_status":"0","verified":"0","is_agreed":"1","is_iphone":"0","profile_status":1,"created_at":"2018-04-13 05:47:20","updated_at":"2018-04-13 06:09:55"}
     * error_code : 0
     * message :
     */

    private DataBean data;
    private int error_code;
    private String message;

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
         * id : 5
         * name : vishal
         * lastname : gosia
         * email : vishal
         * dob : null
         * contact_number : 9876867876
         * city : null
         * profile_pic : null
         * promocode : 1234
         * legal_firstname : null
         * legal_middlename : null
         * legal_lastname : null
         * social_security_number : null
         * is_sedan : 0
         * is_suv : 0
         * is_van : 0
         * is_auto : 0
         * is_manual : 0
         * current_status : 0
         * verified : 0
         * is_agreed : 1
         * is_iphone : 0
         * profile_status : 1
         * created_at : 2018-04-13 05:47:20
         * updated_at : 2018-04-13 06:09:55
         */

        private int id;
        private String name;
        private String lastname;
        private String email;
        private Object dob;
        private String contact_number;
        private Object city;
        private Object profile_pic;
        private String promocode;
        private Object legal_firstname;
        private Object legal_middlename;
        private Object legal_lastname;
        private Object social_security_number;
        private String is_sedan;
        private String is_suv;
        private String is_van;
        private String is_auto;
        private String is_manual;
        private String current_status;
        private String verified;
        private String is_agreed;
        private String is_iphone;
        private int profile_status;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Object getDob() {
            return dob;
        }

        public void setDob(Object dob) {
            this.dob = dob;
        }

        public String getContact_number() {
            return contact_number;
        }

        public void setContact_number(String contact_number) {
            this.contact_number = contact_number;
        }

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
            this.city = city;
        }

        public Object getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(Object profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getPromocode() {
            return promocode;
        }

        public void setPromocode(String promocode) {
            this.promocode = promocode;
        }

        public Object getLegal_firstname() {
            return legal_firstname;
        }

        public void setLegal_firstname(Object legal_firstname) {
            this.legal_firstname = legal_firstname;
        }

        public Object getLegal_middlename() {
            return legal_middlename;
        }

        public void setLegal_middlename(Object legal_middlename) {
            this.legal_middlename = legal_middlename;
        }

        public Object getLegal_lastname() {
            return legal_lastname;
        }

        public void setLegal_lastname(Object legal_lastname) {
            this.legal_lastname = legal_lastname;
        }

        public Object getSocial_security_number() {
            return social_security_number;
        }

        public void setSocial_security_number(Object social_security_number) {
            this.social_security_number = social_security_number;
        }

        public String getIs_sedan() {
            return is_sedan;
        }

        public void setIs_sedan(String is_sedan) {
            this.is_sedan = is_sedan;
        }

        public String getIs_suv() {
            return is_suv;
        }

        public void setIs_suv(String is_suv) {
            this.is_suv = is_suv;
        }

        public String getIs_van() {
            return is_van;
        }

        public void setIs_van(String is_van) {
            this.is_van = is_van;
        }

        public String getIs_auto() {
            return is_auto;
        }

        public void setIs_auto(String is_auto) {
            this.is_auto = is_auto;
        }

        public String getIs_manual() {
            return is_manual;
        }

        public void setIs_manual(String is_manual) {
            this.is_manual = is_manual;
        }

        public String getCurrent_status() {
            return current_status;
        }

        public void setCurrent_status(String current_status) {
            this.current_status = current_status;
        }

        public String getVerified() {
            return verified;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }

        public String getIs_agreed() {
            return is_agreed;
        }

        public void setIs_agreed(String is_agreed) {
            this.is_agreed = is_agreed;
        }

        public String getIs_iphone() {
            return is_iphone;
        }

        public void setIs_iphone(String is_iphone) {
            this.is_iphone = is_iphone;
        }

        public int getProfile_status() {
            return profile_status;
        }

        public void setProfile_status(int profile_status) {
            this.profile_status = profile_status;
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
