package online.motohub.activity.promoter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.dialog.DialogManager;
import online.motohub.util.UrlUtils;
import online.motohub.util.ZoomImageView;


public class PromoterGalleryViewPager extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view_pager);
        ButterKnife.bind(this);
        ViewPager viewPager = findViewById(R.id.promoter_viewpager);
        setToolbar(mToolbar, "Photos");
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        CustomPromoterAdapter customPromoterAdapter = new CustomPromoterAdapter(this, getIntent().getStringArrayExtra("img"));
        viewPager.setAdapter(customPromoterAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("pos", 0));

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

    private class CustomPromoterAdapter extends PagerAdapter {

        private final Context mContext;
        private final String[] mImgUriList;
        private final LayoutInflater mInflater;


        CustomPromoterAdapter(Context context, String[] imgUriList) {
            mContext = context;
            mImgUriList = imgUriList;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public Object instantiateItem(ViewGroup parent, int pos) {
            View convertView = mInflater.inflate(R.layout.row_picker_img_pager_thumbnail, parent, false);
            ZoomImageView mImageView = convertView.findViewById(R.id.row_picker_img_pager_thumbnail_image_view);
            /*if (mImgUriList.length == 1) {
                setImageWithGlide(mImageView, mImgUriList[pos], R.drawable.img_place_holder);
            } else {*/
                /*GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + mImgUriList[pos], new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", getString(R.string.dream_factory_api_key))
                        .build());*/
            Glide.with(getApplicationContext())
                    .load(UrlUtils.AWS_S3_BASE_URL + mImgUriList[pos])
                    .apply(new RequestOptions()
                            .error(R.drawable.img_place_holder)
                            .dontAnimate()
                    )
                    .into(mImageView);
            //}
            parent.addView(convertView);
            return convertView;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((FrameLayout) view);
        }

        @Override
        public int getCount() {
            return mImgUriList.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
