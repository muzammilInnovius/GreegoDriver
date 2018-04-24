package com.greegoapp.greegodriver.GlobleFields;


import com.greegoapp.greegodriver.Utils.Applog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Android on 01-Feb-18.
 */

public class GlobalValues {
    public static final String BEARER_TOKEN = "Bearer ";
    public static final String USER_TYPE = "driver";

    public static final int SIGN_UP_NORMAL = 1;
    public static final int SIGN_UP_WITH_FB = 2;
    public static final String IS_FROM_CHANGE_ACCOUNT = "is_from_change_account";
    public static final String IS_FROM_LOGOUT = "is_from_logout";
    public static final String COUNTRY = "USA";

    public static final String ACCOUNT_TYPE = "AccountType";
    public static final String MANAGE_ACCOUNTANTS_TYPE = "AccountantsType";
    public static final String TRANSACTION_DETAILS = "transactionDetail";
    public static final String IS_TRANSACTION_DELETE = "isTransactionDelete";
    public static final String FAVORITE_CHART_OF_ACCOUNT_TYPE = "ExpenseAccountantsType";


    public static final int TAX_LIMIT = 6;


    public static final int PAYMENT_METHOD_CHEQUE = 1;
    public static final int PAYMENT_METHOD_CREDIT_CARD = 2;
    public static final int PAYMENT_METHOD_DIRECT_FROM_BANK = 3;
    public static final int PAYMENT_METHOD_CASH_FROM_BANK = 4;
    public static final int PAYMENT_METHOD_CREDIT_SALES = 5;
    public static final int PAYMENT_METHOD_CASH_ON_HAND = 6;
    public static final int PAYMENT_METHOD_CREDIT_PURCHASE = 7;
    public static final int JAN = 0;
    public static final int FEB = 1;
    public static final int MAR = 2;
    public static final int APR = 3;
    public static final int MAY = 4;
    public static final int JUN = 5;
    public static final int JUL = 6;
    public static final int AUG = 7;
    public static final int SEP = 8;
    public static final int OCT = 9;
    public static final int NOV = 10;
    public static final int DEC = 11;

    // Start Activity For Result
    public static final int ACCOUNT_TYPE_SELECTION_SEARCH_TRANSACTION = 1111;
    public static final int ACCOUNT_TYPE_SELECTION_PAYMENT = 2222;
    public static final int ACCOUNT_TYPE_SELECTION_RECEIPT = 2233;
    public static final int PAYMENT_METHOD_SELECTION_PAYMENT = 3333;
    public static final int TRANSACTION_DETAILS_TRANSACTION = 4444;
    public static final int ACCOUNT_TYPE_SELECTION_JOURNAL_TARGET_ACC = 5555;
    public static final int ACCOUNT_TYPE_SELECTION_DRILL_DOWN = 6666;
    public static final int ACCOUNT_TYPE_SELECTION_JOURNAL_DEST_ACC = 1112;
    public static final int MANAGE_ACCOUNTANTS_TYPE_NO = 7777;
    public static final int FAVORITE_CHART_OF_ACCOUNT_TYPE_NO = 8888;
    public static final int PAYMENT_METHOD_SELECTION_RECEIPT = 1515;
    public static final int PAYMENT_METHOD_SELECTION_ACC_PAYABLE = 2154;
    public static final int PAYMENT_METHOD_SELECTION_ACC_RECEIVABLE = 1451;

    public static final int TIME_OUT = 15000;



    public static long dataDifferent(String dateStop) {
        Date calendar = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US);

        Date dStart = null;
        Date dStop = null;
        long diffDays = 0;

        try {
            dStart = dateFormat.parse(calendar.toString());
            dStop = dateFormat.parse(dateStop);

            //in milliseconds
            long diff = dStop.getTime() - dStart.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days====>");

        } catch (Exception e) {
            e.printStackTrace();
        }

        Applog.e("diffDays==>" ,"" + diffDays);
        return diffDays;

    }
}
