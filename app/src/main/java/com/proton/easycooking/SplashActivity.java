package com.proton.easycooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.proton.easycooking.models.AppSetting;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    List<AppSetting> appDataList;
    List<AppSetting> appSettingList;
    DatabaseHelper dbHelper;
    boolean connection_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        appDataList = new ArrayList<>();
        appSettingList = new ArrayList<>();
        dbHelper = DatabaseHelper.getInstance(this);

        new NetworkTask().execute(APIClient.BASE_URL);

        new Handler().postDelayed(() -> {
            if (AppTools.isNetworkAvailable(SplashActivity.this)) {
                GetAppData();
            } else {
                RestartApp();
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

    private void GetAppData() {
        Call<List<AppSetting>> apiCall = APIClient.getService().create(APIInterface.class).getJson(AppConfig.SERVER_URL);
        apiCall.enqueue(new Callback<List<AppSetting>>() {
            @Override
            public void onResponse(Call<List<AppSetting>> call, Response<List<AppSetting>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    appDataList = response.body();
                    APIClient.BASE_URL = appDataList.get(0).getValue();
                }

                GetAppSetting();

            }

            @Override
            public void onFailure(Call<List<AppSetting>> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "Fail to load data!", Toast.LENGTH_SHORT).show();
                ShowFixDialog();
                System.out.println("Fail: " + t.getMessage());

            }
        });

    }

    private void GetAppSetting() {
        Call<List<AppSetting>> apiCall = APIClient.getService().create(APIInterface.class).getAppConfig();
        apiCall.enqueue(new Callback<List<AppSetting>>() {
            @Override
            public void onResponse(Call<List<AppSetting>> call, Response<List<AppSetting>> response) {
                if (response.body() != null) {
                    appSettingList = response.body();
                    SetAppInfo();
                    StartApp();
                }
            }

            @Override
            public void onFailure(Call<List<AppSetting>> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "Fail to load data!", Toast.LENGTH_SHORT).show();
                ShowFixDialog();
                System.out.println("Fail: " + t.getMessage());

            }
        });

    }

    private void SetAppInfo() {
        dbHelper.deleteAppConfig();
        for (AppSetting appsetting : appSettingList) {
            dbHelper.addAppConfig(appsetting);
        }
        AppConfig.app_version = dbHelper.getAppConfig("app_version");
//        AppConfig.app_about = dbHelper.getAppConfig("app_about");
//        AppConfig.app_contact = dbHelper.getAppConfig("app_contact");
        AppConfig.app_message = dbHelper.getAppConfig("app_message");
        AppConfig.show_message = dbHelper.getAppConfig("show_message");

        AppConfig.ENABLE_ADMOB_BANNER_ADS = Boolean.parseBoolean(dbHelper.getAppConfig("banner_ads"));
        AppConfig.ENABLE_ADMOB_INTERSTITIAL_ADS = Boolean.parseBoolean(dbHelper.getAppConfig("interstitial_ads"));
        AppConfig.ENABLE_ADMOB_REWARDED_ADS = Boolean.parseBoolean(dbHelper.getAppConfig("rewarded_ads"));

        AppConfig.ADMOB_BANNER_ID = dbHelper.getAppConfig("admob_banner_id");
        AppConfig.ADMOB_INTERSTITIAL_ID = dbHelper.getAppConfig("admob_interstitial_id");
        AppConfig.ADMOB_REWARDED_ID = dbHelper.getAppConfig("admob_rewarded_id");

        AppConfig.INTERSTITIAL_INTERVAL = Integer.parseInt(dbHelper.getAppConfig("interstitial_interval"));
        AppConfig.ADMOB_REWARDED_INTERVAL = Integer.parseInt(dbHelper.getAppConfig("rewarded_interval"));

        AppConfig.NUM_OF_SPECIAL_RECIPES = Integer.parseInt(dbHelper.getAppConfig("special_recipes"));
        AppConfig.NUM_OF_MAX_RECIPES = Integer.parseInt(dbHelper.getAppConfig("max_recipes"));
        AppConfig.ENABLE_EXIT_DIALOG = Boolean.parseBoolean(dbHelper.getAppConfig("exit_dialog"));

    }

    private void StartApp() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
//        PackageManager manager = getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

//        String version = BuildConfig.VERSION_NAME;
//        if (version.equals(AppConfig.app_version)) {
//            startActivity(new Intent(getBaseContext(), MainActivity.class));
//            finish();
//        } else {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
//            dialog.setIcon(R.mipmap.ic_launcher);
//            dialog.setTitle(R.string.app_name);
//            dialog.setMessage(R.string.new_version);
//            dialog.setCancelable(false);
//            dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    final String appPackageName = getPackageName();
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
//                    } catch (android.content.ActivityNotFoundException exception) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                    }
//                    startActivity(new Intent(getBaseContext(), MainActivity.class));
//                    finish();
//
//                }
//            });
//
//            dialog.setNeutralButton("Later", (dialogInterface, i) -> {
////                SplashActivity.this.finish();
////                finishAndRemoveTask();
//                startActivity(new Intent(getBaseContext(), MainActivity.class));
//                finish();
//            });
//            dialog.show();
//        }

    }

    private void RestartApp() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage("No Connection with Server.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Try Again", (dialogInterface, i) -> recreate());
//        alertDialog.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
//            }
//        });
        alertDialog.show();
    }

    private void ShowFixDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setMessage(R.string.app_fix);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Try Again", (dialogInterface, i) -> recreate());
        alertDialog.show();
    }

}