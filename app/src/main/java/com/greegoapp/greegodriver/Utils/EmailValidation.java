package com.greegoapp.greegodriver.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Android on 18-Aug-17.
 */

public class EmailValidation {
    private static final String VALID_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    ;

    public static boolean checkEmailIsCorrect(String strEmail) {
//		try {
        boolean isvalid;
        Pattern pattern = Pattern.compile(VALID_EMAIL);
        Matcher matcher = pattern.matcher(strEmail);
        if (matcher.matches()) {
            isvalid = true;
        } else {
            isvalid = false;
        }
        return isvalid;
    }

    public static boolean isValidUrl(String txtWebsite) {
        Pattern regex = Pattern.compile("^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)$");
        Matcher matcher = regex.matcher(txtWebsite);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
