package com.proton.easycooking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.proton.easycooking.models.AppConfig;
import com.proton.easycooking.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    List<AppConfig> appConfigList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        appConfigList = new ArrayList<>();
        dbHelper = DatabaseHelper.getInstance(this);

        new NetworkTask().execute(APIClient.BASE_URL);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkTask.checkServerConnection) {
                    getAppInfo();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("No Internet Connection.");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recreate();
                        }
                    });
                    alertDialog.show();
                }

            }
        }, 2000);

//        new CountDownTimer(2000, 1000) {
//            @Override
//            public void onFinish() {
//                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//        }.start();

    }

    private void getAppInfo() {
        Call<List<AppConfig>> apiCall = APIClient.getService().create(APIInterface.class).getAppConfig();
        apiCall.enqueue(new Callback<List<AppConfig>>() {
            @Override
            public void onResponse(Call<List<AppConfig>> call, Response<List<AppConfig>> response) {
                if (response.body() != null) {

                    appConfigList = response.body();
                    //Toast.makeText(SplashActivity.this, appConfigList.size() + " data is received!", Toast.LENGTH_LONG).show();
                    setAppInfo();
                    startApp();
                    Toast.makeText(SplashActivity.this, "Let's start cooking!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<AppConfig>> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "ERROR : Fail to receive data!", Toast.LENGTH_SHORT).show();
                System.out.println("Fail: " + t.getMessage());

            }
        });

    }

    private void setAppInfo() {
        dbHelper.deleteAppConfig();
        for (AppConfig appConfig : appConfigList) {
            dbHelper.addAppConfig(appConfig);
        }
        Config.app_version = dbHelper.getAppConfig("app_version");
        Config.app_about = dbHelper.getAppConfig("app_about");
        Config.app_contact = dbHelper.getAppConfig("app_contact");
        Config.app_message = dbHelper.getAppConfig("app_message");

        Config.ENABLE_ADMOB_BANNER_ADS = Boolean.parseBoolean(dbHelper.getAppConfig("banner_ads"));
        Config.ENABLE_ADMOB_INTERSTITIAL_ADS = Boolean.parseBoolean(dbHelper.getAppConfig("interstitial_ads"));
        Config.ENABLE_ADMOB_REWARDED_ADS = Boolean.parseBoolean(dbHelper.getAppConfig("rewarded_ads"));
        Config.ADMOB_INTERSTITIAL_ADS_INTERVAL = Integer.parseInt(dbHelper.getAppConfig("interstitial_interval"));
        Config.NUM_OF_SPECIAL_RECIPES = Integer.parseInt(dbHelper.getAppConfig("special_recipes"));
        Config.NUM_OF_MAX_RECIPES = Integer.parseInt(dbHelper.getAppConfig("max_recipes"));
        Config.ENABLE_EXIT_DIALOG = Boolean.parseBoolean(dbHelper.getAppConfig("exit_dialog"));
    }

    private void startApp() {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        if (version.equals(Config.app_version)) {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setIcon(R.drawable.ic_launcher);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage("New version is available!");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException exception) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    }

                }
            });

            dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SplashActivity.this.finish();
                    finishAndRemoveTask();
                }
            });
            dialog.show();
        }
    }

}