package com.proton.easycooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class GlobalClass {

    public static int recipeId;
    public static int categoryId = 0;
    public static Date date = new Date();

    public static boolean checkInternetAvailability(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = cm.getAllNetworks();
        boolean hasInternet = false;
        if (networks.length > 0) {
            for (Network network : networks) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if (nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    hasInternet = true;
            }
        }
        return hasInternet;
    }

    public static boolean isConnectedToServer(String url) {
        try {
            URL myURL = new URL(url);
            HttpURLConnection con = (HttpURLConnection) myURL.openConnection();
            con.setConnectTimeout(3000); // Timeout 3 seconds.
            con.connect();
            return true;
//            if (con.getResponseCode() == 200) // Successful response.
//            {
//                return true;
//            } else {
//                return false;
//            }
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    public void ShowAlertDialog(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}



