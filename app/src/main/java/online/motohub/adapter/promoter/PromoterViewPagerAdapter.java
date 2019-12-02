package online.motohub.adapter.promoter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.fragment.promoter.PromoterEventsFragment;
import online.motohub.fragment.promoter.PromoterHomeFragment;
import online.motohub.fragment.promoter.PromoterPhotosFragment;
import online.motohub.fragment.promoter.PromoterVideosFragment;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class PromoterViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();
    private Context ctx;

    public PromoterViewPagerAdapter(FragmentManager fm, Context ctx, PromotersResModel promotersResModel, ProfileResModel profileResModel) {
        super(fm);
        this.ctx = ctx;
        /*Bundle mBundle = new Bundle();
        mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, promotersResModel);
        mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, profileResModel);*/
        /*MotoHub.getApplicationInstance().setmProfileResModel(profileResModel);
        MotoHub.getApplicationInstance().setmPromoterResModel(promotersResModel);*/
        EventBus.getDefault().postSticky(profileResModel);
        EventBus.getDefault().postSticky(promotersResModel);
        PromoterHomeFragment mPromoterHomeFragment = new PromoterHomeFragment();
        //mPromoterHomeFragment.setArguments(mBundle);
        this.mFragments.add(mPromoterHomeFragment);
        PromoterEventsFragment mPromoterEventsFragment = new PromoterEventsFragment();
        //mPromoterEventsFragment.setArguments(mBundle);
        this.mFragments.add(mPromoterEventsFragment);
        PromoterPhotosFragment mPromoterPhotosFragment = new PromoterPhotosFragment();
        //mPromoterPhotosFragment.setArguments(mBundle);
        this.mFragments.add(mPromoterPhotosFragment);
        PromoterVideosFragment mPromoterVideosFragment = new PromoterVideosFragment();
        //mPromoterVideosFragment.setArguments(mBundle);
        this.mFragments.add(mPromoterVideosFragment);
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
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
