package online.motohub.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.OnDemanAudoVideoView;
import online.motohub.activity.PromoterVideoGalleryActivity;
import online.motohub.activity.club.ClubProfileActivity;
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity;
import online.motohub.activity.promoter.PromoterProfileActivity;
import online.motohub.activity.promoter.PromotersListActivity;
import online.motohub.activity.track.TrackProfileActivity;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
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
public class OnDemandNewAdapter extends RecyclerView.Adapter<OnDemandNewAdapter.ViewHolder> {

    private final List<PromoterVideoModel.Resource> mPostsList = new ArrayList<>();
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
//            DialogManager.showRetryAlertDialogWithCallback(mContext, mCommonInterface, mContext.getString(R.string.internet_err));
        }
    };

    public OnDemandNewAdapter(Context context, List<OndemandNewResponse> dataList, int profileId, ProfileResModel profileResModel) {
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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final OndemandNewResponse mOnDemandResponse = mDataList.get(position);

            if (mOnDemandResponse.getProfilePicture() != null)
                ((BaseActivity) mContext).setImageWithGlide(holder.imageURL, mOnDemandResponse.getProfilePicture(), R.drawable.default_profile_icon);
            holder.txt_event_name.setText(String.valueOf(mOnDemandResponse.getName()));
            holder.txt_no_videos.setText("No of videos: " + String.valueOf(mOnDemandResponse.getCount()));

            mPromoterFollowerList = mMyProfileResModel.getPromoterFollowerByProfileID();
            /*if (!mOnDemandResponse.getType().equals("Events")) {
                holder.followBtn.setVisibility(View.VISIBLE);
                if (isAlreadyFollowed(position, mPromoterFollowerList)) {
                    holder.followBtn.setText(mContext.getString(R.string.un_follow));
                } else {
                    holder.followBtn.setText(mContext.getString(R.string.follow));
                }
                holder.followBtn.setTag(position);
            } else {*/
            holder.followBtn.setVisibility(View.GONE);
            //}

            holder.btn_watch.postDelayed(new Runnable() {
                public void run() {
                    holder.loader.setVisibility(View.GONE);
                    holder.btn_watch.setVisibility(View.VISIBLE);
                }
            }, 3000);

            holder.btn_watch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = 0;
                    String whichId = "", mFilter = "";
                    if (mOnDemandResponse.getProfileID() != 0) {
                        id = mOnDemandResponse.getProfileID();
                        whichId = "ProfileID";
                    } else if (mOnDemandResponse.getUserID() != 0) {
                        id = mOnDemandResponse.getUserID();
                        whichId = "UserID";
                    } else if (mOnDemandResponse.getEventID() != 0) {
                        id = mOnDemandResponse.getEventID();
                        whichId = "EventID";
                    }
                    if (whichId.equals("EventID")) {
                        mFilter = whichId + " = " + id;
                    } else {
                        mFilter = "(EventFinishDate<" + getCurrentDate() + ") AND (UserType != user) AND (" + whichId + " = " + id + ")";
                    }
                    getVideoDataFromAPi(mFilter, position);

                }
            });

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = 0;
                    String whichId = "", mFilter = "";
                    if (mOnDemandResponse.getProfileID() != 0) {
                        id = mOnDemandResponse.getProfileID();
                        whichId = "ProfileID";
                    } else if (mOnDemandResponse.getUserID() != 0) {
                        id = mOnDemandResponse.getUserID();
                        whichId = "UserID";
                    } else if (mOnDemandResponse.getEventID() != 0) {
                        id = mOnDemandResponse.getEventID();
                        whichId = "EventID";
                    }
                    Bundle mBundle = new Bundle();
                    mBundle.putInt(AppConstants.PROFILE_ID, profileId);
                    mBundle.putInt("ID", mOnDemandResponse.getEventID());
                    mBundle.putString("TYPE", "EventID");
                    mContext.startActivity(new Intent(mContext, PromoterVideoGalleryActivity.class).putExtras(mBundle));
                }
            });

        /*holder.imageURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnDemandResponse.getType().equals(AppConstants.ONDEMAND)) {
                    if (mOnDemandResponse.getProfileID() == profileId) {
                        ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext,
                                profileId, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        ((BaseActivity) mContext).moveOtherProfileScreen(mContext, profileId,
                                mOnDemandResponse.getProfileID());
                    }
                } else {
                    navigationToProfileActivity(position);
                }

            }
        });

        holder.txt_event_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDemandResponse.getType().equals(AppConstants.ONDEMAND)) {
                    if (mOnDemandResponse.getProfileID() == profileId) {
                        ((BaseActivity) mContext).moveMyProfileScreenWithResult(mContext,
                                profileId, AppConstants.FOLLOWERS_FOLLOWING_RESULT);
                    } else {
                        ((BaseActivity) mContext).moveOtherProfileScreen(mContext, profileId,
                                mOnDemandResponse.getProfileID());
                    }
                } else if (!mOnDemandResponse.getType().equals(AppConstants.EVENTS)) {
                    navigationToProfileActivity(position);
                }
            }
        });*/

            holder.followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAlreadyFollowed(position, mMyProfileResModel.getPromoterFollowerByProfileID())) {
                        callUnFollowPromoter(position);
                    } else {
                        callFollowPromoter(position);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getVideoDataFromAPi(String mFilter, int position) {
        int mOffset = 0;
        callGetPromotersGalleryAdapter(mContext, mFilter, mOffset, position);
    }

    public String getCurrentDate() {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return mDateFormat.format(new Date());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private void navigationToProfileActivity(int layoutPosition) {
        Bundle mBundle = new Bundle();
        String mUserType = mDataList.get(layoutPosition).getType().trim();
        if (mDataList.get(layoutPosition).getUserID() != 0) {
            Class mClassName;
            switch (mUserType) {
                case PromotersModel.NEWS_AND_MEDIA:
                    mClassName = NewsAndMediaProfileActivity.class;
                    break;
                case PromotersModel.CLUB:
                    mClassName = ClubProfileActivity.class;
                    break;
                case PromotersModel.PROMOTER:
                    mClassName = PromoterProfileActivity.class;
                    break;
                case PromotersModel.TRACK:
                    mClassName = TrackProfileActivity.class;
                    break;
                default:
                    mClassName = PromoterProfileActivity.class;
                    break;
            }
            mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, mDataList.get(layoutPosition).getUserID());
            mBundle.putSerializable(ProfileModel.MY_PROFILE_RES_MODEL, mMyProfileResModel);
            ((BaseActivity) mContext).startActivityForResult(
                    new Intent(mContext, mClassName).putExtras(mBundle),
                    PromotersListActivity.PROMOTER_FOLLOW_RESPONSE);
        }
    }

    private boolean isAlreadyFollowed(int pos, ArrayList<PromoterFollowerResModel> mPromoterFollowerList) {
        boolean isAlreadyFollowed = false;
        for (PromoterFollowerResModel mPromoterFollowerResModel : mPromoterFollowerList) {
            if (mDataList.get(pos).getUserID() == mPromoterFollowerResModel.getPromoterUserID()) {
                isAlreadyFollowed = true;
                break;
            }
        }
        return isAlreadyFollowed;
    }

    private void callUnFollowPromoter(int position) {
        String mFilter = "FollowRelation=" + mMyProfileResModel.getID() + "_" + mDataList.get(position).getUserID();
        RetrofitClient.getRetrofitInstance().callUnFollowPromoter(mContext, mRetrofitResInterface, mFilter, RetrofitClient.GET_PROMOTER_UN_FOLLOW_RESPONSE);
    }

    private void callFollowPromoter(int position) {

        String mFollowRelation = mMyProfileResModel.getID() + "_" + mDataList.get(position).getUserID();
        JsonArray mJsonArray = new JsonArray();
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(PromoterFollowerResModel.PROFILE_ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(PromoterFollowerResModel.PROMOTER_USER_ID, mDataList.get(position).getUserID());
        mJsonObject.addProperty(PromoterFollowerResModel.FOLLOW_RELATION, mFollowRelation);

        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callFollowPromoter(mContext, mRetrofitResInterface, mJsonArray, RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE);
    }

    private void setResult() {
        ((BaseActivity) mContext).setResult(RESULT_OK, new Intent().putExtra(AppConstants.IS_FOLLOW_RESULT, true)
                .putExtra(AppConstants.MY_PROFILE_OBJ, mMyProfileResModel));
    }

    public void callGetPromotersGalleryAdapter(final Context context, String filter, int mOffset, final int Position) {
        int mLimit = 10;
        String fields = "*";
        DialogManager.showProgress(context);
        String mOrderBy = "CreatedAt DESC";

        RetrofitClient.getRetrofitInstance().getRetrofitApiInterface().callGetPromotersVideos(fields, filter,
                APIConstants.mPromoterGalleryRelated, mOrderBy, mLimit, mOffset, true)
                .enqueue(new Callback<PromoterVideoModel>() {
                    @Override
                    public void onResponse(Call<PromoterVideoModel> call, Response<PromoterVideoModel> response) {
                        DialogManager.hideProgress();
                        if (response.isSuccessful()) {
                            PromoterVideoModel mResponse = (PromoterVideoModel) response.body();
                            if (mResponse != null && mResponse.getResource().size() > 0) {
                                mPostsList.addAll(mResponse.getResource());
                                if (mPostsList.size() > 0) {
                                    Bundle mBundle = new Bundle();
                                    mBundle.putSerializable(AppConstants.ONDEMAND_DATA, (Serializable) mPostsList);
                                    //mBundle.putSerializable(AppConstants.ONDEMAND_DATA, convertModelToList());
                                    mBundle.putInt("ID", profileId);
                                    mBundle.putInt(AppConstants.POSITION, Position);
                                    mContext.startActivity(new Intent(mContext, OnDemanAudoVideoView.class).putExtras(mBundle));
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

    private ArrayList<HashMap<String, String>> convertModelToList() {
        ArrayList<HashMap<String, String>> mVideoList = new ArrayList<>();
        for (PromoterVideoModel.Resource mResource : mPostsList) {
            HashMap<String, String> mMapData = new HashMap<>();
            mMapData.put(AppConstants.VIDEO_PATH, mResource.getVideoUrl());
            mMapData.put(AppConstants.CAPTION, mResource.getCaption());
            mVideoList.add(mMapData);
        }
        return mVideoList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageURL;
        TextView txt_event_name, followBtn, txt_no_videos, btn_watch;
        SpinKitView loader;
        CardView root;

        public ViewHolder(View v) {
            super(v);
            imageURL = v.findViewById(R.id.imageURL);
            txt_event_name = v.findViewById(R.id.txt_event_name);
            followBtn = v.findViewById(R.id.followBtn);
            txt_no_videos = v.findViewById(R.id.txt_no_videos);
            btn_watch = v.findViewById(R.id.btn_watch);
            loader = v.findViewById(R.id.loader);
            root = v.findViewById(R.id.root);
        }
    }
}
