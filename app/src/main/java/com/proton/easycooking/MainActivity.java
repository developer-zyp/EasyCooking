package com.proton.easycooking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.proton.easycooking.fragments.CategoryFragment;
import com.proton.easycooking.fragments.FavoriteFragment;
import com.proton.easycooking.fragments.HomeFragment;
import com.proton.easycooking.fragments.NoEatTogetherFragment;
import com.proton.easycooking.tabs.TabCaloriesCalcFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private boolean toolBarNavigationListenerIsRegistered = false;
    private boolean doubleBackToExitPressedOnce = false;

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For night mode theme
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //For day mode theme
        setContentView(R.layout.activity_main);
        FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(this);

        initToolbar();
        initNavigationDrawer();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this::displayHomeUpOrHamburger);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        CardView adCardView = findViewById(R.id.cv_ad_main);
        AdMob.showBannerAds(this, "admob", adCardView, AdSize.BANNER);

        showMessageFromServer();
        checkUpdate();

    }

    private void initToolbar() {
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // This will display an Up icon (<-), we will replace it with hamburger later
        ActionBar actionBar = getSupportActionBar();
        // add back arrow to toolbar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView nav_View = (NavigationView) findViewById(R.id.nav_view);

        nav_View.setNavigationItemSelectedListener(item -> {
            menuItemClick(item);
            new Handler().postDelayed(() -> {
                drawerLayout.closeDrawers();
            }, 200);
            return true;
        });

        Menu nav_Menu = nav_View.getMenu();
        if (BuildConfig.DEBUG) {
            nav_Menu.findItem(R.id.nav_admin).setVisible(true);
        }

    }

    private void menuItemClick(MenuItem item) {
        if (!item.isChecked()) {
            switch (item.getItemId()) {
                case R.id.nav_today_special:
//                    TodaySpecialFragment todaySpecialFragment = new TodaySpecialFragment();
//                    fragmentManager.beginTransaction().replace(R.id.fragment_container, todaySpecialFragment).commit();
//                    setTitle(item.getTitle());
                    HomeFragment homeFragment = new HomeFragment();
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container, homeFragment).commit();
                    setTitle(item.getTitle());
                    break;
                case R.id.nav_category:
                    CategoryFragment categoryFragment = new CategoryFragment(1);
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container, categoryFragment).commit();
                    setTitle(item.getTitle());
                    break;
                case R.id.nav_calories_cal:
                    TabCaloriesCalcFragment caloriesCalcFragment = new TabCaloriesCalcFragment();
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container, caloriesCalcFragment).commit();
                    setTitle(item.getTitle());
                    break;
                case R.id.nav_never_eat:
                    NoEatTogetherFragment noEatTogetherFragment = new NoEatTogetherFragment();
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container, noEatTogetherFragment).commit();
                    setTitle(item.getTitle());
                    break;
                case R.id.nav_favorite:
                    FavoriteFragment favoriteFragment = new FavoriteFragment();
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container, favoriteFragment).commit();
                    setTitle(item.getTitle());
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
                case R.id.nav_admin:
                    adminApp();
                    break;
                default:
                    break;
            }

        }
    }

    private void displayHomeUpOrHamburger() {
        boolean upBtn = getSupportFragmentManager().getBackStackEntryCount() > 0;

        if (upBtn) {
            //cant swipe left to open drawer
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //remove hamburger
            drawerToggle.setDrawerIndicatorEnabled(false);
            //need listener for up btn
            if (!toolBarNavigationListenerIsRegistered) {
                drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                });

                toolBarNavigationListenerIsRegistered = true;
            }
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Show hamburger
            drawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            drawerToggle.setToolbarNavigationClickListener(null);
            toolBarNavigationListenerIsRegistered = false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
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

            if (AppConfig.ENABLE_EXIT_DIALOG) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.drawable.ic_launcher);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage("Are you sure want to exit!");
                dialog.setPositiveButton("Yes", (dialogInterface, i) -> MainActivity.this.finish());

                dialog.setNeutralButton("Feedback", (dialogInterface, i) -> {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException exception) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    }

                    //MainActivity.this.finish();
                });

//                dialog.setNeutralButton("More Apps", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/dev?id=" + Config.DEVELOPER_ID)));
//                        //MainActivity.this.finish();
//                    }
//                });
                dialog.show();

            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                AppTools.showToast(MainActivity.this, "Tap again to exit");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
//                super.onBackPressed();
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

    private void adminApp() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    private void checkUpdate() {
        String[] message = AppConfig.show_message.split(";");
        String show = message.length > 0 ? message[0].trim() : "";
        String version = BuildConfig.VERSION_NAME;
        if (!version.equals(AppConfig.app_version) && show.equals("true")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setIcon(R.drawable.ic_launcher);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(R.string.new_version);
            dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException exception) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    }

                }
            });

            dialog.setNeutralButton("Later", (dialogInterface, i) -> {
            });
            dialog.show();
        }
    }

    private void showMessageFromServer() {
        String[] message = AppConfig.show_message.split(";");
        String show = message.length > 1 ? message[1].trim() : "";
        if (show.equals("true")) {
            AppTools.showAlertDialog(MainActivity.this, "Message", AppConfig.app_message);
        }
    }


}