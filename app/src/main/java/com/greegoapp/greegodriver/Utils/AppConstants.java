package com.greegoapp.greegodriver.Utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;

public class AppConstants {

//	public static final String Weather_Url ="http://openweathermap.org/img/w/";

	public static final String STR_PROGRESS_DIALOG_MESSAGE = "Please Wait...";
	public static final String STR_INTERNET_ALERT_TITLE = "Network Error!";
	public static final String STR_INETRNET_ALERT_MESSAGE = "Something wrong with the internet connection.";
	public static final String CAMERA_FOLDER = "/RR";
	public static final String CAMERA_IMAGE_PREFIX = "RR-image";
	public static final String CAMERA_IMAGE_EXT = ".jpg";

	public static final int PIC_WIDTH = 512;
	public static final int PIC_HEIGHT = 512;

	public static final String LATTITUDE = "lattitude";
	public static ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
			ScaleAnimation.RELATIVE_TO_SELF, .5f,
			ScaleAnimation.RELATIVE_TO_SELF, .5f);
	public static final AlphaAnimation buttonClick = new AlphaAnimation(1F,
			0.5F);
	public static final int SCALE_TIME = 140;
	public static final String LONGITUDE = "longitude";
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";

	public static boolean isActivityRunning=false;
	public static boolean isActivityPaused=false;
	public static boolean isNewMessage=false;
	//public static final String HOMEWORTHY_IN_APP_KEY = "";
	public static final String PURCHASE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAghC1nD/Meb0wcTXmxppvknOeVq2gZorVZ3CnryuWBWjXCnSmcDCjaBDmK6RES3We9dtfjSVj/VZeBMpgUpvGYv0nOcfaNMCIZsiyFh8gLqMpmhFcDLc3j6PN36I8EatEBh4F45abtykIGe+gOlpH3lrfrqGykfvsI+YMICbxDVBngnfrxl66H2PQPHNY6Qt7fXn6OQSKuBB8+7RSZv4ponTmA4LdaWhCgCuNZCjqrh8AoxQNBp2YZxKLx2SSWqyNnfvhya6vKDiFndZrslO1IoIzhgaftT8hOemte6ZDf34bYBguHV0Hw5lHn6coBpqn0MAFHMRZJZKGX/MUmXUBtQIDAQAB";
	//public static final String SKU = "com.homeworthyinspection.tenant.report";// premium
//	public static final String SKU = "android.test.purchased";// test
	public static final String SKU_10 = "android.test.purchased";// test
	public static final String SKU_15 = "android.test.purchased";// test
	public static final String SKU_25 = "android.test.purchased";// test
	public static final String SKU_50 = "android.test.purchased";// test

	public static final int REQUEST_CODE_PURCHASE = 1001;
}
