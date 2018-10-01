package online.motohub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.util.ZoomImageView;


public class PicturePreviewActivity extends BaseActivity {

    @BindView(R.id.image)
    ZoomImageView mImageView;

    @BindView(R.id.toolbar_back_img_btn)
    ImageButton mBackBtn;

    private Uri mPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        ButterKnife.bind(this);
        initialise();
        setImageWithGlide(mImageView, mPath);
    }

    private void initialise() {
        mBackBtn.setVisibility(View.VISIBLE);
        mPath = getIntent().getParcelableExtra("file_uri");
    }

    @OnClick({R.id.btn_cancel, R.id.btn_next, R.id.toolbar_back_img_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                nextScreen();
                break;
            default:
                finish();
        }
    }

    private void nextScreen() {
        Intent intent = new Intent(this, StorySettingActivity.class);
        intent.putExtra("file_uri", mPath);
        intent.putExtra("file_type", "image");
        Bundle mBundle = getIntent().getExtras().getBundle("bundle_data");
        if (mBundle != null)
            intent.putExtra("bundle_data", mBundle);
        startActivity(intent);
        finish();
    }
}
