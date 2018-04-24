package com.greegoapp.greegodriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GetDriverData implements Parcelable{


    /**
     * message :
     * data : {"promocode":"0","bank_information":{"id":3,"created_at":"2018-04-17 08:40:16","driver_id":"1","updated_at":"2018-04-17 08:47:03","account_number":"MTIzNDU2Nzg=","routing_number":"123456789"},"documents":{},"personal_information":{"social_security_number":"1468935788","legal_lastname":"m","legal_middlename":"mm","legal_firstname":"m"},"lastname":"modan","type":{"is_van":"0","is_manual":"0","is_sedan":"1","is_suv":"0","is_auto":"1"},"contact_number":"+919998841576","id":1,"shipping_adress":{"id":3,"updated_at":"2018-04-17 08:37:12","apt":"app","zipcode":"12345","street":"sakar","state":"New York (NY)","created_at":"2018-04-17 08:32:00","driver_id":"1","city":"New York"},"is_agreed":"1","email":"muzammil@gmail.com","name":"muzammil","profile_status":6,"profile_pic":""}
     * error_code : 0
     */

    private String message;
    private DataBean data;
    private int error_code;

    protected GetDriverData(Parcel in) {
        message = in.readString();
        error_code = in.readInt();
    }

    public static final Creator<GetDriverData> CREATOR = new Creator<GetDriverData>() {
        @Override
        public GetDriverData createFromParcel(Parcel in) {
            return new GetDriverData(in);
        }

        @Override
        public GetDriverData[] newArray(int size) {
            return new GetDriverData[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeInt(error_code);
    }

    public static class DataBean {
        /**
         * promocode : 0
         * bank_information : {"id":3,"created_at":"2018-04-17 08:40:16","driver_id":"1","updated_at":"2018-04-17 08:47:03","account_number":"MTIzNDU2Nzg=","routing_number":"123456789"}
         * documents : {}
         * personal_information : {"social_security_number":"1468935788","legal_lastname":"m","legal_middlename":"mm","legal_firstname":"m"}
         * lastname : modan
         * type : {"is_van":"0","is_manual":"0","is_sedan":"1","is_suv":"0","is_auto":"1"}
         * contact_number : +919998841576
         * id : 1
         * shipping_adress : {"id":3,"updated_at":"2018-04-17 08:37:12","apt":"app","zipcode":"12345","street":"sakar","state":"New York (NY)","created_at":"2018-04-17 08:32:00","driver_id":"1","city":"New York"}
         * is_agreed : 1
         * email : muzammil@gmail.com
         * name : muzammil
         * profile_status : 6
         * profile_pic :
         */

        private String promocode;
        private BankInformationBean bank_information;
        private DocumentsBean documents;
        private PersonalInformationBean personal_information;
        private String lastname;
        private TypeBean type;
        private String contact_number;
        private int id;
        private ShippingAdressBean shipping_adress;
        private String is_agreed;
        private String email;
        private String name;
        private int profile_status;
        private String profile_pic;

        public String getPromocode() {
            return promocode;
        }

        public void setPromocode(String promocode) {
            this.promocode = promocode;
        }

        public BankInformationBean getBank_information() {
            return bank_information;
        }

        public void setBank_information(BankInformationBean bank_information) {
            this.bank_information = bank_information;
        }

        public DocumentsBean getDocuments() {
            return documents;
        }

        public void setDocuments(DocumentsBean documents) {
            this.documents = documents;
        }

        public PersonalInformationBean getPersonal_information() {
            return personal_information;
        }

        public void setPersonal_information(PersonalInformationBean personal_information) {
            this.personal_information = personal_information;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public TypeBean getType() {
            return type;
        }

        public void setType(TypeBean type) {
            this.type = type;
        }

        public String getContact_number() {
            return contact_number;
        }

        public void setContact_number(String contact_number) {
            this.contact_number = contact_number;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ShippingAdressBean getShipping_adress() {
            return shipping_adress;
        }

        public void setShipping_adress(ShippingAdressBean shipping_adress) {
            this.shipping_adress = shipping_adress;
        }

        public String getIs_agreed() {
            return is_agreed;
        }

        public void setIs_agreed(String is_agreed) {
            this.is_agreed = is_agreed;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getProfile_status() {
            return profile_status;
        }

        public void setProfile_status(int profile_status) {
            this.profile_status = profile_status;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public static class BankInformationBean {
        }

        public static class DocumentsBean {
        }

        public static class PersonalInformationBean {
            /**
             * social_security_number : 1468935788
             * legal_lastname : m
             * legal_middlename : mm
             * legal_firstname : m
             */

            private String social_security_number;
            private String legal_lastname;
            private String legal_middlename;
            private String legal_firstname;

            public String getSocial_security_number() {
                return social_security_number;
            }

            public void setSocial_security_number(String social_security_number) {
                this.social_security_number = social_security_number;
            }

            public String getLegal_lastname() {
                return legal_lastname;
            }

            public void setLegal_lastname(String legal_lastname) {
                this.legal_lastname = legal_lastname;
            }

            public String getLegal_middlename() {
                return legal_middlename;
            }

            public void setLegal_middlename(String legal_middlename) {
                this.legal_middlename = legal_middlename;
            }

            public String getLegal_firstname() {
                return legal_firstname;
            }

            public void setLegal_firstname(String legal_firstname) {
                this.legal_firstname = legal_firstname;
            }
        }

        public static class TypeBean {
            /**
             * is_van : 0
             * is_manual : 0
             * is_sedan : 1
             * is_suv : 0
             * is_auto : 1
             */

            private String is_van;
            private String is_manual;
            private String is_sedan;
            private String is_suv;
            private String is_auto;

            public String getIs_van() {
                return is_van;
            }

            public void setIs_van(String is_van) {
                this.is_van = is_van;
            }

            public String getIs_manual() {
                return is_manual;
            }

            public void setIs_manual(String is_manual) {
                this.is_manual = is_manual;
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

            public String getIs_auto() {
                return is_auto;
            }

            public void setIs_auto(String is_auto) {
                this.is_auto = is_auto;
            }
        }

        public static class ShippingAdressBean {
            /**
             * id : 3
             * updated_at : 2018-04-17 08:37:12
             * apt : app
             * zipcode : 12345
             * street : sakar
             * state : New York (NY)
             * created_at : 2018-04-17 08:32:00
             * driver_id : 1
             * city : New York
             */

            private int id;
            private String updated_at;
            private String apt;
            private String zipcode;
            private String street;
            private String state;
            private String created_at;
            private String driver_id;
            private String city;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            public String getApt() {
                return apt;
            }

            public void setApt(String apt) {
                this.apt = apt;
            }

            public String getZipcode() {
                return zipcode;
            }

            public void setZipcode(String zipcode) {
                this.zipcode = zipcode;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getDriver_id() {
                return driver_id;
            }

            public void setDriver_id(String driver_id) {
                this.driver_id = driver_id;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }
        }
    }
}
