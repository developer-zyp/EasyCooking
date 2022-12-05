package com.proton.easycooking;

public class AppConfig {

    //your admin panel url here
    public static final String SERVER_URL = "https://opensheet.elk.sh/1cEx3RrTFGwIddpn3EFBBHAzIQtCDCUZK9BH_4UHs3iM/easycooking";

    public static String app_version = BuildConfig.VERSION_NAME;
    //    public static String app_about = "";
    //    public static String app_contact = "";
    public static String app_message = "0";
    public static String show_message = "";

    //set true to enable ads or set false to disable ads
    public static boolean ENABLE_ADMOB_BANNER_ADS = false;
    public static boolean ENABLE_ADMOB_INTERSTITIAL_ADS = false;
    public static boolean ENABLE_ADMOB_REWARDED_ADS = false;

    public static int INTERSTITIAL_COUNT = 0;
    public static int INTERSTITIAL_INTERVAL = 3;

    public static int ADMOB_REWARDED_INTERVAL = 3;
    public static int ADMOB_REWARDED_COUNT = ADMOB_REWARDED_INTERVAL - 1;

    public static String DEVELOPER_ID = "6678365446619087361";
    public static String ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    public static String ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    public static String ADMOB_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";

    //set true to enable exit dialog or set false to disable exit dialog
    public static boolean ENABLE_EXIT_DIALOG = true;

    //limit for number of recent recipes
    public static int NUM_OF_SPECIAL_RECIPES = 10;
    public static int NUM_OF_MAX_RECIPES = 10;

}
