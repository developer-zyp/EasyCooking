package com.proton.easycooking;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask extends AsyncTask<String, Void, Boolean> {

    public static Boolean checkServerConnection = false;

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            URL myURL = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) myURL.openConnection();
            con.setConnectTimeout(3000); // Timeout 3 seconds
            con.connect();
            if (con.getResponseCode() == 200) {
                // Successful response
//                checkServerConnection = true;
                return true;
            }
            else {
//                checkServerConnection = false;
                return false;
            }
        } catch (Exception e) {
            // Handle your exceptions
//            checkServerConnection = false;
            return false;
        }

//        return checkServerConnection;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        checkServerConnection = result;
    }
}
