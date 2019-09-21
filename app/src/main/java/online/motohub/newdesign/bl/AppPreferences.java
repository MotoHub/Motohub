package online.motohub.newdesign.bl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import online.motohub.interfaces.UserPreferences;


public class AppPreferences implements UserPreferences {

    private final SharedPreferences sharedPreferences;

    AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    //We need to do the commit the pref since it will be used immediately on the next screen.
    public void putString(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    @SuppressLint("ApplySharedPref")
    //We need to do the commit the pref since it will be used immediately on the next screen.
    public void putInt(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    @SuppressLint("ApplySharedPref")
    //We need to do the commit the pref since it will be used immediately on the next screen.
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    @Override
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public Integer getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }


    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void removeString(String key) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }
}
