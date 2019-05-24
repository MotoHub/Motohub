package online.motohub.util;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import online.motohub.activity.BaseActivity;
import online.motohub.application.MotoHub;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;
import online.motohub.retrofit.APIConstants;
import online.motohub.retrofit.RetrofitClient;

public class CommonAPI {

    public static CommonAPI getInstance() {

        return MotoHub.COMMON_API_INSTANCE;

    }

    public void callFollowProfile(Context mContext, RetrofitResInterface mRetrofitResInterface, int mMyProfileID, int mFollowProfileID) {
        JsonObject mInputObj = new JsonObject();
        String mFollowRelation = mFollowProfileID + "_" + mMyProfileID;
        mInputObj.addProperty(APIConstants.ProfileID, mMyProfileID);
        mInputObj.addProperty(APIConstants.FollowProfileID, mFollowProfileID);
        mInputObj.addProperty(APIConstants.FollowRelation, mFollowRelation);
        JsonArray mInputArray = new JsonArray();
        mInputArray.add(mInputObj);
        RetrofitClient.getRetrofitInstance().callFollowProfile(mContext, mRetrofitResInterface, mInputArray, RetrofitClient.FOLLOW_PROFILE_RESPONSE);
    }

    public void callUnFollowProfile(Context mContext, RetrofitResInterface mRetrofitResInterface, int mFollowRowID) {
        String mFilter = APIConstants.ID + "=" + mFollowRowID;
        RetrofitClient.getRetrofitInstance().callUnFollowProfile(mContext, mRetrofitResInterface, mFilter, RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE);
    }

    public void callFollowProfile(Context mContext, int mMyProfileID, int mFollowProfileID) {
        JsonObject mInputObj = new JsonObject();
        String mFollowRelation = mFollowProfileID + "_" + mMyProfileID;
        mInputObj.addProperty(APIConstants.ProfileID, mMyProfileID);
        mInputObj.addProperty(APIConstants.FollowProfileID, mFollowProfileID);
        mInputObj.addProperty(APIConstants.FollowRelation, mFollowRelation);
        JsonArray mInputArray = new JsonArray();
        mInputArray.add(mInputObj);
        RetrofitClient.getRetrofitInstance().callFollowProfile(mContext, mInputArray, RetrofitClient.FOLLOW_PROFILE_RESPONSE);
    }

    public void callUnFollowProfile(Context mContext, int mFollowRowID) {
        String mFilter = APIConstants.ID + "=" + mFollowRowID;
        RetrofitClient.getRetrofitInstance().callUnFollowProfile(mContext, mFilter, RetrofitClient.UN_FOLLOW_PROFILE_RESPONSE);
    }

    public void callUnFollowMyProfile(Context mContext, RetrofitResInterface mRetrofitResInterface, int mMyProfileID, int mOtherProfileID) {
        String mFilter = "(" + APIConstants.ProfileID + "=" + mOtherProfileID + ") AND (" + APIConstants.FollowProfileID + "=" + mMyProfileID + ")";
        RetrofitClient.getRetrofitInstance().callUnFollowProfile(mContext, mRetrofitResInterface, mFilter, RetrofitClient.UN_FOLLOW_MY_PROFILE_RESPONSE);
    }

    public void callUnFollowBothProfile(Context mContext, RetrofitResInterface mRetrofitResInterface, int mFollowRowID, int mMyProfileID, int mOtherProfileID) {
        String mFilter = "(" + APIConstants.ID + "=" + mFollowRowID + ") OR ((" + APIConstants.ProfileID + "=" + mOtherProfileID + ") AND (" + APIConstants.FollowProfileID + "=" + mMyProfileID + "))";
        RetrofitClient.getRetrofitInstance().callUnFollowProfile(mContext, mRetrofitResInterface, mFilter, RetrofitClient.UN_FOLLOW_BOTH_PROFILE_RESPONSE);
    }


