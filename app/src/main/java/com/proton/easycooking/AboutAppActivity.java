package com.proton.easycooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        initToolbar();

        final TextView tvName = findViewById(R.id.tv_appname);
        final TextView tvVersion = findViewById(R.id.tv_appversion);
        final TextView tvAbout = findViewById(R.id.tv_appabout);
        final TextView tvContact = findViewById(R.id.tv_appcontact);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

        tvVersion.setText(getResources().getString(R.string.app_version) + " " + BuildConfig.VERSION_NAME);
        tvAbout.setText(dbHelper.getAppConfig("app_about"));
        tvContact.setText(dbHelper.getAppConfig("app_contact"));

        tvContact.setOnLongClickListener(view -> {
            Log.d("BUILD_TYPE", String.valueOf(com.proton.easycooking.BuildConfig.DEBUG));
            if (BuildConfig.DEBUG) {
                Intent intent = new Intent(AboutAppActivity.this, AdminActivity.class);
                startActivity(intent);
//                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                final EditText edittext = new EditText(this);
//                edittext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                dialog.setTitle("Enter Password :");
//                dialog.setView(edittext);
//
//                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        //What ever you want to do with the value
//                        if (edittext.getText().toString().equals(getDailyPassword())) {
//                            dialog.dismiss();
//                            Intent intent = new Intent(AboutAppActivity.this, AdminActivity.class);
//                            startActivity(intent);
//                        }
//
//                    }
//                });
//
//                dialog.show();
            }
            return false;
        });

    }

    private String getDailyPassword() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String currentDateTime = sdf.format(new Date());
        char[] currentDT = currentDateTime.toCharArray();
        int dividend = Integer.parseInt(currentDateTime);
        int divisor = 16;
        int quotient = 0;
        for (char i : currentDT) {
            divisor += Integer.parseInt(String.valueOf(i));
        }

        quotient = dividend / divisor;

        Log.i("DailyPassword", quotient + ", " + dividend + ", " + divisor);

        return String.valueOf(quotient);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            setTitle("About App");
        }

    }
}