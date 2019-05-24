package online.motohub.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.greenrobot.eventbus.EventBus;

import online.motohub.fragment.ChatGroupFragment;
import online.motohub.fragment.ChatSingleFragment;
import online.motohub.model.ProfileResModel;

public class ChatTabPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments = new Fragment[2];
    private ProfileResModel mMyProfileModel;

    public ChatTabPagerAdapter(FragmentManager fm, ProfileResModel myProfileModel) {
        super(fm);
        this.mMyProfileModel = myProfileModel;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(mFragments[position] == null) {
                    setFragment(new ChatSingleFragment(), position);
                }
                return mFragments[position];
            case 1:
                if(mFragments[position] == null) {
                    setFragment(new ChatGroupFragment(), position);
                }
                return mFragments[position];
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    private void setFragment(Fragment mFragment, int position) {
        //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileModel);
        EventBus.getDefault().postSticky(mMyProfileModel);
        Bundle mBundle = new Bundle();
        //mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileModel);
        mFragment.setArguments(mBundle);
        this.mFragments[position] = mFragment;
    }

}
