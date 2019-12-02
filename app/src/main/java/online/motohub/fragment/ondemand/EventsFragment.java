package online.motohub.fragment.ondemand;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.application.MotoHub;
import online.motohub.fragment.BaseFragment;
import online.motohub.interfaces.OnLoadMoreListener;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.newdesign.adapter.OnDemandEventsAdapter;
import online.motohub.newdesign.constants.AppConstants;
import online.motohub.retrofit.RetrofitClient;

import static android.app.Activity.RESULT_OK;


/**
 * Created by pickzy01 on 30/05/2018.
 */

public class EventsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private final long DELAY = 600;
    public OnDemandEventsAdapter adapter;
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
    private Timer timer = new Timer();
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
        setupUI(parent);
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

        getMyProfiles();

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String searchStr = s.toString();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                searchOnDemand(searchStr);
                            }
                        });
                        //
                    }

                }, DELAY);
            }
        });

    }

    private void searchOnDemand(String searchStr) {
        try {
            mSearchStr = searchStr;
            ArrayList<OndemandNewResponse> temp = new ArrayList();
            for (OndemandNewResponse d : mListOndemand) {
                if (d.getName().toLowerCase().contains(mSearchStr.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (adapter != null) {
                if (temp.size() > 0) {
                    adapter.updateList(temp);
                    recyclerView.setVisibility(View.VISIBLE);
                } else if (temp.isEmpty() && !mSearchStr.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    showSnackBar(parent, getString(R.string.video_not_found));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMyProfiles() {
        String mFilter = "ID =" + mCurrentProfileID;
        if (isNetworkConnected())
            RetrofitClient.getRetrofitInstance().callGetProfiles(this, mFilter, RetrofitClient.GET_PROFILE_RESPONSE);
        else {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.parent), getActivity().getResources().getString(R.string.internet_err), Snackbar.LENGTH_LONG);
            snackbar.show();
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
        searchEdt.setText(null);
        //recyclerView.getRecycledViewPool().clear();
        callApi();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.ONDEMAND_REQUEST:
                    assert data.getExtras() != null;
                    /*DialogManager.showProgress(getActivity());
                    mPromoterVideoList.clear();*/
                    ArrayList<PromoterVideoModel.PromoterVideoResModel> mTempPromoterVideoList = (ArrayList<PromoterVideoModel.PromoterVideoResModel>) data.getExtras().getSerializable(AppConstants.VIDEO_LIST);
                    /*mPromoterVideoList.addAll(mTempPromoterVideoList);
                    mAdapter.notifyDataSetChanged();
                    DialogManager.hideProgress();*/
                    break;
            }
        }
    }

    private void setAdapter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Do whatever you want
                try {
                    if (adapter == null && mMyProfileResModel.getID() != 0) {
                        adapter = new OnDemandEventsAdapter(getActivity(), mListOndemand, mMyProfileResModel);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void retrofitOnResponse(ArrayList<OndemandNewResponse> responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        try {
            swipeContainer.setRefreshing(false);
            if (RetrofitClient.CALL_GET_NOTIFICATIONS == responseType) {
                mListOndemand.clear();
                mListOndemand.addAll(responseObj);
                setAdapter();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                //showSnackBar(parent, getActivity().getResources().getString(R.string.no_profile_found_err));
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.parent), getActivity().getResources().getString(R.string.no_profile_found_err), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    public void retrofitOnFailure(int code) {
        super.retrofitOnFailure(code);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
        swipeContainer.setRefreshing(false);
    }

}
