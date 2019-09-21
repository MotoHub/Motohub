package online.motohub.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.model.TutorialEntity;
import online.motohub.dialog.DialogManager;
import online.motohub.newdesign.activity.LoginActivity;
import online.motohub.util.PreferenceUtils;


public class TutorialScreen extends BaseActivity {

    @BindView(R.id.tutorial_parent)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.left_arrow)
    ImageView mLeftArrow;
    @BindView(R.id.right_arrow)
    ImageView mRightArrow;
    @BindView(R.id.get_started_btn)
    Button mGetStartedBtn;
    @BindView(R.id.dot_lay)
    LinearLayout mDotsLay;
    private List<ImageView> dots;
    private boolean isTutorial = false;
    private CustomPagerAdapter mPageradapter;
    private ArrayList<TutorialEntity> mSlidesList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_screen);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {
        isTutorial = PreferenceUtils.getInstance(this).getBooleanData(PreferenceUtils.IS_TUTORIAL_FIRST);
        setToolbar(mToolbar, getString(R.string.moto_tutorial));
        if (!isTutorial) {
            mGetStartedBtn.setVisibility(View.VISIBLE);
        } else {
            mGetStartedBtn.setVisibility(View.GONE);
            showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);
        }
        setList();
        mLeftArrow.setVisibility(View.GONE);
        addDots();
        mPageradapter = new CustomPagerAdapter(getApplicationContext());
        mViewPager.setAdapter(mPageradapter);
        mPageradapter.notifyDataSetChanged();

        mViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        selectDot(position);
                        if (mViewPager.getCurrentItem() == 0) {
                            mLeftArrow.setVisibility(View.GONE);
                        } else {
                            mLeftArrow.setVisibility(View.VISIBLE);
                        }
                        if ((mViewPager.getCurrentItem() == mSlidesList.size() - 1)) {
                            mRightArrow.setVisibility(View.GONE);
                        } else {
                            mRightArrow.setVisibility(View.VISIBLE);
                        }
                        if (position == mSlidesList.size() - 1) {
                            mGetStartedBtn.setText(getString(R.string.get_started));
                        } else {
                            mGetStartedBtn.setText(getString(R.string.skip));
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

//        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void setList() {
        mSlidesList.add(getTutorialEntity(getString(R.string.t1), R.drawable.t1));
        mSlidesList.add(getTutorialEntity(getString(R.string.t2), R.drawable.t2));
        mSlidesList.add(getTutorialEntity(getString(R.string.t3), R.drawable.t3));
        mSlidesList.add(getTutorialEntity(getString(R.string.t4), R.drawable.t4));
        mSlidesList.add(getTutorialEntity(getString(R.string.t5), R.drawable.t5));
        mSlidesList.add(getTutorialEntity(getString(R.string.t6), R.drawable.t6));
        mSlidesList.add(getTutorialEntity(getString(R.string.t7), R.drawable.t7));

    }

    private TutorialEntity getTutorialEntity(String mContent, int mContentImg) {
        TutorialEntity mEntity = new TutorialEntity();
        mEntity.setContent(mContent);
        mEntity.setContentImg(mContentImg);
        return mEntity;
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        if (mSlidesList != null && mSlidesList.size() > 0) {
            for (int i = 0; i < mSlidesList.size(); i++) {
                int drawableId = (i == idx) ? (R.drawable.checked)
                        : (R.drawable.unchecked);
                Drawable drawable = res.getDrawable(drawableId);
                dots.get(i).setImageDrawable(drawable);
            }
        }
    }

    public void addDots() {
        dots = new ArrayList<>();

        if (mSlidesList != null && mSlidesList.size() > 0) {
            for (int i = 0; i < mSlidesList.size(); i++) {
                ImageView dot = new ImageView(this);

                if (i == 0) {
                    dot.setImageDrawable(getResources().getDrawable(
                            R.drawable.checked));
                } else {
                    dot.setImageDrawable(getResources().getDrawable(
                            R.drawable.unchecked));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 5, 5, 5);
                mDotsLay.addView(dot, params);
                dots.add(dot);

            }
        }


    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.left_arrow, R.id.right_arrow, R.id.get_started_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;
            case R.id.left_arrow:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.right_arrow:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            case R.id.get_started_btn:
                PreferenceUtils.getInstance(getApplicationContext()).saveBooleanData(PreferenceUtils.IS_TUTORIAL_FIRST, true);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    class CustomPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mSlidesList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_get_started,
                    container, false);

            TextView mTitleTxt;

            final ImageView mSlideImg = itemView
                    .findViewById(R.id.slide_image);
            mTitleTxt = itemView.findViewById(R.id.title_txt);

            mSlideImg.setImageResource(mSlidesList.get(position).getContentImg());
            mTitleTxt.setText(mSlidesList.get(position).getContent());

            container.addView(itemView);
            return itemView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }
}