    public void callBlockProfile(Context mContext, RetrofitResInterface mRetrofitResInterface, int mMyProfileID, int mBlockProfileID) {
        JsonObject mInputObj = new JsonObject();
        String mRelation = mBlockProfileID + "_" + mMyProfileID;
        mInputObj.addProperty(APIConstants.ProfileID, mMyProfileID);
        mInputObj.addProperty(APIConstants.BlockedProfileID, mBlockProfileID);
        mInputObj.addProperty(APIConstants.UniqueRelation, mRelation);
        JsonArray mInputArray = new JsonArray();
        mInputArray.add(mInputObj);

        RetrofitClient.getRetrofitInstance().callBlockProfile(mContext, mRetrofitResInterface, mInputArray,
                RetrofitClient.CALL_BLOCK_USER_PROFILE);
    }

    public void callUnBlockProfile(Context mContext, RetrofitResInterface mRetrofitResInterface, int mBlockRowID) {
        String mFilter = APIConstants.ID + "=" + mBlockRowID;
//        String mFilter = "((ProfileID=" + mMyProfileID + ") AND (BlockedProfileID=" + mBlockProfileID + ")) OR ((ProfileID=" + mBlockProfileID + ") AND (BlockedProfileID=" + mMyProfileID + "))";
        RetrofitClient.getRetrofitInstance().callUnBlockProfile(mContext, mRetrofitResInterface, mFilter, RetrofitClient.CALL_UN_BLOCK_USER_PROFILE);
    }

