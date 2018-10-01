package online.motohub.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import online.motohub.fragment.VideoFragment;
import online.motohub.model.LocalVideoModel;

public class VideoPagerAdapter extends FragmentStatePagerAdapter {

    private List<LocalVideoModel> videoModels;

    public VideoPagerAdapter(FragmentManager fm, List<LocalVideoModel> videoModels) {
        super(fm);
        this.videoModels = videoModels;
    }

    @Override
    public Fragment getItem(int position) {
        return VideoFragment.newInstance(videoModels.get(position));
    }

    @Override
    public int getCount() {
        return videoModels.size();
    }
}
