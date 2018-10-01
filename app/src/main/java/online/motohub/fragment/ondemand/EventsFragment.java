package online.motohub.fragment.ondemand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.adapter.OnDemandNewAdapter;
import online.motohub.application.MotoHub;
import online.motohub.fragment.BaseFragment;
import online.motohub.interfaces.OnLoadMoreListener;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.retrofit.RetrofitClient;

/**
 * Created by pickzy01 on 30/05/2018.
 */

public class EventsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public OnDemandNewAdapter adapter;
    @BindView(R.id.search_edt)
    EditText searchEdt;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    Unbinder unbinder;
    @BindView(R.id.parent)
    LinearLayout parent;
    private ArrayList<OndemandNewResponse> mListOndemand;
    private int mCurrentProfileID = 0;
    private ProfileResModel mMyProfileResModel;
    private String api_key;
    private LinearLayoutManager mLinearLayoutManager;
    //LoadMore
    private boolean isLoading;
    private int mFeedTotalCount = 0;
    private String mSearchStr = "";
    private int mOffset = 0;
    private boolean mIsLoadMore = false;
    private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mIsLoadMore = true;
            mListOndemand.add(null);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(mListOndemand.size() - 1);
                }
            });
            mOffset = mListOndemand.size() - 1;
            //Show loading item
            if (mSearchStr.isEmpty()) {
                //getVideoDataFromAPi(mFilter);
            } else {
                //getSearchDataFromAPi(mFilter);
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View obj = inflater.inflate(R.layout.events_fragment, container, false);
        unbinder = ButterKnife.bind(this, obj);
        initView();
        return obj;
    }

    private void initView() {
        mListOndemand = new ArrayList<>();
        // SwipeRefreshLayout
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorOrange,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeContainer.post(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                // Fetching data from server
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        api_key = getResources().getString(R.string.dream_factory_api_key);
        mCurrentProfileID = MotoHub.getApplicationInstance().getProfileId();

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int mVisibleItemCount = mLinearLayoutManager.getChildCount();
                int mTotalItemCount = mLinearLayoutManager.getItemCount();
                int mFirstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (mOffset < mFeedTotalCount)) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0) {
                        isLoading = true;
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                }
            }
        });*/

        getMyProfiles();
    }

    private void getMyProfiles() {
        String mFilter = "ID =" + mCurrentProfileID;
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else {
            showSnackBar(parent, getActivity().getResources().getString(R.string.internet_err));
        }
    }

    private void callApi() {
        RetrofitClient.getRetrofitInstance().callGetOndemandList(this, api_key, RetrofitClient.CALL_GET_NOTIFICATIONS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        getMyProfiles();
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new OnDemandNewAdapter(getActivity(), mListOndemand, mCurrentProfileID, mMyProfileResModel);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void retrofitOnResponse(ArrayList<OndemandNewResponse> responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        swipeContainer.setRefreshing(false);
        if (RetrofitClient.CALL_GET_NOTIFICATIONS == responseType) {
            mListOndemand.addAll(responseObj);
            setAdapter();
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof ProfileModel) {
            ProfileModel mProfileModel = (ProfileModel) responseObj;
            String data = new Gson().toJson(responseObj);
            if (mProfileModel.getResource().size() > 0) {
                mMyProfileResModel = mProfileModel.getResource().get(0);
                callApi();
            } else {
                showSnackBar(parent, getActivity().getResources().getString(R.string.no_profile_found_err));
            }
        }
    }

    @Override
    public void retrofitOnFailure(int code) {
        super.retrofitOnFailure(code);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }
}
