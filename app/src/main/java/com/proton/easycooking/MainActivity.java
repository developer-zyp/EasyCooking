package com.proton.easycooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.proton.easycooking.fragments.CategoryFragment;
import com.proton.easycooking.fragments.FavoriteFragment;
import com.proton.easycooking.fragments.NoEatTogetherFragment;
import com.proton.easycooking.fragments.TodaySpecialFragment;
import com.proton.easycooking.tabs.TabCaloriesCalcFragment;
import com.proton.easycooking.R;
import com.proton.easycooking.fragments.*;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;

    public static FragmentManager fragmentManager;

    private boolean mToolBarNavigationListenerIsRegistered = false;

    CardView adCardView;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        firebaseAnalytics();

        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            loadAdMobBannerAd();
        }

        initToolbar();

        initNavigationDrawer();

        fragmentManager = getSupportFragmentManager();

        getSupportFragmentManager().addOnBackStackChangedListener(this::displayHomeUpOrHamburger);
        displayHomeUpOrHamburger();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TodaySpecialFragment()).commit();

        showMessageFromServer();

    }

    private void initToolbar() {
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // This will display an Up icon (<-), we will replace it with hamburger later
        ActionBar actionBar = getSupportActionBar();
        // add back arrow to toolbar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public void initNavigationDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView nav_View = (NavigationView) findViewById(R.id.nav_view);

        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                mDrawer.closeDrawers();
                return true;
            }
        });

    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        if (!menuItem.isChecked()) {
            Fragment fragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_today_special:
                    fragment = new TodaySpecialFragment();
                    break;
                case R.id.nav_category:
                    fragment = new CategoryFragment(1);
                    break;
                case R.id.nav_calories_cal:
                    fragment = new TabCaloriesCalcFragment();
                    break;
                case R.id.nav_never_eat:
                    fragment = new NoEatTogetherFragment();
                    break;
                case R.id.nav_favorite:
                    fragment = new FavoriteFragment();
                    break;
                case R.id.nav_share:
                    shareApp();
                    break;
                case R.id.nav_rate:
                    rateApp();
                    break;
                case R.id.nav_about:
                    aboutApp();
                    break;
                default:
                    fragment = new TodaySpecialFragment();
            }

            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (menuItem.getItemId() == R.id.nav_share ||
                    menuItem.getItemId() == R.id.nav_rate ||
                    menuItem.getItemId() == R.id.nav_about) {
                menuItem.setChecked(false);
            } else {
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {

            if (Config.ENABLE_EXIT_DIALOG) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.drawable.ic_launcher);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage("Are you sure want to quit!");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                });

                dialog.setNegativeButton("Rate Us", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String appName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                        } catch (android.content.ActivityNotFoundException exception) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                        }

                        MainActivity.this.finish();
                    }
                });

                dialog.setNeutralButton("More Apps", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com")));
                        MainActivity.this.finish();
                    }
                });
                dialog.show();

            } else {
                super.onBackPressed();
            }
        }
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "\n" + getResources().getString(R.string.app_name) + "\n"
                + getResources().getString(R.string.share_text)
                + "https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void rateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
        startActivity(intent);
    }

    private void aboutApp() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    private void displayHomeUpOrHamburger() {
        boolean upBtn = getSupportFragmentManager().getBackStackEntryCount() > 0;

        if (upBtn) {
            //cant swipe left to open drawer
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //remove hamburger
            drawerToggle.setDrawerIndicatorEnabled(false);
            //need listener for up btn
            if (!mToolBarNavigationListenerIsRegistered) {
                drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Show hamburger
            drawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            drawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    private void firebaseAnalytics(){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        //Sets whether analytics collection is enabled for this app on this device.
        //firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        //firebaseAnalytics.setMinimumSessionDuration(5000);
        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(1000000);
    }

    private void loadAdMobBannerAd() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        adCardView = (CardView) findViewById(R.id.cv_ads);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adCardView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adCardView.setVisibility(View.VISIBLE);
            }

        });

        adCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "AD Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessageFromServer(){
        if(!Config.app_message.equals("0")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Message");
            alertDialog.setMessage(Config.app_message);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        }
    }

}