    public void callAddSharedPost(Context context, PostsResModel mPostResModel, ProfileResModel mMyProfileResModel, String mShareTxt) {

        String mNewSharedID = mPostResModel.getID() + "_" + mMyProfileResModel.getID();
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(PostsModel.PROFILE_ID, String.valueOf(mMyProfileResModel.getID()));
        mJsonObject.addProperty(PostsModel.OLD_POST_ID, mPostResModel.getID());
        mJsonObject.addProperty(PostsModel.WHO_POSTED_PROFILE_ID, mPostResModel.getProfileID());
        mJsonObject.addProperty(PostsModel.WHO_POSTED_USER_ID, mPostResModel.getWhoPostedUserID());
        mJsonObject.addProperty(PostsModel.POST_TEXT, mPostResModel.getPostText());
        mJsonObject.addProperty(PostsModel.POST_PICTURE, mPostResModel.getPostPicture());
        mJsonObject.addProperty(PostsModel.TAGGED_PROFILE_ID, mPostResModel.getTaggedProfileID());
        mJsonObject.addProperty(PostsModel.SHARED_PROFILE_ID, Utility.getInstance().
                getMyFollowersFollowingsID(mMyProfileResModel.getFollowprofile_by_ProfileID(), false));
        mJsonObject.addProperty(PostsModel.WHO_SHARED_PROFILE_ID, mMyProfileResModel.getID());
        mJsonObject.addProperty(PostsModel.NEW_SHARED_POST_ID, mNewSharedID);
        mJsonObject.addProperty(PostsModel.IS_NEWS_FEED_POST, false);
        mJsonObject.addProperty(PostsModel.PostVideoThumbnailurl, mPostResModel.getPostVideoThumbnailURL());
        mJsonObject.addProperty(PostsModel.PostVideoURL, mPostResModel.getPostVideoURL());
        try {
            mJsonObject.addProperty(PostsModel.SHARED_TEXT, URLEncoder.encode(mShareTxt, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (mPostResModel.getUserType().trim().equals(AppConstants.PROMOTER) || mPostResModel.getUserType().trim().equals(AppConstants.CLUB) ||
                mPostResModel.getUserType().trim().equals(AppConstants.NEWS_MEDIA) ||
                mPostResModel.getUserType().trim().equals(AppConstants.TRACK) || mPostResModel.getUserType().trim().equals(AppConstants.SHOP)) {
            mJsonObject.addProperty(PostsModel.USER_TYPE, AppConstants.SHARED_POST);
        }
        final JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        RetrofitClient.getRetrofitInstance().callCreateProfilePosts(context, mJsonArray, RetrofitClient.SHARED_POST_RESPONSE);
    }

    public void callPostShare(Context context, PostsResModel mPostResModel, int mMyProfileID) {

        String mUserType = "";

        switch (mPostResModel.getUserType()) {
            case "sharedVideo":
            case "VideoSharedPost":
                mUserType = "sharedVideo";
                break;
            case "sharedPost":
                mUserType = "user";
                break;
        }

        JsonObject mJsonObject = new JsonObject();
        String mNewSharedID = mPostResModel.getID() + "_" + mMyProfileID;
        mJsonObject.addProperty("PostID", mNewSharedID);
        mJsonObject.addProperty("ProfileID", mMyProfileID);
        mJsonObject.addProperty("user_type", mUserType);
        mJsonObject.addProperty("PostOwnerID", mPostResModel.getProfileID());
        mJsonObject.addProperty("SharedAt", mPostResModel.getDateCreatedAt());
        mJsonObject.addProperty("OriginalPostID", mPostResModel.getID());

        JsonArray mJsonArray = new JsonArray();
        mJsonArray.add(mJsonObject);
        JsonObject mResJsonObject = new JsonObject();
        mResJsonObject.add("resource", mJsonArray);
        RetrofitClient.getRetrofitInstance().callPostShares(context, mResJsonObject, RetrofitClient.POST_SHARES);
    }

    public void callFollowPromoter(Context context, int mPromoterUserID, int mMyProfileID) {

        String mFollowRelation = mMyProfileID + "_" + mPromoterUserID;

        JsonArray mJsonArray = new JsonArray();
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(PromoterFollowerResModel.PROFILE_ID, mMyProfileID);
        mJsonObject.addProperty(PromoterFollowerResModel.PROMOTER_USER_ID, mPromoterUserID);
        mJsonObject.addProperty(PromoterFollowerResModel.FOLLOW_RELATION, mFollowRelation);

        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callFollowPromoter(context,
                mJsonArray, RetrofitClient.GET_PROMOTER_FOLLOW_RESPONSE);
    }

    public void callSendSingleChatMsg(Context context, int fromProfileId, int fromUserID, int toProfileID,
                                      int toUserID, String msg, String chatrelation, String media, String imageUrl) {

        JsonArray mJsonArray = new JsonArray();
        JsonObject mJsonObject = new JsonObject();
        mJsonObject.addProperty(APIConstants.FromProfileID, fromProfileId);
        mJsonObject.addProperty(APIConstants.ToProfileID, toProfileID);
        mJsonObject.addProperty(APIConstants.FromUserID, fromUserID);
        mJsonObject.addProperty(APIConstants.ToUserID, toUserID);
        mJsonObject.addProperty(APIConstants.ChatRelation, chatrelation);
        mJsonObject.addProperty(APIConstants.Message, msg);
        mJsonObject.addProperty(APIConstants.Message_Type, media);
        mJsonObject.addProperty(APIConstants.Image_Url, imageUrl);
        mJsonArray.add(mJsonObject);

        RetrofitClient.getRetrofitInstance().callSendSingleChatMsg(context, mJsonArray, RetrofitClient.SEND_SINGLE_CHAT_MSG);

    }

    public void callPostReportpost(Context mContext, int mUserID, int mProfileID, int mPostID, String mReportString, String mStatus) {
        JsonObject mInputObj = new JsonObject();

        mInputObj.addProperty(ProfileModel.USER_ID, mUserID);
        mInputObj.addProperty(ProfileModel.PROFILE_ID, mProfileID);
        mInputObj.addProperty(AppConstants.POST_ID, mPostID);
        try {
            mInputObj.addProperty(AppConstants.REPORT_STRING, URLEncoder.encode(mReportString, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonArray mInputArray = new JsonArray();
        mInputArray.add(mInputObj);

        RetrofitClient.getRetrofitInstance().callPostReportpost((BaseActivity) mContext, mInputArray,
                RetrofitClient.REPORT_POST_RESPONSE);
    }
}
