package online.motohub.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.application.MotoHub;
import online.motohub.fragment.track.TrackContactFragment;
import online.motohub.fragment.track.TrackEventFragment;
import online.motohub.fragment.track.TrackNewsFeedFragment;
import online.motohub.fragment.track.TrackPhotosFragment;
import online.motohub.fragment.track.TrackVideosFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class TrackProfileTabPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private Context ctx;

    public TrackProfileTabPagerAdapter(Context ctx, FragmentManager fm, ProfileResModel mProfileResModel, PromotersResModel mPromoterResModel) {
        super(fm);
        this.ctx = ctx;

        //Bundle mTrackBundle = new Bundle();
        /*mTrackBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mProfileResModel);
        mTrackBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mPromoterResModel);*/
        /*MotoHub.getApplicationInstance().setmProfileResModel(mProfileResModel);
        MotoHub.getApplicationInstance().setmPromoterResModel(mPromoterResModel);*/
        EventBus.getDefault().postSticky(mProfileResModel);
        EventBus.getDefault().postSticky(mPromoterResModel);

        TrackEventFragment mTrackEventFragment = new TrackEventFragment();
        //mTrackEventFragment.setArguments(mTrackBundle);

        TrackContactFragment mTrackContactFragment = new TrackContactFragment();
        //mTrackContactFragment.setArguments(mTrackBundle);

        TrackNewsFeedFragment mTrackNewsFeedFragment = new TrackNewsFeedFragment();
        //mTrackNewsFeedFragment.setArguments(mTrackBundle);

        TrackPhotosFragment mTrackPhotosFragment = new TrackPhotosFragment();
        //mTrackPhotosFragment.setArguments(mTrackBundle);

        TrackVideosFragment mTrackVideosFragment = new TrackVideosFragment();
        //mTrackVideosFragment.setArguments(mTrackBundle);

        mFragmentList.add(mTrackNewsFeedFragment);
        mFragmentList.add(mTrackEventFragment);
        mFragmentList.add(mTrackPhotosFragment);
        mFragmentList.add(mTrackVideosFragment);
        mFragmentList.add(mTrackContactFragment);
    }

    @Override
    public Fragment getItem(int position) {return mFragmentList.get(position);}

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
            case 4:
                return ctx.getResources().getString(R.string.track_profile_contact);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}
