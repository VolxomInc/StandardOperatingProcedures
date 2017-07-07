package com.gmpsop.standardoperatingprocedures.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gmpsop.standardoperatingprocedures.Models.ApplicationUser;

/**
 * Created by BD1 on 05-May-17.
 */

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "GMPSOP";
    private static final String KEY_IS_FIRST_TIME = "isFirstTime";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_FULL_NAME = "userFullName";
    private static final String USER_NAME = "userName";
    private static final String USER_SUBSCRIPTION = "subscription";
    private static final String USER_PASSWORD = "userPassword";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstRun(boolean isFirstTime) {
        editor.putBoolean(KEY_IS_FIRST_TIME, isFirstTime);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public void setUserDetails(ApplicationUser user) {
        editor.putString(USER_EMAIL, user.getEmail());
        editor.putString(USER_FULL_NAME, user.getFullName());
        editor.putString(USER_NAME, user.getUserName());
        editor.putString(USER_PASSWORD, user.getPassword());
        editor.putInt(USER_SUBSCRIPTION, user.getSubscription());
        editor.commit();
    }

    public ApplicationUser getUserDetail() {
        ApplicationUser appo = new ApplicationUser();
        appo.setEmail(pref.getString(USER_EMAIL, ""));
        appo.setFullName(pref.getString(USER_FULL_NAME, ""));
        appo.setUserName(pref.getString(USER_NAME, ""));
        appo.setPassword(pref.getString(USER_PASSWORD, ""));
        appo.setSubscription(pref.getInt(USER_SUBSCRIPTION, 0));
        return appo;
    }

    public boolean isFirstTime() {
        return pref.getBoolean(KEY_IS_FIRST_TIME, true);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getSubsription() {
        return pref.getInt(USER_SUBSCRIPTION, 0);
    }

    public void setSubsription(int sub) {
        editor.putInt(USER_SUBSCRIPTION, sub);
        editor.commit();

        Log.d(TAG, "Subscription Modified");
    }
}
