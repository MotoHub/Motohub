package online.motohub.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.model.ImageModel;
import online.motohub.util.UrlUtils;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ViewImageActivity extends BaseActivity {

    @BindView(R.id.photo_view)
    ImageView mPhotoView;

    @BindView(R.id.smallProgressBar)
    ProgressBar mPostPicProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);
        setImage();

    }

    private void setImage() {
        String mImageUrl;
        mImageUrl = getIntent().getStringExtra(ImageModel.POST_IMAGE);

        mPostPicProgressBar.setVisibility(View.VISIBLE);

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + mImageUrl, new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(this)
                .load(glideUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mPostPicProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mPostPicProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .dontAnimate()
                )
                .into(mPhotoView);

        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mPhotoView);

    }

    @OnClick(R.id.close_btn)
    public void onClick(View v){
        switch (v.getId()){
            case R.id.close_btn:
                finish();
                break;
        }

    }
}
