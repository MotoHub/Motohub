package online.motohub.adapter.club;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.fragment.club.ClubEventsFragment;
import online.motohub.fragment.club.ClubHomeFragment;
import online.motohub.fragment.club.ClubPhotosFragment;
import online.motohub.fragment.club.ClubSubscribedUsersFragment;
import online.motohub.fragment.club.ClubVideosFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class ClubViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();
    private Context ctx;

    public ClubViewPagerAdapter(FragmentManager fm, Context ctx, PromotersResModel promotersResModel,
                                ProfileResModel profileResModel) {
        super(fm);
        this.ctx = ctx;

        /*Bundle mBundle = new Bundle();
        mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, promotersResModel);
        mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, profileResModel);*/
        /*MotoHub.getApplicationInstance().setmProfileResModel(profileResModel);
        MotoHub.getApplicationInstance().setmPromoterResModel(promotersResModel);*/
        EventBus.getDefault().postSticky(profileResModel);
        EventBus.getDefault().postSticky(promotersResModel);

        ClubHomeFragment mClubHomeFragment = new ClubHomeFragment();
        //mClubHomeFragment.setArguments(mBundle);
        this.mFragments.add(mClubHomeFragment);

        ClubEventsFragment mClubEventsFragment = new ClubEventsFragment();
        //mClubEventsFragment.setArguments(mBundle);
        this.mFragments.add(mClubEventsFragment);

        ClubPhotosFragment mClubPhotosFragment = new ClubPhotosFragment();
        //mClubPhotosFragment.setArguments(mBundle);
        this.mFragments.add(mClubPhotosFragment);

        ClubVideosFragment mClubVideosFragment = new ClubVideosFragment();
        //mClubVideosFragment.setArguments(mBundle);
        this.mFragments.add(mClubVideosFragment);

        ClubSubscribedUsersFragment mClubUsersFragment = new ClubSubscribedUsersFragment();
        //mClubUsersFragment.setArguments(mBundle);
        this.mFragments.add(mClubUsersFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return ctx.getResources().getString(R.string.home);
            case 1:
                return ctx.getResources().getString(R.string.events);
            case 2:
                return ctx.getResources().getString(R.string.photos);
            case 3:
                return ctx.getResources().getString(R.string.videos);
            case 4:
                return ctx.getResources().getString(R.string.users);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


}