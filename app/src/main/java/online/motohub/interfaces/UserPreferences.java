package online.motohub.interfaces;

public interface UserPreferences {

    void putString(String key, String value);

    void putInt(String key, Integer value);

    void putBoolean(String key, Boolean value);

    String getString(String key);

    String getString(String key, String defaultValue);

    Integer getInt(String key);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    void removeString(String key);

}
