package online.motohub.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.fragment.Performance_Shop.PerfEventsFragment;
import online.motohub.fragment.Performance_Shop.PerfNewsFeedFragment;
import online.motohub.fragment.Performance_Shop.PerfPhotosFragment;
import online.motohub.fragment.Performance_Shop.PerfVideosFragment;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class PerfProfileTabPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private Context ctx;

    public PerfProfileTabPagerAdapter(Context ctx, FragmentManager fm, ProfileResModel mProfileResModel, PromotersResModel mPromoterResModel) {
        super(fm);
        this.ctx = ctx;

        Bundle mTrackBundle = new Bundle();
        mTrackBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mProfileResModel);
        mTrackBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPromoterResModel);

        PerfNewsFeedFragment mPerfNewsFeedFragment = new PerfNewsFeedFragment();
        mPerfNewsFeedFragment.setArguments(mTrackBundle);

        PerfEventsFragment mPerfEventsFragment = new PerfEventsFragment();
        mPerfEventsFragment.setArguments(mTrackBundle);

        PerfPhotosFragment mPerfPhotosFragment = new PerfPhotosFragment();
        mPerfPhotosFragment.setArguments(mTrackBundle);

        PerfVideosFragment mPerfVideosFragment = new PerfVideosFragment();
        mPerfVideosFragment.setArguments(mTrackBundle);

        mFragmentList.add(mPerfNewsFeedFragment);
        mFragmentList.add(mPerfEventsFragment);
        mFragmentList.add(mPerfPhotosFragment);
        mFragmentList.add(mPerfVideosFragment);

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return ctx.getResources().getString(R.string.home);
            case 1:
                return ctx.getResources().getString(R.string.events);
            case 2:
                return ctx.getResources().getString(R.string.track_profile_photos);
            case 3:
                return ctx.getResources().getString(R.string.track_profile_videos);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}