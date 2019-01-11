package online.motohub.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference Utility Singleton Class, has Shared Preference Keys and Methods.
 *
 * @version 1.0, 06/03/2017
 * @since 1.0
 */
public class PreferenceUtils {

    //SharedPreferences Key's
    public static final String IS_NOT_FIRST_LAUNCH = "IS_NOT_FIRST_LAUNCH";
    public static final String USER_KEEP_LOGGED_IN = "user_keep_logged_in";
    public static final String USER_PROFILE_COMPLETED = "user_profile_completed";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String SESSION_TOKEN = "session_token";
    public static final String CURRENT_PROFILE_POS = "current_profile_id";

    public static final String IS_CONTACT_PERMISSION_ADDED = "IS_CONTACT_PERMISSION_ADDED";

    //In-App Keys
    public static final String BIKE_IN_APP = "bike_in_app";
    public static final String BOAT_IN_APP = "boat_in_app";
    public static final String CAR_IN_APP = "car_in_app";
    public static final String KART_IN_APP = "kart_in_app";
    public static final String USER_EMAIL = "user_email";
    public static final String IS_PERMANATLY_DENIED_CONTACT_PERMISSION = "is_permanently _denied";
    public static final String BUSINESS_PROFILE_COMPLETED = "BUSINESS_PROFILE_COMPLETED";
    public static final String CURRENT_PROFILE_MODEL = "CURRENT_PROFILE_MODEL";
    public static final String FULL_PROFILE_LIST = "FULL_PROFILE_LIST";

    private static PreferenceUtils mPreferenceUtils = new PreferenceUtils();

    private static SharedPreferences mSharedPrefInstance;
    public static String ALLOW_NOTIFICATION="allow_notification";
    public static String ALLOW_NOTIFICATION_Sound="allow_sound";
    public static String ALLOW_NOTIFICATION_VIB="allow_vib";

    public static final String IS_TUTORIAL_FIRST = "IS_TUTORIAL_FIRST";
    public static final String IS_JOB_SCHEDULER = "IS_JOB_SCHEDULER";

    /**
     * PreferenceUtils default constructor.
     */
    private PreferenceUtils() {
    }

    /**
     * Gets PreferenceUtils Instance.
     *
     * @param ctx context of the calling Activity.
     * @return mPreferenceUtils new instance if null, returns existing instance if not null.
     */
    public static PreferenceUtils getInstance(Context ctx) {
        if (mSharedPrefInstance == null) {
            mSharedPrefInstance = ctx.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        }
        return mPreferenceUtils;
    }

    /**
     * Saves string data to Shared Preferences.
     *
     * @param key   Shared Preference key.
     * @param value Shared Preference value.
     */
    public void saveStrData(String key, String value) {
        mSharedPrefInstance.edit().putString(key, value).apply();
    }

    /**
     * Saves int data to Shared Preferences.
     *
     * @param key   Shared Preference key.
     * @param value Shared Preference value.
     */
    public void saveIntData(String key, int value) {
        mSharedPrefInstance.edit().putInt(key, value).apply();
    }

    /**
     * Saves boolean data to Shared Preferences.
     *
     * @param key   Shared Preference key.
     * @param value Shared Preference value.
     */
    public void saveBooleanData(String key, boolean value) {
        mSharedPrefInstance.edit().putBoolean(key, value).apply();
    }

    /**
     * Gets String data from Shared Preferences.
     *
     * @param key Shared Preference key.
     * @return value from Shared Preference based on key.
     */
    public String getStrData(String key) {
        return mSharedPrefInstance.getString(key, "");
    }

    /**
     * Gets integer data from Shared Preferences.
     *
     * @param key Shared Preference key.
     * @return value from Shared Preference based on key.
     */
    public int getIntData(String key) {
        return mSharedPrefInstance.getInt(key, 0);
    }

    /**
     * Gets data from Shared Preferences.
     *
     * @param key Shared Preference key.
     * @return value from Shared Preference based on key.
     */
    public boolean getBooleanData(String key) {
        return mSharedPrefInstance.getBoolean(key, false);
    }

    /**
     * Clears Data's stored in Shared Preferences.
     */
    public void clearData() {
        mSharedPrefInstance.edit().clear().apply();
    }

}
