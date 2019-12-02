package online.motohub.fragment.newsandmedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ViewSpecLiveActivity;
import online.motohub.adapter.GalleryVideoAdapter;
import online.motohub.fragment.BaseFragment;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.RecyclerClick_Listener;
import online.motohub.util.RecyclerTouchListener;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NewsandMediaVideosFragment extends BaseFragment {

    public static final String TAG = NewsandMediaVideosFragment.class.getSimpleName();
    private static String mSearchStr = "";
    public boolean mRefresh = true;
    @BindView(R.id.rv_videos_news1)
    RecyclerView mRv;
    @BindView(R.id.search_edt_videos1)
    EditText searchEdt;
    @BindView(R.id.txt_no_data_news1)
    TextView txtNoData;
    @BindView(R.id.parent)
    LinearLayout mParent;
    GridLayoutManager layoutManager;
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
    private rx.Subscription subscription;
    private GalleryVideoAdapter mAdapter;
    private PromotersResModel mClubResModel;
    private Unbinder mUnBinder;
    private Activity mActivity;
    private boolean Isvisible = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mRefresh = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newsand_media_videos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        setupUI(mParent);
        initView();
    }

    private void initView() {
        //mClubResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        //mClubResModel = MotoHub.getApplicationInstance().getmPromoterResModel();
        mClubResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);
        videoResModels = new ArrayList<>();
        mAdapter = new GalleryVideoAdapter(getActivity(), videoResModels);
        mRv.setAdapter(mAdapter);
        //  mAdapter.setOnItemClickListener(onItemClickListener);
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
/*        searchEdt.addTextChangedListener(new TextWatcher() {
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
                } else {
                    mAdapter.filterList(videoResModels);
                }
            }
        });*/
        subscription = RxTextView.textChanges(searchEdt)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        SystemClock.sleep(1000); // Simulate the heavy stuff.
                        return true;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        String mSearchstr = charSequence.toString();
                        findvideos(mSearchstr);
                    }
                });
        /*mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = layoutManager.getChildCount();
                int mTotalItemCount = layoutManager.getItemCount();
                int mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!mIsPostsRvLoading && !(mPostsRvOffset >= mPostsRvTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        mIsPostsRvLoading = true;
                        if (videoResModels.size() > 0)
                            getVideoDataFromAPi();
                    }
                }
            }
        });*/
    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    public void onRefresh() {
        setRefresh(true);
        //getVideoDataFromAPi();
    }

    private void getVideoDataFromAPi() {
        String mFilter = "(UserID=" + mClubResModel.getUserId() + ") AND ((" + APIConstants.UserType + "=newsmedia) OR ("
                + APIConstants.UserType + " = " + AppConstants.USER_EVENT_VIDEOS + "))";
        /*ApiClient.getRetrofitInstance()
                .getPromoterVideoGallery1((BaseActivity) getActivity(),
                        mFilter, ApiClient.GET_VIDEO_FILE_RESPONSE, mDataLimit, mPostsRvOffset);*/
        RetrofitClient.getRetrofitInstance()
                .getPromoterVideoGallery((BaseActivity) getActivity(), mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE);
    }

    private void findvideos(String searchstr) {
        mSearchStr = searchstr;
        if ((mSearchStr.trim().isEmpty() || mSearchStr.length() == 0) && Isvisible) {
            getVideoDataFromAPi();
        } else if (Isvisible) {
            videoResModels.clear();
            searchVideoDataFromApi();
        }
    }

    private void searchVideoDataFromApi() {
        String mFilter = "(UserID=" + mClubResModel.getUserId() + ") AND (Caption like '%" + mSearchStr + "%') AND ((" + APIConstants.UserType + "=newsmedia) OR ("
                + APIConstants.UserType + " = " + AppConstants.USER_EVENT_VIDEOS + "))";
        /*ApiClient.getRetrofitInstance().getPromoterVideoGallery1((BaseActivity) getActivity(),
                mFilter, ApiClient.SEARCH_VIDEO_FILE_RESPONSE, mDataLimit, mPostsRvOffset);*/
        RetrofitClient.getRetrofitInstance()
                .getPromoterVideoGallery((BaseActivity) getActivity(), mFilter, RetrofitClient.SEARCH_VIDEO_FILE_RESPONSE);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        GalleryVideoModel videoModel = (GalleryVideoModel) responseObj;
        switch (responseType) {
            case RetrofitClient.GET_VIDEO_FILE_RESPONSE:
                mRefresh = false;
                Isvisible = true;
                if (videoModel != null && videoModel.getResource().size() > 0) {
                    videoResModels.clear();
                    videoResModels.addAll(videoModel.getResource());
                    mRv.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.video_not_found));
                    mRv.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    searchEdt.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case RetrofitClient.SEARCH_VIDEO_FILE_RESPONSE:
                mRefresh = false;
                if (videoModel != null && videoModel.getResource().size() > 0) {
                    videoResModels.clear();
                    videoResModels.addAll(videoModel.getResource());
                    mRv.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.video_not_found));
                    mRv.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
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
            //use .toLowerCase() for better matches or use .equal(text) with you want equal match
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