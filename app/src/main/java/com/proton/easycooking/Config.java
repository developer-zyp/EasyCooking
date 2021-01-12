package com.proton.easycooking;

public class Config {

    //your admin panel url here
    public static final String SERVER_URL = "http://192.168.1.11/your_recipes_app";

    public static String app_version = "1.0";
    public static String app_about = "";
    public static String app_contact = "";
    public static String app_message = "0";

    //set true to enable ads or set false to disable ads
    public static boolean ENABLE_ADMOB_BANNER_ADS = true;
    public static boolean ENABLE_ADMOB_INTERSTITIAL_ADS = true;
    public static boolean ENABLE_ADMOB_REWARDED_ADS = true;
    public static int ADMOB_INTERSTITIAL_ADS_INTERVAL = 2;

    public static String ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    public static String ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    public static String ADMOB_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";


    //set true to enable exit dialog or set false to disable exit dialog
    public static boolean ENABLE_EXIT_DIALOG = true;

    //limit for number of recent recipes
    public static int NUM_OF_SPECIAL_RECIPES = 10;
    public static int NUM_OF_MAX_RECIPES = 10;

}
