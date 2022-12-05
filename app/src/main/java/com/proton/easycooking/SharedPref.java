package com.proton.easycooking;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    private static final String USER_AGE = "USER_AGE";
    private final SharedPreferences sharedPreferences;
    private Context context;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH, flag).apply();
    }

    public int getUserAge() {
        return sharedPreferences.getInt(USER_AGE, 0);
    }

    public void setUserAge(int age) {
        sharedPreferences.edit().putInt(USER_AGE, age).apply();
    }


}
