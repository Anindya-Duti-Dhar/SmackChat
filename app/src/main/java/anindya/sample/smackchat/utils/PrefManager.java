package anindya.sample.smackchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PrefManager {
    SharedPreferences pref;
    Editor editor;
    Context mContext;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "webhawksit";

    // user login data record
    private static final String PREFERENCE_USER_LOGGED_DATA = "user_logged_data";
    private static final String APPLICATION_USER_LOGGED_DATA = "ap_user_logged_data";

    // user login session record
    private static final String PREFERENCE_USER_LOGGED_IN = "user_logged_in";
    private static final String APPLICATION_USER_LOGGED_IN = "ap_user_logged_in";

    //update and get user preference
    private static final String PREFERENCE_USER_NAME = "user_name";
    private static final String APPLICATION_USER_NAME = "ap_user_name";

    private static final String PREFERENCE_USER_FULL_NAME = "user_full_name";
    private static final String APPLICATION_USER_FULL_NAME = "ap_user_full_name";

    private static final String PREFERENCE_USER_PASSWORD = "user_password";
    private static final String APPLICATION_USER_PASSWORD = "ap_user_password";

    private static final String PREFERENCE_USER_PHOTO = "user_photo";
    private static final String APPLICATION_USER_PHOTO = "ap_user_photo";

    private static final String PREFERENCE_USER_GENDER = "user_gender";
    private static final String APPLICATION_USER_GENDER = "ap_user_gender";

    private static final String PREFERENCE_USER_ADDRESS = "user_address";
    private static final String APPLICATION_USER_ADDRESS = "ap_user_address";


    public PrefManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static void setUserLoggedData(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_LOGGED_DATA, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_LOGGED_DATA, user);
        editor.commit();
    }

    public static String getUserLoggedData(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_LOGGED_DATA, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_LOGGED_DATA, "");
    }

    public static void setUserLoggedIn(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_LOGGED_IN, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_LOGGED_IN, user);
        editor.commit();
    }

    public static String getUserLoggedIn(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_LOGGED_IN, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_LOGGED_IN, "");
    }


    public static void setUserName(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_NAME, user);
        editor.commit();
    }

    public static String getUserName(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_NAME, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_NAME, "");
    }

    public static void setUserFullName(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_FULL_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_FULL_NAME, user);
        editor.commit();
    }

    public static String getUserFullName(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_FULL_NAME, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_FULL_NAME, "");
    }

    public static void setUserPassword(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_PASSWORD, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_PASSWORD, user);
        editor.commit();
    }

    public static String getUserPassword(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_PASSWORD, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_PASSWORD, "");
    }

    public static void setUserPhoto(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_PHOTO, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_PHOTO, user);
        editor.commit();
    }

    public static String getUserPhoto(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_PHOTO, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_PHOTO, "");
    }

    public static void setUserGender(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_GENDER, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_GENDER, user);
        editor.commit();
    }

    public static String getUserGender(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_GENDER, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_GENDER, "");
    }

    public static void setUserAddress(final Context ctx, final String user) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_ADDRESS, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PrefManager.APPLICATION_USER_ADDRESS, user);
        editor.commit();
    }

    public static String getUserAddress(final Context ctx) {
        return ctx.getSharedPreferences(
                PrefManager.PREFERENCE_USER_ADDRESS, Context.MODE_PRIVATE)
                .getString(PrefManager.APPLICATION_USER_ADDRESS, "");
    }

}
