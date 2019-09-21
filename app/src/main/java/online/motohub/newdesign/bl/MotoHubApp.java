package online.motohub.newdesign.bl;

import android.content.Context;

import java.io.File;

import online.motohub.newdesign.constants.AppConstants;
import online.motohub.newdesign.constants.ConfigConstants;
import online.motohub.interfaces.UserPreferenceCallback;
import online.motohub.interfaces.UserPreferences;

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
        public UserPreferences userPreferences;
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

    }


    public ApiClient apiClients;
    public UserPreferences userPreferences;
    public ConnectivityLiveData internetUtil;
    public MHFileCacheImplementor fileCacheImplementor;
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
        String url= ConfigConstants.getBaseUrl();
//        if (TextUtils.isEmpty(configuration.baseURL)) {
//            url = ConfigConstants.getBaseUrl();
//        } else {
//            url = configuration.baseURL;
//        }

        MotoHubApp mh = getInstance();
        Context context = configuration.context;

        if (configuration.userPreferences == null) {
            mh.userPreferences = new AppPreferences(context);
        } else {
            mh.userPreferences = configuration.userPreferences;
        }
        File fileCacheDir = new File(context.getFilesDir(), AppConstants.FILE_CACHE_FOLDER_NAME);
        mh.fileCacheImplementor = new MHFileCacheImplementor(fileCacheDir);
        mh.apiClients = new ApiClient(url, mh.userPreferences, mh.fileCacheImplementor);
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
