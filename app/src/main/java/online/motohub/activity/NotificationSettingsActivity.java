package online.motohub.activity;


import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.dialog.DialogManager;
import online.motohub.util.PreferenceUtils;

public class NotificationSettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        setToolbar(mToolbar, "Settings");
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        Switch allow_notification = findViewById(R.id.switch_allow);
        final Switch allow_sound = findViewById(R.id.switch_sound);
        final Switch allow_vib = findViewById(R.id.switch_vib);
        boolean allow_noticiation_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION);
        boolean allow_sound_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound);
        boolean allow_vib_status = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB);
        if (allow_sound_status) {
            allow_sound.setChecked(true);
        } else {
            allow_sound.setChecked(false);
        }
        if (allow_vib_status) {
            allow_vib.setChecked(true);
        } else {
            allow_vib.setChecked(false);
        }
        if (allow_noticiation_status) {
            allow_notification.setChecked(true);
        } else {
            allow_sound.setEnabled(false);
            allow_vib.setEnabled(false);
            allow_notification.setChecked(false);
        }

        allow_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.getInstance(NotificationSettingsActivity.this).saveBooleanData(PreferenceUtils.ALLOW_NOTIFICATION, isChecked);
                if (!isChecked) {
                    allow_sound.setEnabled(false);
                    allow_vib.setEnabled(false);
                } else {
                    allow_sound.setEnabled(true);
                    allow_vib.setEnabled(true);
                }
            }
        });
        allow_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.getInstance(NotificationSettingsActivity.this).saveBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_Sound, isChecked);

            }
        });
        allow_vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.getInstance(NotificationSettingsActivity.this).saveBooleanData(PreferenceUtils.ALLOW_NOTIFICATION_VIB, isChecked);

            }
        });
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }
}

