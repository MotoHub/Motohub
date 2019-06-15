package online.motohub.adapter.business;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import online.motohub.R;
import online.motohub.constants.AppConstants;

public class BusinessViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private Context ctx;
    private String mUserType;

    public BusinessViewPagerAdapter(FragmentManager fm, Context ctx, ArrayList<Fragment> fragmentList, String userType) {
        super(fm);
        this.ctx = ctx;
        this.mFragmentList = fragmentList;
        this.mUserType = userType;
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
                return ctx.getResources().getString(R.string.photos);
            case 3:
                return ctx.getResources().getString(R.string.videos);
            case 4:
                if (mUserType.equals(AppConstants.CLUB))
                    return ctx.getString(R.string.clubs);
                else if (mUserType.equals(AppConstants.SHOP))
                    return ctx.getString(R.string.vehicles);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}
