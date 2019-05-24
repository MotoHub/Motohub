package online.motohub.fragment.Business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import online.motohub.fragment.promoter.PromoterVideosFragment;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.RecyclerClick_Listener;
import online.motohub.util.RecyclerTouchListener;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class BusinessVideosFragment extends BaseFragment {

    public static final String TAG = PromoterVideosFragment.class.getSimpleName();
    @BindView(R.id.rv_videos)
    RecyclerView mRv;
    @BindView(R.id.search_edt)
    EditText searchEdt;
    @BindView(R.id.txt_no_data)
    TextView txtNoData;
    @BindView(R.id.parent)
    LinearLayout parent;
    private ArrayList<GalleryVideoResModel> videoResModels;
    private static String mSearchStr = "";

    private GalleryVideoAdapter mAdapter;
    private PromotersResModel mPromotersResModel;

    private Unbinder mUnBinder;
    private Activity mActivity;
    GridLayoutManager layoutManager;
    public boolean mRefresh = true;
    private boolean mIsPostsRvLoading = true;
    private static final int mDataLimit = 15;
    private int mPostsRvOffset = 0, mPostsRvTotalCount = 0;

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
        return inflater.inflate(R.layout.fragment_promotor_videos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        setupUI(parent);
        initView();
    }


    private void initView() {
        setupUI(parent);
        assert getArguments() != null;
        //mPromotersResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        //mPromotersResModel = MotoHub.getApplicationInstance().getmPromoterResModel();
        mPromotersResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);

        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);

        videoResModels = new ArrayList<>();
        mAdapter = new GalleryVideoAdapter(getActivity(), videoResModels);
        mRv.setAdapter(mAdapter);

        RxTextView.textChanges(searchEdt)
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

        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getVideoDataFromAPi();
                    }
                }
            }
        });
    }

    @Override
    public void setRefresh(boolean refresh) {
        super.setRefresh(refresh);
        this.mRefresh = refresh;
    }

    public void onRefresh() {
        setRefresh(true);
        mPostsRvOffset = 0;
        getVideoDataFromAPi();
    }

    private void getVideoDataFromAPi() {
        String mFilter = "(UserID=" + mPromotersResModel.getUserId() + ") AND ((" + APIConstants.UserType + "=" + mPromotersResModel.getUserType() + ") OR ("
                + APIConstants.UserType + " = " + AppConstants.USER_EVENT_VIDEOS + "))";
        RetrofitClient.getRetrofitInstance()
                .getPromoterVideoGallery1((BaseActivity) getActivity(),
                        mFilter, RetrofitClient.GET_VIDEO_FILE_RESPONSE,mDataLimit,mPostsRvOffset);
    }

    private void findvideos(String searchstr) {
        if (videoResModels.size() > 0) {
            mSearchStr = searchstr;
            mPostsRvOffset = 0;
            mIsPostsRvLoading = true;
            if (mSearchStr.trim().equals("")) {
                getVideoDataFromAPi();
            } else {
                searchVideoDataFromApi();
            }
        }
    }

    private void searchVideoDataFromApi() {
        String mFilter = "(UserID=" + mPromotersResModel.getUserId() + ") AND (Caption like '%" + mSearchStr + "%') AND ((" + APIConstants.UserType + "=promoter) OR ("
                + APIConstants.UserType + " = " + AppConstants.USER_EVENT_VIDEOS + "))";
        RetrofitClient.getRetrofitInstance().getPromoterVideoGallery1((BaseActivity) getActivity(),
                mFilter, RetrofitClient.SEARCH_VIDEO_FILE_RESPONSE, mDataLimit, mPostsRvOffset);
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        GalleryVideoModel videoModel = (GalleryVideoModel) responseObj;
        switch (responseType) {
            case RetrofitClient.GET_VIDEO_FILE_RESPONSE:
                mRefresh = false;
                if (videoModel != null && videoModel.getResModelList().size() > 0) {
                    mPostsRvTotalCount = videoModel.getMeta().getCount();
                    mIsPostsRvLoading = false;
                    if (mPostsRvOffset == 0) {
                        videoResModels.clear();
                    }
                    videoResModels.addAll(videoModel.getResModelList());
                    mPostsRvOffset = mPostsRvOffset + mDataLimit;
                } else {
                    ((BaseActivity) mActivity).showToast(getActivity(), getString(R.string.video_not_found));
                    if (mPostsRvOffset == 0) {
                        mPostsRvTotalCount = 0;
                    }
                    mRv.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    searchEdt.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case RetrofitClient.SEARCH_VIDEO_FILE_RESPONSE:
                mRefresh = false;
                if (videoModel != null && videoModel.getResModelList().size() > 0) {
                    mPostsRvTotalCount = videoModel.getMeta().getCount();
                    mIsPostsRvLoading = false;
                    if (mPostsRvOffset == 0) {
                        videoResModels.clear();
                    }
                    videoResModels.addAll(videoModel.getResModelList());
                    mPostsRvOffset = mPostsRvOffset + mDataLimit;
                    mRv.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    ((BaseActivity) mActivity).showToast(getActivity(), getString(R.string.video_not_found));
                    if (mPostsRvOffset == 0) {
                        mPostsRvTotalCount = 0;
                    }
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
        if (code == RetrofitClient.GET_VIDEO_FILE_RESPONSE){
            mPostsRvTotalCount = 0;
        }
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


    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View mInnerView = ((ViewGroup) view).getChildAt(i);
                setupUI(mInnerView);
            }
        }
    }

    public void hideSoftKeyboard(Activity mActivity) {
        try {
            if (mActivity != null && !mActivity.isFinishing()) {
                InputMethodManager mInputMethodManager = (InputMethodManager) mActivity
                        .getSystemService(INPUT_METHOD_SERVICE);
                if (mActivity.getCurrentFocus() != null
                        && mActivity.getCurrentFocus().getWindowToken() != null) {
                    assert mInputMethodManager != null;
                    mInputMethodManager.hideSoftInputFromWindow(mActivity
                            .getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}