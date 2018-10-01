package online.motohub.fragment.club;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ProfileVideoGalleryActivity;
import online.motohub.activity.ViewSpecLiveActivity;
import online.motohub.adapter.GalleryVideoAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.fragment.promoter.PromoterVideosFragment;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.RecyclerClick_Listener;
import online.motohub.util.RecyclerTouchListener;

public class ClubVideosFragment extends BaseFragment {

    public static final String TAG = PromoterVideosFragment.class.getSimpleName();

    @BindView(R.id.rv_videos)
    RecyclerView mRv;
    @BindView(R.id.search_edt)
    EditText searchEdt;
    @BindView(R.id.txt_no_data)
    TextView txtNoData;
    private ArrayList<GalleryVideoResModel> videoResModels;
    /*final GalleryVideoAdapter.OnItemClickListener onItemClickListener = new GalleryVideoAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(GalleryVideoResModel model, int position) {
            if (model != null) {
                Intent mAutoVideoIntent = new Intent(getActivity(), ViewSpecLiveActivity.class);
                mAutoVideoIntent.putParcelableArrayListExtra(AppConstants.VIDEOLIST, videoResModels);
                mAutoVideoIntent.putExtra(AppConstants.POSITION, position);
                mAutoVideoIntent.putExtra(AppConstants.TAG, TAG);
                startActivity(mAutoVideoIntent);
            }
        }
    };*/
    private GalleryVideoAdapter mAdapter;
    private PromotersResModel mClubResModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_promotor_videos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {

        mClubResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mRv.setLayoutManager(layoutManager);

        videoResModels = new ArrayList<>();
        mAdapter = new GalleryVideoAdapter(getActivity(), videoResModels);
        mRv.setAdapter(mAdapter);

        //mAdapter.setOnItemClickListener(onItemClickListener);

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    filter(s.toString());
                }

            }
        });

        mRv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRv, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {

                GalleryVideoResModel model = videoResModels.get(position);
                if (model != null) {
                    Intent mAutoVideoIntent = new Intent(getActivity(), ViewSpecLiveActivity.class);
                    mAutoVideoIntent.putParcelableArrayListExtra(AppConstants.VIDEOLIST, videoResModels);
                    mAutoVideoIntent.putExtra(AppConstants.POSITION, position);
                    mAutoVideoIntent.putExtra(AppConstants.TAG, TAG);
                    startActivity(mAutoVideoIntent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
            }
        }));
    }

    private void getVideoDataFromAPi() {
        String mFilter = "(ProfileID=" + mClubResModel.getUserId() + ") AND (" + APIConstants.UserType + "=club)";
        RetrofitClient.getRetrofitInstance()
                .getPromoterVideoGallery((BaseActivity) getActivity(),
                        mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.GET_VIDEO_FILE_RESPONSE:
                GalleryVideoModel videoModel = (GalleryVideoModel) responseObj;
                if (videoModel != null && videoModel.getResModelList().size() > 0) {
                    videoResModels.clear();
                    videoResModels.addAll(videoModel.getResModelList());
                    mAdapter.notifyDataSetChanged();
                } else {
                    ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.video_not_found));
                    mRv.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    searchEdt.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && videoResModels.size() == 0)
            getVideoDataFromAPi();
    }

    private void filter(String text) {
        ArrayList<GalleryVideoResModel> temp = new ArrayList();
        for (GalleryVideoResModel d : videoResModels) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getCaption().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        if (temp.size() > 0) {
            mAdapter.filterList(temp);
            txtNoData.setVisibility(View.GONE);
            mRv.setVisibility(View.VISIBLE);
            searchEdt.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            mRv.setVisibility(View.GONE);
        }
    }
}
