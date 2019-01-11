package online.motohub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.PromoterVideoGalleryActivity;
import online.motohub.activity.ondemand.EventVideosPlayingActivity;
import online.motohub.activity.ondemand.OnDemandActivity;
import online.motohub.application.MotoHub;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.AppConstants;
import online.motohub.util.DialogManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Author: pickzy01
 * Created on 24/05/2018
 */
public class OnDemandEventsAdapter extends RecyclerView.Adapter<OnDemandEventsAdapter.ViewHolder> {

    private final ArrayList<PromoterVideoModel.Resource> mPostsList = new ArrayList<>();
    private Context mContext;
    private List<OndemandNewResponse> mDataList;
    private int profileId;
    private ProfileResModel mMyProfileResModel;
    private ArrayList<PromoterFollowerResModel> mPromoterFollowerList = new ArrayList<>();
    private RetrofitResInterface mRetrofitResInterface = new RetrofitResInterface() {
        @Override
        public void retrofitOnResponse(Object responseObj, int responseType) {

            if (responseObj instanceof PromoterFollowerModel) {
                PromoterFollowerModel mPromoterFollowerModel = (PromoterFollowerModel) responseObj;
                switch (responseType) {
                    case RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE:
                        mPromoterFollowerList.add(mPromoterFollowerModel.getResource().get(0));
                        mMyProfileResModel.setPromoterFollowerByProfileID(mPromoterFollowerList);
                        notifyDataSetChanged();
                        break;
                    case RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE:
                        for (int i = 0; i < mPromoterFollowerList.size(); i++) {
                            if (mPromoterFollowerList.get(i).getID() == mPromoterFollowerModel.getResource().get(0).getID()) {
                                mPromoterFollowerList.remove(i);
                                break;
                            }
                        }
                        mMyProfileResModel.setPromoterFollowerByProfileID(mPromoterFollowerList);
                        notifyDataSetChanged();
                        break;
                }
                setResult();
            }
        }

        @Override
        public void retrofitOnError(int code, String message) {
            if (message.equals("Unauthorized") || code == 401) {
                RetrofitClient.getRetrofitInstance().callUpdateSession(mContext, mRetrofitResInterface, RetrofitClient.UPDATE_SESSION_RESPONSE);
            } else {
                ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
            }

        }

        @Override
        public void retrofitOnSessionError(int code, String message) {
            ((BaseActivity) mContext).retrofitOnSessionError(code, message);
        }

        @Override
        public void retrofitOnFailure() {
            ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.internet_err));
        }
    };

    public OnDemandEventsAdapter(Context context, List<OndemandNewResponse> dataList, int profileId, ProfileResModel profileResModel) {
        this.mContext = context;
        this.mDataList = dataList;
        this.profileId = profileId;
        this.mMyProfileResModel = profileResModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reportItemvView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_viewondemand, parent, false);
        return new ViewHolder(reportItemvView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final OndemandNewResponse mOnDemandResponse = mDataList.get(position);

            //if (mOnDemandResponse.getCoverImage() != null) {

            if (!mOnDemandResponse.getEventImage().equals("")) {
                if (mOnDemandResponse.getEventImage() != null && !mOnDemandResponse.getEventImage().equals(""))
                    ((BaseActivity) mContext)
                            .setCoverImageWithGlide(holder.image_cover, mOnDemandResponse.getEventImage(), R.drawable.default_cover_img);
            } else {
                if (mOnDemandResponse.getCoverImage() != null && !mOnDemandResponse.getCoverImage().equals(""))
                    ((BaseActivity) mContext)
                            .setCoverImageWithGlide(holder.image_cover, mOnDemandResponse.getCoverImage(), R.drawable.default_cover_img);
            }

            if (mOnDemandResponse.getProfilePicture() != null && !mOnDemandResponse.getProfilePicture().equals(""))
                ((BaseActivity) mContext).setImageWithGlide(holder.imageURL, mOnDemandResponse.getProfilePicture(), R.drawable.default_profile_icon);
            //}
            holder.txt_event_name.setText(String.valueOf(mOnDemandResponse.getName()));
            holder.txt_no_videos.setText("No of videos: " + String.valueOf(mOnDemandResponse.getCount()));
            mPromoterFollowerList = mMyProfileResModel.getPromoterFollowerByProfileID();
            if (mOnDemandResponse.getViewCount() == 0) {
                holder.view_count.setVisibility(View.GONE);
            } else {
                holder.view_count.setVisibility(View.VISIBLE);
                int count = mOnDemandResponse.getViewCount() * 11;
                String count_value = BaseActivity.convertToSuffix(Long.parseLong(String.valueOf(count)));
                holder.view_count.setText("View Count - " + count_value);
            }

            holder.btn_watch.postDelayed(new Runnable() {
                public void run() {
                    holder.loader.setVisibility(View.GONE);
                    holder.btn_watch.setVisibility(View.VISIBLE);
                }
            }, 3000);

            holder.btn_watch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPostsList.clear();
                    String mFilter;
                    if (mOnDemandResponse.getEventID() != 0) {
                        mFilter = "EventID" + " = " + mOnDemandResponse.getEventID();
                        getVideoDataFromAPi(mFilter, position);
                    }
                }
            });

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetwork()) {
                        mPostsList.clear();
                        Bundle mBundle = new Bundle();
                        mBundle.putInt(AppConstants.PROFILE_ID, profileId);
                        mBundle.putInt("ID", mOnDemandResponse.getEventID());
                        mBundle.putString("TYPE", "EventID");
                        //mBundle.putString("Filter", "EventID" + mOnDemandResponse.getEventID());
                        mContext.startActivity(new Intent(mContext, PromoterVideoGalleryActivity.class).putExtras(mBundle));
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVideoDataFromAPi(String mFilter, int position) {
        try {
            int mOffset = 0;
            if (isNetwork()) {
                callGetPromotersGalleryAdapter(mContext, mFilter, mOffset, position);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.internet_err), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentDate() {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return mDateFormat.format(new Date());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private void setResult() {
        ((BaseActivity) mContext).setResult(RESULT_OK, new Intent().putExtra(AppConstants.IS_FOLLOW_RESULT, true).putExtra(AppConstants.MY_PROFILE_OBJ, mMyProfileResModel));
    }

    private void callGetPromotersGalleryAdapter(final Context context, final String filter, int mOffset, final int Position) {
        String fields = "*";
        DialogManager.showProgress(context);
        int mLimit = 10;
        String mOrderBy = "CreatedAt ASC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideosForAdapter(fields, filter, APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            PromoterVideoModel mResponse = response.body();
                            if (mResponse != null && mResponse.getResource().size() > 0) {
                                mPostsList.addAll(mResponse.getResource());
                                if (mPostsList.size() > 0) {
                                    Bundle mBundle = new Bundle();
                                    mBundle.putSerializable(AppConstants.ONDEMAND_DATA, mPostsList);
                                    //mBundle.putSerializable(AppConstants.MY_PROFILE_OBJ, mMyProfileResModel);
                                    //MotoHub.getApplicationInstance().setmProfileResModel(mMyProfileResModel);
                                    EventBus.getDefault().postSticky(mMyProfileResModel);
                                    mBundle.putString("Filter", filter);
                                    if (mContext instanceof OnDemandActivity)
                                        ((OnDemandActivity) mContext).startActivityForResult(new Intent(mContext, EventVideosPlayingActivity.class).putExtras(mBundle), AppConstants.ONDEMAND_REQUEST);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoterVideoModel> call, Throwable t) {
                        DialogManager.hideProgress();
                    }
                });
    }

    public void updateList(List<OndemandNewResponse> list) {
        mDataList = list;
        this.notifyDataSetChanged();
    }

    private boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageURL;
        TextView txt_event_name, view_count, txt_no_videos, btn_watch;
        SpinKitView loader;
        CardView root;
        ImageView image_cover;

        public ViewHolder(View v) {
            super(v);
            imageURL = v.findViewById(R.id.imageURL);
            image_cover = v.findViewById(R.id.image_cover);
            txt_event_name = v.findViewById(R.id.txt_event_name);
            view_count = v.findViewById(R.id.view_count);
            txt_no_videos = v.findViewById(R.id.txt_no_videos);
            btn_watch = v.findViewById(R.id.btn_watch);
            loader = v.findViewById(R.id.loader);
            root = v.findViewById(R.id.root);
        }
    }
}
