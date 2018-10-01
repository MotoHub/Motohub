package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import online.motohub.R;
import online.motohub.util.AppConstants;
import online.motohub.util.PreferenceUtils;

public class MainSplashScreenActivity extends BaseActivity {

    private static final String TAG = MainSplashScreenActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_launcher);
        //TODO REMOVE the below line Client need Tutorial
        PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_TUTORIAL_FIRST, true);

        boolean isNotFirstLaunch = PreferenceUtils.getInstance(MainSplashScreenActivity.this).getBooleanData(PreferenceUtils.IS_NOT_FIRST_LAUNCH);
        if (isNotFirstLaunch) {
            int mSecondsDelayed = 3;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    boolean mKeepMeLoggedIn = PreferenceUtils.getInstance(MainSplashScreenActivity.this).getBooleanData(PreferenceUtils.USER_KEEP_LOGGED_IN);
                    boolean mUserProfileCompleted = PreferenceUtils.getInstance(MainSplashScreenActivity.this).getBooleanData(PreferenceUtils.USER_PROFILE_COMPLETED);
                    boolean mIsTutorialFirst = PreferenceUtils.getInstance(MainSplashScreenActivity.this).getBooleanData(PreferenceUtils.IS_TUTORIAL_FIRST);
                    if (!mIsTutorialFirst) {
                        startActivity(new Intent(MainSplashScreenActivity.this, TutorialScreen.class));
                    } else {
                        if (mKeepMeLoggedIn) {
                            if (mUserProfileCompleted) {
                                startActivity(new Intent(MainSplashScreenActivity.this, ViewProfileActivity.class));
                            } else {
                                startActivity(new Intent(MainSplashScreenActivity.this, CreateProfileActivity.class)
                                        .putExtra(AppConstants.TAG, TAG));
                            }
                        } else {
                            startActivity(new Intent(MainSplashScreenActivity.this, LoginActivity.class));
                        }
                    }
                    finish();
                }
            }, mSecondsDelayed * 1000);
        } else {
            startActivity(new Intent(MainSplashScreenActivity.this, MotoVideoScreen.class));
            finish();
        }


    }


}
