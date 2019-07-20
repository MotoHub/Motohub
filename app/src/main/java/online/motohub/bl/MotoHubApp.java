package online.motohub.bl;

import android.content.Context;

import online.motohub.interfaces.UserPreferenceCallback;
import online.motohub.interfaces.UserPreferences;
import online.motohub.retrofit.RetrofitClient;

/***
 * Stores access to all manager that will be used by Providers.
 * Can be improved by using DI or any other framework that can make this class obsolete
 */
public class MotoHubApp implements UserPreferenceCallback {

    /**
     * Allows for simple constructing of a {@code Configuration} object.
     */
    public static final class Builder {
        /**
         * Members of Builder
         */
        public Context context;
        public String baseURL;
        public String fileDir;
        public UserPreferences userPreferences;
        public String appName;
        public boolean testMode = false;

        /**
         * Initialize a builder with a given context.
         *
         * @param context The active {@link Context} for your application. Cannot be null.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new AssertionError("context cannot be null");
            }
            this.context = context;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public void setBaseURL(String baseURL) {
            this.baseURL = baseURL;
        }

        public void setFileDir(String fileDir) {
            this.fileDir = fileDir;
        }

        public void setUserPreferences(UserPreferences userPreferences) {
            this.userPreferences = userPreferences;
        }

        public void setTestMode(boolean testMode) {
            this.testMode = testMode;
        }
    }


    public RetrofitClient apiClients;
    public UserPreferences userPreferences;
    public String appName;
    public ConnectivityLiveData internetUtil;

    /**
     * Initialize the MotoHub app with given configuration
     */
    public static MotoHubApp initialize(Builder configuration) {
        if (configuration == null) {
            throw new AssertionError("Configuration cannot be null");
        }
        if (configuration.context == null) {
            throw new AssertionError("Context in Configuration cannot be null");
        }
        MotoHubApp mh = getInstance();
        Context context = configuration.context;
        mh.apiClients = new RetrofitClient();
        mh.internetUtil = new ConnectivityLiveData(context);
        return mh;
    }

    /**
     * Setting up singleton access
     */
    private static final Object LOCK = new Object();
    private static MotoHubApp instance;

    public static MotoHubApp getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new MotoHubApp();
                }
            }
        }
        return instance;
    }

    @Override
    public UserPreferences getUserPreference() {
        return getInstance().userPreferences;
    }

    public void onUserLogout() {
    }


}
