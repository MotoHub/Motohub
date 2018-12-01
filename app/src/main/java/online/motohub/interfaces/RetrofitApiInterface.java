package online.motohub.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import online.motohub.model.BlockedUserModel;
import online.motohub.model.ClubGroupModel;
import online.motohub.model.CommonResponse;
import online.motohub.model.DeleteProfileImagesResponse;
import online.motohub.model.EventAddOnModel;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.EventGrpChatMsgModel;
import online.motohub.model.EventLiveGroupChatModel;
import online.motohub.model.EventsModel;
import online.motohub.model.EventsWhoIsGoingModel;
import online.motohub.model.FeedCommentLikeModel;
import online.motohub.model.FeedCommentModel;
import online.motohub.model.FeedCommentReplyModel;
import online.motohub.model.FeedLikesModel;
import online.motohub.model.FeedShareModel;
import online.motohub.model.FollowProfileModel;
import online.motohub.model.FollowProfileModel1;
import online.motohub.model.GalleryImgModel;
import online.motohub.model.GalleryVideoModel;
import online.motohub.model.GroupChatMsgModel;
import online.motohub.model.GroupChatRoomModel;
import online.motohub.model.ImageModel;
import online.motohub.model.LiveStreamPaymentResponse;
import online.motohub.model.LiveStreamRequestResponse;
import online.motohub.model.LiveStreamResponse;
import online.motohub.model.LoginModel;
import online.motohub.model.NotificationBlockedUsersModel;
import online.motohub.model.NotificationModel;
import online.motohub.model.OnDemandVideoUploadedResponse;
import online.motohub.model.OndemandNewResponse;
import online.motohub.model.PaymentCardDetailsModel;
import online.motohub.model.PaymentModel;
import online.motohub.model.PostReportModel;
import online.motohub.model.PostsModel;
import online.motohub.model.PostsResModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.PromoterSubsResModel;
import online.motohub.model.PromoterVideoModel;
import online.motohub.model.PromotersFollowers1;
import online.motohub.model.PurchasedAddOnModel;
import online.motohub.model.PushTokenModel;
import online.motohub.model.RacingModel;
import online.motohub.model.ReplyLikeModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SignUpResModel;
import online.motohub.model.SingleChatCountModel;
import online.motohub.model.SingleChatMsgModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatUnreadMsgModel;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.VehicleInfoLikeModel;
import online.motohub.model.VideoCommentLikeModel;
import online.motohub.model.VideoCommentReplyModel;
import online.motohub.model.VideoCommentsModel;
import online.motohub.model.VideoLikesModel;
import online.motohub.model.VideoReplyLikeModel;
import online.motohub.model.VideoShareModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.util.UrlUtils;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitApiInterface {

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.FACEBOOK_CALLBACK)
    Call<LoginModel> callFacebookLogin(@Query("service") String mService, @Query("code") String mCode, @Query("state") String mState);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.EMAIL_SIGN_UP)
    Call<SignUpResModel> callSignUp(@Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.USER_SESSION)
    Call<LoginModel> callEmailLogin(@Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @PUT(UrlUtils.USER_SESSION)
    Call<SessionModel> callUpdateSession();

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.FOLLOW_PROFILE)
    Call<FollowProfileModel1> callFollowingfollowerscount(@Query("filter") String filter, @Query("include_count") boolean include_count);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.PROFILES)
    Call<ProfileModel> callCreateProfile(@Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.PROFILES)
    Call<ProfileModel> setAvticeUsersCount(@Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.PROFILES)
    Call<ProfileModel> callUpdateProfile(@Query("fields") String fields, @Query("related") String related, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROFILES)
    Call<ProfileModel> callGetProfiles(@Query("filter") String filter, @Query("related") String related);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROFILES)
    Call<ProfileModel> callAllOtherProfiles(@Query("filter") String filter, @Query("related") String related, @Query("limit") int limit,
                                            @Query("offset") int offset, @Query("order") String order, @Query("include_count") boolean include_count);

    @Headers("Content-Type: application/json")
    @POST("user/password?reset=true")
    Call<CommonResponse> callForgotPassword(@Body JsonObject inputObj);

    @Headers("Content-Type: application/json")
    @POST("user/password")
    Call<CommonResponse> callResetPassword(@Body JsonObject inputObj);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.POSTS)
    Call<PostsModel> callCreateProfilePosts(@Query("fields") String fields, @Query("related") String related, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.POSTS)
    Call<PostsModel> callUpdateProfilePosts(@Query("fields") String fields, @Query("related") String related, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.POSTS)
    Call<PostsModel> callDeleteProfilePost(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.POSTS)
    Call<PostsModel> callGetProfilePosts(@Query("filter") String filter, @Query("related") String related, @Query("order") String order,
                                         @Query("limit") int limit, @Query("offset") int offset, @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.POSTS)
    Call<PostsModel> callGetProfilePosts(@Query("related") String related, @Query("order") String order,
                                         @Query("limit") int limit, @Query("offset") int offset, @Query("include_count") boolean count);


    @Headers("Content-Type: application/json")
    @GET(UrlUtils.EVENTS)
    Call<EventsModel> callGetEvents(@Query("filter") String filter, @Query("related") String related, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GET_EVENTS_WHO_IS_GOING)
    Call<EventsWhoIsGoingModel> callGetEventsWhoIsGoing(@Query("filter") String filter, @Query("related") String related);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GET_EVENTS_WHO_IS_GOING)
    Call<EventsWhoIsGoingModel> callBookAnEvent(@Query("fields") String fields, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PUSH_TOKEN)
    Call<PushTokenModel> callGetPushToken(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.PUSH_TOKEN)
    Call<PushTokenModel> callCreatePushToken(@Body PushTokenModel pushTokenModel);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.PUSH_TOKEN)
    Call<PushTokenModel> callUpdatePushToken(@Query("filter") String filter, @Body PushTokenModel pushTokenModel);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.PUSH_TOKEN)
    Call<PushTokenModel> callDeletePushToken(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.SINGLE_CHAT_ROOM)
    Call<SingleChatRoomModel> callCreateSingleChatRoom(@Query("fields") String fields, @Body SingleChatRoomModel singleChatRoomModel);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.SINGLE_CHAT_ROOM)
    Call<SingleChatRoomModel> callGetSingleChatRoom(@Query("filter") String filter, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.SINGLE_CHAT_MESSAGES)
    Call<SingleChatMsgModel> callSendSingleChatMsg(@Query("fields") String fields, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.SINGLE_CHAT_MESSAGES)
    Call<SingleChatMsgModel> callGetSingleChatMsg(@Query("filter") String filter, @Query("order") String order,
                                                  @Query("limit") int limit, @Query("offset") int offset,
                                                  @Query("include_count") boolean include_count);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.SINGLE_CHAT_MESSAGES)
    Call<SingleChatMsgModel> callSetMsgStatus(@Query("filter") String filter, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.SINGLE_CHAT_MESSAGES)
    Call<SingleChatCountModel> callGetSingleChatUnreadCount(@Query("filter") String filter, @Query("group") String group,
                                                            @Query("fields") String fields);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.SINGLE_CHAT_MESSAGES)
    Call<SingleChatUnreadMsgModel> callGetSingleChatUnreadMsg(@Query("filter") String filter, @Query("related") String related,
                                                              @Query("group") String group, @Query("fields") String fields);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.EVENTS_GRP_CHAT_MESSAGES)
    Call<EventGrpChatMsgModel> callSendEventGrpChatMsg(@Query("fields") String fields, @Body EventGrpChatMsgModel eventGrpChatMsgModel);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.EVENTS_GRP_CHAT_MESSAGES)
    Call<EventGrpChatMsgModel> callGetEventGrpChatMsg(@Query("filter") String filter, @Query("related") String related,
                                                      @Query("order") String order, @Query("limit") int limit, @Query("offset") int offset,
                                                      @Query("include_count") boolean include_count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_IMAGE)
    Call<GalleryImgModel> callGetImageGallery(@Query("filter") String filter, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = UrlUtils.GALLERY_IMAGE, hasBody = true)
    Call<DeleteProfileImagesResponse> callDeleteImageGallery(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = UrlUtils.GALLERY_VIDEO, hasBody = true)
    Call<DeleteProfileImagesResponse> callDeleteVideoGallery(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GALLERY_IMAGE)
    Call<GalleryImgModel> postGalleryImg(@Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GALLERY_VIDEO)
    Call<GalleryImgModel> postGalleryVideo(@Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GALLERY_VIDEO)
    Call<OnDemandVideoUploadedResponse> postGalleryVideo1(@Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<GalleryVideoModel> callGetVideoGallery(@Query("filter") String filter, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<GalleryVideoModel> callGetVideoGallery1(@Query("filter") String filter, @Query("order") String order,
                                                 @Query("limit") int limit, @Query("offset") int offset,
                                                 @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.EVENT_GROUPS)
    Call<EventCategoryModel> getEventSessions(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GRP_CHAT_ROOM)
    Call<GroupChatRoomModel> callCreateGrpChatRoom(@Query("fields") String fields, @Body GroupChatRoomModel groupChatRoomModel);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GRP_CHAT_ROOM)
    Call<GroupChatRoomModel> callGetGrpChatRoom(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GRP_CHAT_MSG)
    Call<GroupChatMsgModel> callSendGrpChatMsg(@Query("fields") String fields, @Body GroupChatMsgModel groupChatMsgModel);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GRP_CHAT_MSG)
    Call<GroupChatMsgModel> callGetGrpChatMsg(@Query("filter") String filter, @Query("related") String related,
                                              @Query("order") String order, @Query("limit") int limit, @Query("offset") int offset,
                                              @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTERS)
    Call<PromotersModel> callGetPromotersList(@Query("filter") String filter, @Query("order") String order, @Query("related") String related);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTERS)
    Call<PromotersModel> callGetPromotersList(@Query("filter") String filter);

    //TODO Changed Ramesh
    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTER_FOLLOWERS)
    Call<PromotersFollowers1> callGetPromotersFollowers(@Query("filter") String filter, @Query("limit") int limit,
                                                        @Query("include_count") boolean count);

    //TODO Changed Ramesh for check follower or unfollower
    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTER_FOLLOWERS)
    Call<PromotersFollowers1> callCheckFollowers(@Query("filter") String filter, @Query("limit") int limit,
                                                 @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTERS)
    Call<PromotersModel> callGetPromotersList();

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.PROMOTER_FOLLOWERS)
    Call<PromoterFollowerModel> callFollowPromoter(@Query("fields") String fields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.PROMOTER_FOLLOWERS)
    Call<PromoterFollowerModel> callUnFollowPromoter(@Query("fields") String fields, @Query("filter") String filter);

    //file upload
    @Multipart
    @POST(UrlUtils.FILE_GALLERY_IMAGE)
    Call<ImageModel> postImageFile(@Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.FILE_GALLERY_VIDEO)
    Call<ImageModel> postVideoFile(@Part MultipartBody.Part videoPart, @Part MultipartBody.Part imagePart);


    @Multipart
    @POST(UrlUtils.File_Upload_Video)
    Call<ImageModel> uploadpostview(@Part MultipartBody.Part videoPart, @Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.COMMENT_REPLY_IMG)
    Call<ImageModel> postCommentReplyImgFile(@Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.FILE_POST_IMG)
    Call<ImageModel> postProfilePostImgFile(@Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.FILE_POST_IMG)
    Single<ImageModel> postProfilePostImgFileRxjava(@Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.FILE_COVER_IMG)
    Call<ImageModel> postProfileCoverImgFile(@Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.FILE_PROFILE_IMG)
    Call<ImageModel> postProfileImgFile(@Part MultipartBody.Part imagePart);

    @Multipart
    @POST(UrlUtils.FILE_GRP_CHAT_IMG)
    Call<ImageModel> postGrpChatImgFile(@Part MultipartBody.Part imagePart);


    @Headers("Content-Type: application/json")
    @GET(UrlUtils.EVENTS)
    Call<EventsModel> callGetAllEventsFromTracks(@Query("filter") String filter, @Query("related") String related);

    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST("payment.php")
    Call<PaymentModel> postPaymentForEventBooking(@Field("token") String token, @Field("account") String account,
                                                  @Field("amount") int amount, @Field("type") String mType);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.POST_LIKES)
    Call<FeedLikesModel> postLikesForPosts(@Query("fields") String fields, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.COMMENTS)
    Call<FeedCommentModel> callPostFeedComments(@Query("fields") String fields, @Query("related") String related, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.POST_LIKES)
    Call<FeedLikesModel> postUnLikesForPosts(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.POST_SHARES)
    Call<FeedShareModel> callPostShares(@Query("fields") String fields, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.BLOCKED_USER_PROFILES)
    Call<BlockedUserModel> callSendBlockUserProfile(@Query("fields") String fields, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.BLOCKED_USER_PROFILES)
    Call<BlockedUserModel> callUnBlockUserProfile(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.COMMENTS_REPLY)
    Call<FeedCommentReplyModel> callPostFeedCommentReply(@Query("fields") String fields, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.NOTIFICATION)
    Call<NotificationModel> callGetNotifications(@Query("filter") String filter, @Query("order") String order, @Query("limit") int limit, @Query("offset") int offset, @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.LIVE_STREAM)
    Call<LiveStreamResponse> callPostLiveStream(@Query("fields") String fields, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.LIVE_STREAM)
    Call<LiveStreamResponse> callGetLiveStream(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.RACING)
    Call<RacingModel> callPostRacingData(@Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.EVENT_ANSWER)
    Call<EventAnswersModel> callPostAnswerForEventRegistrationQuestions(@Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.EVENT_ANSWER)
    Call<EventAnswersModel> callUpdateAnswerForEventRegistrationQuestions(
            @Query("fields") String fields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.EVENT_ANSWER)
    Call<EventAnswersModel> callGetAnswerForEventRegistrationQuestions(@Query("filter") String mFilter);


    @Headers("Content-Type: application/json")
    @POST(UrlUtils.LIVE_STREAM_REQUEST)
    Call<LiveStreamRequestResponse> callSendStreamRequest(@Body JsonArray mJsonArray, @Query("fields") String mFields);


    @Headers("Content-Type: application/json")
    @GET(UrlUtils.LIVE_STREAM_REQUEST)
    Call<LiveStreamRequestResponse> callGetRequestedUsersList(@Query("filter") String mFilter, @Query("related") String mRelated);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.LIVE_STREAM_REQUEST)
    Call<LiveStreamRequestResponse> callAcceptStreamRequest(@Body JsonArray mJsonArray, @Query("fields") String mFields);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.LIVE_STREAM_REQUEST)
    Call<LiveStreamRequestResponse> callDeclineStreamRequest(@Query("fields") String mFields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.LIVE_STREAM)
    Call<LiveStreamResponse> callPostLiveStream(@Query("fields") String fields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.LIVE_STREAM)
    Call<LiveStreamResponse> callDeleteLiveStream(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.LIVE_STREAM)
    Call<LiveStreamResponse> callGetFriendsStream(@Query("filter") String mFilter, @Query("related") String mRelated);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.POST_SHARES)
    Call<FeedShareModel> callDeleteSharedPost(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTERS)
    Call<PromotersModel> callGetStreamPromoters(@Query("filter") String filter, @Query("related") String mRelated);


    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST("payment.php")
    Call<PaymentModel> postPayForViewLiveStream(@Field("token") String token, @Field("account") String account, @Field("amount") int amount,
                                                @Field("type") String mType);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.LIVE_STREAM_PAYMENT)
    Call<LiveStreamPaymentResponse> callUpdateLiveStreamPayment(@Body JsonArray mJsonArray, @Query("fields") String mFields);

    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST("payment.php")
    Call<PaymentModel> postSubscribeRequestToClub(@Field("plan") String plan, @Field("email") String email,
                                                  @Field("token") String token, @Field("type") String type, @Field("account_no") String account, @Field("amount")int subscription_fee);

    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST("payment.php")
    Call<ResponseBody> postUnSubscribeRequestToClub(@Field("subscription_id") String subsId, @Field("type") String type, @Field("access_token") String accessToken);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.PROMOTER_SUBSCRIPTION)
    Call<PromoterSubsResModel> callDltPromoterSubs(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.PROMOTER_SUBSCRIPTION)
    Call<PromoterSubsResModel> postPromoterSubscribeRequest(@Body JsonArray mJsonObject, @Query("fields") String mFields);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.COMMENT_LIKES)
    Call<FeedCommentLikeModel> postLikesForComments(@Query("fields") String fields, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.COMMENT_LIKES)
    Call<FeedCommentLikeModel> postUnLikesForComments(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.CLUB_GROUP)
    Call<ClubGroupModel> callUnSubScribeClub(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VEHICLE_INFO_LIKE)
    Call<VehicleInfoLikeModel> postLikeForVehicleInfo(@Query("fields") String fields, @Body JsonArray jsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.VEHICLE_INFO_LIKE)
    Call<VehicleInfoLikeModel> postUnLikesForVehicleInfo(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.EVENT_ADD_ON)
    Call<EventAddOnModel> callGetEventAddOn(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.PURCHASED_ADD_ON)
    Call<PurchasedAddOnModel> callPostPurchasedAddOn(@Body JsonArray mJsonArray, @Query("fields") String mFields);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.COMMENTS)
    Call<FeedCommentModel> callGetComments(@Query("filter") String mFilter, @Query("related") String mRelated, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.REPLY_LIKE)
    Call<ReplyLikeModel> postLikesForReply(@Query("fields") String mFields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.REPLY_LIKE)
    Call<ReplyLikeModel> postUnLikesForReply(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.COMMENTS_REPLY)
    Call<FeedCommentReplyModel> callGetFeedCommentReply(@Query("filter") String mFilter, @Query("related") String mRelated,
                                                        @Query("order") String order);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.LIVE_EVENTS_GRP_CHAT_MESSAGES)
    Call<EventGrpChatMsgModel> callGetLiveEventGrpChatMsg(@Query("filter") String filter, @Query("related") String related,
                                                          @Query("order") String order, @Query("limit") int limit, @Query("offset") int offset,
                                                          @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.LIVE_EVENTS_GRP_CHAT_MESSAGES)
    Call<EventGrpChatMsgModel> callGetLiveEventPromoterGrpChatMsg(@Query("filter") String filter, @Query("related") String related,
                                                                  @Query("order") String order);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.LIVE_EVENTS_GRP_CHAT_MESSAGES)
    Call<EventGrpChatMsgModel> callSendLiveEventGrpChatMsg(@Query("fields") String fields, @Body EventGrpChatMsgModel eventGrpChatMsgModel);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.FOLLOW_PROFILE)
    Call<FollowProfileModel> callFollowProfile(@Query("fields") String mFields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.FOLLOW_PROFILE)
    Call<FollowProfileModel> callUnFollowProfile(@Query("fields") String mFields, @Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callGetPromotersVideos(@Query("fields") String fields, @Query("filter") String mFilter,
                                                    @Query("related") String related, @Query("order") String order,
                                                    @Query("limit") int mLimit, @Query("offset") int mOffset, @Query("include_count") boolean isIncludeCount);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callGetPromotersVideosNotifyTray(@Query("fields") String fields, @Query("filter") String mFilter,
                                                              @Query("related") String related, @Query("order") String order,
                                                              @Query("include_count") boolean isIncludeCount);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callGetPromotersVideosForAdapter(@Query("fields") String fields, @Query("filter") String mFilter,
                                                              @Query("related") String related, @Query("order") String order,
                                                              @Query("limit") int mLimit, @Query("offset") int mOffset, @Query("include_count") boolean isIncludeCount);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callGetPromotersVideosWhilePlaying(@Query("fields") String fields, @Query("filter") String mFilter,
                                                                @Query("related") String related,
                                                                @Query("order") String order,
                                                                @Query("limit") int mLimit, @Query("offset") int mOffset, @Query("include_count") boolean isIncludeCount);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.EVENT_LIVE_GROUP_CHAT)
    Call<EventLiveGroupChatModel> callPostEventLiveGroupChatMember(@Query("fields") String mFields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.EVENT_LIVE_GROUP_CHAT)
    Call<EventLiveGroupChatModel> callDeleteEventLiveGroupChatMember(@Query("filter") String filter);

    @Multipart
    @POST(UrlUtils.FILE_SPECTATOR_LIVE)
    Call<ImageModel> postSpectatorLiveImgFile(@Part MultipartBody.Part videoPart, @Part MultipartBody.Part imagePart);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VIDEO_LIKES)
    Call<VideoLikesModel> postLikesForVideos(@Query("fields") String fields, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.VIDEO_LIKES)
    Call<VideoLikesModel> postUnLikesForVideos(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.VIDEO_COMMENTS)
    Call<VideoCommentsModel> callGetVideoComments(@Query("fields") String fields, @Query("filter") String mFilter,
                                                  @Query("related") String mRelated, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VIDEO_COMMENT_REPLY)
    Call<VideoCommentReplyModel> callPostVideoCommentReply(@Query("fields") String fields, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VIDEO_REPLY_LIKE)
    Call<VideoReplyLikeModel> postLikesForVideoReply(@Query("fields") String mFields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.VIDEO_REPLY_LIKE)
    Call<VideoReplyLikeModel> postUnLikesForVideoReply(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.VIDEO_COMMENT_LIKE)
    Call<VideoCommentLikeModel> postUnLikesForVideoComments(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VIDEO_COMMENT_LIKE)
    Call<VideoCommentLikeModel> postLikesForVideoComments(@Query("fields") String fields, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VIDEO_COMMENTS)
    Call<VideoCommentsModel> callPostVideoComments(@Query("fields") String fields, @Query("related") String related, @Body JsonObject jsonArray);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.VIDEO_COMMENT_REPLY)
    Call<VideoCommentReplyModel> callGetVideoCommentsReply(@Query("fields") String fields, @Query("filter") String mFilter,
                                                           @Query("related") String mRelated, @Query("order") String order);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.VIDEO_SHARES)
    Call<VideoShareModel> callPostVideoShares(@Query("fields") String fields, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.GALLERY_VIDEO)
    Call<SpectatorLiveModel> callPostSpectatorLiveStory(@Query("fields") String mFields, @Body JsonArray mJsonObject);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.VEHICLE_INFO_LIKE)
    Call<VehicleInfoLikeModel> callGetVehicleInfoLikes(@Query("fields") String fields, @Query("filter") String mFilter, @Query("related") String mRelated);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTER_SUBSCRIPTION)
    Call<PromoterSubsResModel> callGetPromoterSubs(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.REPORT_POST)
    Call<PostReportModel> callPostReportpost(@Query("fields") String mFields, @Body JsonArray mJsonObject);

    @Multipart
    @POST(UrlUtils.FILE_SINGLE_CHAT)
    Call<ImageModel> postSingleChatImgFile(@Part MultipartBody.Part imagePart);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.NEW_ONDEMAND)
    Call<ArrayList<OndemandNewResponse>> callGetOndemandList(@Query("api_key") String key);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTER_FOLLOWERS)
    Call<PromoterFollowerModel> callGetIsAlreadyFollowedPromoter(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.FOLLOW_PROFILE)
    Call<FollowProfileModel> callGetIsAlreadyFollowedProfile(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTER_SUBSCRIPTION)
    Call<PromoterSubsResModel> callGetClubUsers(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.VIDEO_SHARES)
    Call<VideoShareModel> callDeleteVideoSharedPost(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callUpdateOnDemandProfilePosts(@Query("fields") String mFields, @Body JsonArray mJsonObject);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PAYMENT_CARD_DETAILS)
    Call<PaymentCardDetailsModel> callGetPaymentCardDetails(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.PAYMENT_CARD_DETAILS)
    Call<PaymentCardDetailsModel> callAddPaymentCardDetails(@Query("fields") String mFields, @Body JsonArray mJsonArray);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.PAYMENT_CARD_DETAILS)
    Call<PaymentCardDetailsModel> callDeletePaymentCardDetails(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @POST(UrlUtils.Block_Unblock_Post_Notifications)
    Call<NotificationBlockedUsersModel> blockNotifications(@Query("fields") String fields, @Body JsonObject jsonobj);

    @Headers("Content-Type: application/json")
    @DELETE(UrlUtils.Block_Unblock_Post_Notifications)
    Call<NotificationBlockedUsersModel> unBlockNotifications(@Query("fields") String fields, @Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTER_SUBSCRIPTION)
    Call<PromoterSubsResModel> callGetClubSubs(@Query("related") String related, @Query("filter") String filter,
                                               @Query("limit") int limit, @Query("offset") int offset,
                                               @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.FOLLOW_PROFILE)
    Call<FollowProfileModel> callGetFollowings(@Query("related") String related, @Query("filter") String filter,
                                               @Query("limit") int limit, @Query("offset") int offset,
                                               @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROFILES)
    Call<ProfileModel> callAllOtherSearchProfiles(@Query("filter") String filter, @Query("related") String related, @Query("limit") int limit,
                                                  @Query("offset") int offset, @Query("include_count") boolean include_count);

    @POST(UrlUtils.PHONE_NUMBERS)
    Call<ProfileModel> callGetPhoneEmailProfiles(@Query("fields") String mFields, @Body JsonObject mJsonObject);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.USER_TYPE)
    Call<LoginModel> callGetProfileUserType(@Query("fields") String mFields);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.PROMOTERS)
    Call<PromotersModel> callGetPromoterWithPushToken(@Query("filter") String mFilter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.POSTS)
    Call<PostsModel> callGetViewCount(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @GET(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callGetViewCountOnDemand(@Query("filter") String filter);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.POSTS)
    Call<PostsModel> callsetViewCount(@Query("fields") String fields, @Body JsonObject mJsonArray);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.GALLERY_VIDEO)
    Call<PromoterVideoModel> callsetViewCountOnDemand(@Query("fields") String fields, @Body JsonObject mJsonArray);

    @GET(UrlUtils.ALL_POST)
    Call<PostsModel> callGetAllPosts(@Query("userid") int filter, @Query("related") String related,
                                     @Query("limit") int limit, @Query("offset") int offset, @Query("include_count") boolean count);

    @Headers("Content-Type: application/json")
    @PATCH(UrlUtils.PROMOTER_SUBSCRIPTION)
    Call<PromoterSubsResModel> callUpdateUnSubscription(@Body JsonArray mArray, @Query("fields") String fields);


    /**
     * Don't Delete
     */
//    @GET("http://18.221.84.245:10000/api/v1/startdevicestream")
//    Call<DeviceStreamModel> callStartDeviceStream(@Query("device") String device, @Query("channel") int channel, @Query("reserve") int reserve);
//    @GET("http://18.221.84.245:10000/api/v1/getdeviceinfo")
//    Call<DeviceInfoModel> callGetDeviceInfo(@Query("device") String device);
//    @GET("http://18.221.84.245:10088/api/v1/getdevicestream?device=1234567890123&channel=1&protocol=rtmp&reserve=1")
//    Call<GetDeviceStreamModel> callGetDeviceStream();

}