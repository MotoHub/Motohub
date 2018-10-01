package online.motohub.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import online.motohub.R;
import online.motohub.model.LocalVideoModel;

public class VideoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private LocalVideoModel mLocalVideoModel;

    private VideoView mVideoView;
    private ImageView mImageView;
    private MediaController mMediaController;
    private View mPlayBtnImgView;

    public static VideoFragment newInstance(LocalVideoModel videoModel) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, videoModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocalVideoModel = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mVideoView = v.findViewById(R.id.video_fragment_video_view);
        mPlayBtnImgView = v.findViewById(R.id.play_btn_image_view);
        mImageView = v.findViewById(R.id.video_fragment_thumbnail_image_view);
        mImageView.setOnClickListener(this);

        if(mLocalVideoModel.getThumbnail() != null) {
            mImageView.setImageBitmap(mLocalVideoModel.getThumbnail());
        } else {
            mImageView.setImageResource(R.drawable.video_place_holder);
        }
        mMediaController = new MediaController(getContext());
        mMediaController.setAnchorView(mVideoView);

        mVideoView.setVideoURI(mLocalVideoModel.getVideoFile());
        mVideoView.setMediaController(mMediaController);

        mVideoView.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (!isVisibleToUser) {
            if (mVideoView != null && mVideoView.isPlaying()) {
                mVideoView.pause();
                mMediaController.setSystemUiVisibility(View.INVISIBLE);
            }
        } else {
            if (mVideoView != null && mVideoView.isActivated()) {
                mVideoView.resume();
                mMediaController.setSystemUiVisibility(View.VISIBLE);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_fragment_thumbnail_image_view:
            case R.id.play_btn_image_view:
                mVideoView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);
                mPlayBtnImgView.setVisibility(View.GONE);
                mVideoView.start();
                break;
            case R.id.video_fragment_video_view:

                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                } else {
                    if (mVideoView.isActivated()) {
                        mVideoView.resume();
                    } else {
                        mVideoView.start();
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
