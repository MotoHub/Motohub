package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.model.ProfileResModel;
import online.motohub.util.AppConstants;

import static android.view.View.VISIBLE;

public class ReportWritePostActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_img)
    CircleImageView mMPImg;
    @BindView(R.id.name_of_moto_tv)
    TextView mNameOfMotoTv;
    @BindView(R.id.name_of_driver_tv)
    TextView mNameOfDriverTv;
    @BindView(R.id.write_post_et)
    EditText mWritePostEt;
    @BindView(R.id.toolbar_back_img_btn)
    ImageButton toolBack;
    @BindString(R.string.report_reason)
    String mToolbarTitle;
    @BindView(R.id.reportWritePostLayout)
    RelativeLayout mReportWritePostLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_write_post);
        ButterKnife.bind(this);
        setToolbar(mToolbar, mToolbarTitle);
        initView();
    }

    private void initView() {
        setupUI(mReportWritePostLayout);
        toolBack.setVisibility(VISIBLE);
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.submitBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
            case R.id.submitBtn:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(AppConstants.REPORT_STRING,mWritePostEt.getText().toString());
                setResult(RESULT_OK,resultIntent);
                finish();
                break;
        }
    }

}
