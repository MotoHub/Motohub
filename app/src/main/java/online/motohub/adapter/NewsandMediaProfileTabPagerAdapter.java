package online.motohub.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.fragment.newsandmedia.NewsandMediaEventsFragment;
import online.motohub.fragment.newsandmedia.NewsandMediaFeedFragment;
import online.motohub.fragment.newsandmedia.NewsandMediaPhotosFragment;
import online.motohub.fragment.newsandmedia.NewsandMediaVideosFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class NewsandMediaProfileTabPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private Context ctx;

    public NewsandMediaProfileTabPagerAdapter(Context ctx, FragmentManager fm, ProfileResModel mProfileResModel, PromotersResModel mPromoterResModel) {
        super(fm);
        this.ctx = ctx;

        /*Bundle mTrackBundle = new Bundle();
        mTrackBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mProfileResModel);
        mTrackBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPromoterResModel);*/
        /*MotoHub.getApplicationInstance().setmProfileResModel(mProfileResModel);
        MotoHub.getApplicationInstance().setmPromoterResModel(mPromoterResModel);*/
        EventBus.getDefault().postSticky(mProfileResModel);
        EventBus.getDefault().postSticky(mPromoterResModel);

        NewsandMediaFeedFragment mNewsandMediaFeedFragment = new NewsandMediaFeedFragment();
        //mNewsandMediaFeedFragment.setArguments(mTrackBundle);

        NewsandMediaEventsFragment mNewsandMediaEventsFragment = new NewsandMediaEventsFragment();
        //mNewsandMediaEventsFragment.setArguments(mTrackBundle);

        NewsandMediaPhotosFragment mNewsandMediaPhotosFragment = new NewsandMediaPhotosFragment();
        //mNewsandMediaPhotosFragment.setArguments(mTrackBundle);

        NewsandMediaVideosFragment mNewsandMediaVideosFragment = new NewsandMediaVideosFragment();
        //mNewsandMediaVideosFragment.setArguments(mTrackBundle);

        mFragmentList.add(mNewsandMediaFeedFragment);
        mFragmentList.add(mNewsandMediaEventsFragment);
        mFragmentList.add(mNewsandMediaPhotosFragment);
        mFragmentList.add(mNewsandMediaVideosFragment);

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