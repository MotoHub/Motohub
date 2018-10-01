package online.motohub.fragment.Performance_Shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.GalleryImgAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryImgResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.DialogManager;
import online.motohub.util.RecyclerClick_Listener;
import online.motohub.util.RecyclerTouchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfPhotosFragment extends BaseFragment {

    @BindView(R.id.rv_videos_perf1)
    RecyclerView mGalleryRv;
    @BindView(R.id.txt_no_data_perf1)
    TextView txtNoData;

    private GalleryImgAdapter mAdapter;
    private List<GalleryImgResModel> mGalleryResModels;
    private PromotersResModel mPromotersResModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perf_photos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initRV();
    }

    private void initRV() {

        try {
            mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        } catch (NullPointerException e) {
            e.printStackTrace();
            mPromotersResModel = null;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mGalleryRv.setLayoutManager(layoutManager);

        mGalleryResModels = new ArrayList<>();
        mAdapter = new GalleryImgAdapter(getActivity(), mGalleryResModels);
        mGalleryRv.setAdapter(mAdapter);
       // mAdapter.setOnItemClickListener(mOnItemClickListener);

        mGalleryRv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mGalleryRv, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                ((BaseActivity) getActivity()).moveLoadImageScreen(getActivity(), mGalleryResModels.get(position).getGalleryImage());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

   /* private final GalleryImgAdapter.OnItemClickListener mOnItemClickListener = new GalleryImgAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            ((BaseActivity) getActivity()).moveLoadImageScreen(getActivity(), mGalleryResModels.get(position).getGalleryImage());
        }
    };*/

    private void getGalleryImages() {
        String mFilter = "(UserID=" + mPromotersResModel.getUserId() + ") AND (" + APIConstants.UserType + "=shop)";

        RetrofitClient.getRetrofitInstance().callGetImageGallery((BaseActivity) getActivity(), mFilter,
                RetrofitClient.GET_GALLERY_DATA_RESPONSE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        switch (responseType) {
            case RetrofitClient.GET_GALLERY_DATA_RESPONSE:
                GalleryImgModel model = (GalleryImgModel) responseObj;
                if (model.getGalleryResModelList() != null && model.getGalleryResModelList().size() > 0) {
                    mGalleryResModels.clear();
                    mGalleryResModels.addAll(model.getGalleryResModelList());
                    mAdapter.notifyDataSetChanged();
                } else {
                    ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.picture_not_found));
                    mGalleryRv.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mGalleryResModels.size() == 0)
            getGalleryImages();
    }

}