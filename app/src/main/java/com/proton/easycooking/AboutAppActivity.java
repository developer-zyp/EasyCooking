package com.proton.easycooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.proton.easycooking.R;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        initToolbar();

        final TextView tvName = (TextView) findViewById(R.id.tv_appname);
        final TextView tvVersion = (TextView) findViewById(R.id.tv_appversion);
        final TextView tvAbout = (TextView) findViewById(R.id.tv_appabout);
        final TextView tvContact = (TextView) findViewById(R.id.tv_appcontact);

        tvVersion.setText(getResources().getString(R.string.app_version) + Config.app_version);
        tvAbout.setText(Config.app_about);
        tvContact.setText(Config.app_contact);

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
        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // This will display an Up icon (<-), we will replace it with hamburger later
        ActionBar actionBar = getSupportActionBar();
        // add back arrow to toolbar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            setTitle("About App");
        }

    }
}