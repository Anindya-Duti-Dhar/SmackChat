package base.droidtool.dtlib;

import android.content.SharedPreferences;

import base.droidtool.DroidTool;


public class Pref {

    int PRIVATE_MODE = 0;// shared pref mode
    private static final String PREF_NAME = "SmackPref";     // Shared preferences file name
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DroidTool dt;

    public Pref(DroidTool droidTool) {
        dt = droidTool;
    }

    public void set(String key, boolean value) {
        editor = dt.c.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void set(String key, String value) {
        editor = dt.c.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void set(String key, int value) {
        editor = dt.c.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        pref = dt.c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getBoolean(key, false);
    }

    public String getString(String key) {
        pref = dt.c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(key, "");
    }

    public int getInt(String key) {
        pref = dt.c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getInt(key, 0);
    }


}
