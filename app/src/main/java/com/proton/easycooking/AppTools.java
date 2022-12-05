package com.proton.easycooking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import com.proton.easycooking.models.Recipe;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AppTools {

    public static Recipe itemRecipeDetail;
    public static int categoryId = 0;
    //private static boolean connection_status = false;
    private static ProgressDialog progressDialog;

    public static void showToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    public static void showAlertDialog(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    public static void showProgressDialog(Context ctx, String message) {
        progressDialog = new ProgressDialog(ctx, R.style.MyProgressDialogStyle);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void openURL(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);

    }

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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Boolean checkConnection() {
        boolean connection_status = isConnectedToServer("https://zyphost.000webhostapp.com/");
        return connection_status;
    }

    public static boolean isConnectedToServer(String url) {
        try {
            URL myUrl = new URL(url);
            int timeout = 1500;
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    public static String getRandom(Context context, Boolean isRefresh) {
        StringBuilder result = new StringBuilder("");
        int random = 0;

        String nowDate = getDateTime();
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        String recipes_date = db.getNewRecipesId("100");

        if (recipes_date == null) {
            for (int i = 0; i < AppConfig.NUM_OF_SPECIAL_RECIPES; i++) {

                random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1; // [0, 60] + 20 => [20, 80]
                while (result.toString().contains(Integer.toString(random))) {
                    random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1;
                }

                result.append(",").append(random);
            }

        } else {
            if (recipes_date.equals(nowDate) && !isRefresh) {
                result.append(db.getAppConfig(nowDate));
            } else {
                for (int i = 0; i < AppConfig.NUM_OF_SPECIAL_RECIPES; i++) {

                    random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1; // [0, 60] + 20 => [20, 80]
                    while (result.toString().contains(Integer.toString(random))) {
                        random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1;
                    }

                    result.append(",").append(random);
                }

                db.updateNewRecipesId(nowDate, result.toString());
            }
        }

        //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        return "0" + result + "";
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());// yyyy/MM/dd HH:mm:ss
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static <T> List<T> limitList(List<T> list, int limit) {
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
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



