package online.motohub.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.ViewLiveVideoViewScreen;
import online.motohub.adapter.EventCategoryAdapter;
import online.motohub.adapter.TagFollowingProfileAdapter;
import online.motohub.adapter.TagSponsorsHorAdapter;
import online.motohub.adapter.TaggedSponsorsAdapter;
import online.motohub.application.MotoHub;
import online.motohub.interfaces.RetrofitResInterface;
import online.motohub.interfaces.SendVideoUrl;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.ProfileModel;
import online.motohub.model.ProfileResModel;
import online.motohub.model.SessionModel;
import online.motohub.model.SingleChatRoomModel;
import online.motohub.model.SingleChatRoomResModel;
import online.motohub.model.promoter_club_news_media.PromotersModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;

import static online.motohub.retrofit.RetrofitClient.GET_PROMOTERS_RESPONSE;

public class AppDialogFragment extends DialogFragment implements
        RetrofitResInterface,
        TagSponsorsHorAdapter.TagSponsorsInterface,
        TaggedSponsorsAdapter.TaggedSponsorsInterface, SendVideoUrl {

    public static final String TAG = AppDialogFragment.class.getSimpleName(),
            BOTTOM_ADD_IMG_DIALOG = "2", ALERT_EXIT_DIALOG = "3", ALERT_TIP_DIALOG = "4",
            ALERT_PROFILE_TYPE = "5", ALERT_TAG_SPONSOR_DIALOG = "6", IN_APP_PURCHASE_DIALOG = "7",
            ALERT_OTHER_PROFILE_DIALOG = "8", BOTTOM_POST_ACTION_DIALOG = "9",
            BOTTOM_EDIT_DIALOG = "10", BOTTOM_DELETE_DIALOG = "11", DIALOG_PROFILE_VIEW = "12",
            TAG_FOLLOWING_PROFILE_DIALOG = "13", ALERT_NO_EVENT_SESSIONS = "14",
            BOTTOM_SHARE_DIALOG = "15", ALERT_BLOCK_DIALOG = "16", ALERT_UN_BLOCK_DIALOG = "17",
            BOTTOM_LIVE_STREAM_DIALOG = "18", BUFFERING_DIALOG = "19",
            ALERT_INTERNET_FAILURE_DIALOG = "20", EVENT_CATEGORY_DIALOG = "21",
            ALERT_SPECTATOR_UPDATE_DIALOG = "23", UPGRADE_SPECTATOR_OPTION_DIALOG = "24",
            ALERT_API_FAILURE_DIALOG = "25", BOTTOM_ADD_VIDEO_DIALOG = "26",
            BOTTOM_VIEW_PHOTOS_VIDEOS_DIALOG = "27", BOTTOM_VIEW_VIDEOS_DIALOG = "28",
            BOTTOM_VIEW_PHOTOS_DIALOG = "29", BOTTOM_CHAT_EDIT = "30",
            BOTTOM_LIVE_STREAM_OPTION_DIALOG = "31", BOTTOM_LIVE_STREAM_OPTION_MULTI = "32",
            BOTTOM_SHARED_POST_ACTION_DIALOG = "33", ALERT_TRIAL_DIALOG = "34", LOG_OUT_DIALOG = "35", DIALOG_PROMOTER_PROFILE_VIEW = "36",
            BOTTOM_REPORT_ACTION_DIALOG = "37", ACCESS_CONTACT_ALERT_DIALOG = "38", SET_CONTACT_PERMISSION = "39",
            BOTTOM_ADD_IMG_DIALOG_BUSINESS = "40";
    public static AppDialogFragment mAppDialogFragment = new AppDialogFragment();
    private static String mDialogType;
    private static ArrayList<Bitmap> mShareImage;
    private static String[] mVideoUrl;
    //private static AppDialogFragment mAppDialogFragment;
    public ShareDialog shareFBDialog;
    private Unbinder mUnBinder;
    private ArrayList<String> mProfileTypes;
    private String mShareContent;
    private int mProfilePosition;
    private ArrayList<PromotersResModel> mTaggedSponsorsList;
    private TagSponsorsHorAdapter mTagSponsorsHorAdapter;
    private ArrayList<PromotersResModel> mTagSponsorsList;
    private EditText mSearchSponsorEt;
    private TaggedSponsorsAdapter mTaggedSponsorsAdapter;
    private EditText mSponsorEmailEt;
    private SingleChatRoomResModel singleChatMsgResModel;
    private EventCategoryAdapter mEventCategoryAdapter;
    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };

    public static AppDialogFragment getInstance() {
        return mAppDialogFragment;
    }

    public static AppDialogFragment newInstance(String dialog_type, ArrayList<String> profileTypes) {

        mAppDialogFragment = new AppDialogFragment();

        mDialogType = dialog_type;

        switch (dialog_type) {

            case BUFFERING_DIALOG:
                initAppDialog(mAppDialogFragment, R.style.MyDialogNormal, false);
                break;
            case BOTTOM_ADD_VIDEO_DIALOG:
            case BOTTOM_CHAT_EDIT:
            case BOTTOM_ADD_IMG_DIALOG:
            case BOTTOM_ADD_IMG_DIALOG_BUSINESS:
            case BOTTOM_POST_ACTION_DIALOG:
            case BOTTOM_REPORT_ACTION_DIALOG:
            case BOTTOM_SHARED_POST_ACTION_DIALOG:
            case BOTTOM_VIEW_PHOTOS_VIDEOS_DIALOG:
            case ALERT_PROFILE_TYPE:
            case DIALOG_PROMOTER_PROFILE_VIEW:
            case DIALOG_PROFILE_VIEW:
            case IN_APP_PURCHASE_DIALOG:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, true);
                break;
            case ALERT_NO_EVENT_SESSIONS:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, false);
                break;
            case BOTTOM_LIVE_STREAM_DIALOG:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, true);
                break;
            case BOTTOM_LIVE_STREAM_OPTION_DIALOG:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, true);
                break;
            case UPGRADE_SPECTATOR_OPTION_DIALOG:
            case LOG_OUT_DIALOG:
            case ALERT_API_FAILURE_DIALOG:
            case EVENT_CATEGORY_DIALOG:
            case ACCESS_CONTACT_ALERT_DIALOG:
            case SET_CONTACT_PERMISSION:
            case ALERT_TRIAL_DIALOG:
                initAppDialog(mAppDialogFragment, 0, false);
                break;
            default:
                initAppDialog(mAppDialogFragment, 0, true);
                break;
        }

        Bundle mBundle = new Bundle();
        mBundle.putString("dialogType", dialog_type);
        mBundle.putStringArrayList("profileTypes", profileTypes);
        mAppDialogFragment.setArguments(mBundle);
        return mAppDialogFragment;
    }

    public static AppDialogFragment newInstance(String dialog_type, List<ProfileResModel> followingList, ArrayList<EventCategoryModel> eventCategoryList) {

        mAppDialogFragment = new AppDialogFragment();

        mDialogType = dialog_type;
        Bundle mBundle = new Bundle();

        switch (dialog_type) {
            case TAG_FOLLOWING_PROFILE_DIALOG:
                initAppDialog(mAppDialogFragment, 0, true);
                mBundle.putString("dialogType", dialog_type);
                mBundle.putSerializable(ProfileModel.OTHERS_PROFILE_RES_MODEL, (Serializable) followingList);
                mAppDialogFragment.setArguments(mBundle);
                break;
            case EVENT_CATEGORY_DIALOG:
                initAppDialog(mAppDialogFragment, 0, false);
                mBundle.putString("dialogType", dialog_type);
                mBundle.putSerializable(EventCategoryModel.EVENT_CATEGORY_RES_MODEL, eventCategoryList);
                mAppDialogFragment.setArguments(mBundle);
                break;
        }

        return mAppDialogFragment;

    }

    public static AppDialogFragment newInstanceForTagSponsors(String dialogType, String currentTagSponsor) {

        mAppDialogFragment = new AppDialogFragment();

        mDialogType = dialogType;

        switch (dialogType) {
            case ALERT_TAG_SPONSOR_DIALOG:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, true);
                break;
        }

        Bundle mBundle = new Bundle();
        mBundle.putString("dialogType", dialogType);
        mBundle.putString("currentSponsorTag", currentTagSponsor);
        mAppDialogFragment.setArguments(mBundle);

        return mAppDialogFragment;

    }

    public static AppDialogFragment newInstanceForChatDelete(String dialogType, SingleChatRoomResModel singleChatRoomResModel) {

        mAppDialogFragment = new AppDialogFragment();

        mDialogType = dialogType;

        switch (dialogType) {
            case BOTTOM_CHAT_EDIT:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, true);
                break;
        }

        Bundle mBundle = new Bundle();
        mBundle.putString("dialogType", dialogType);
        mBundle.putSerializable("singlechatroomresmodel", singleChatRoomResModel);
        mAppDialogFragment.setArguments(mBundle);

        return mAppDialogFragment;

    }

    public static AppDialogFragment newInstance(String dialog_type, String content, ArrayList<Bitmap> imgUrl, String[] videoUrl, int position, boolean isFromOtherMotoFile) {

        mAppDialogFragment = new AppDialogFragment();

        mShareImage = imgUrl;

        mDialogType = dialog_type;

        mVideoUrl = videoUrl;

        switch (mDialogType) {
            case BOTTOM_SHARE_DIALOG:
                initAppDialog(mAppDialogFragment, R.style.MyDialogBottomSheet, true);
                break;
        }

        Bundle mBundle = new Bundle();
        mBundle.putString("dialogType", dialog_type);
        mBundle.putString("shareContent", content);
        mBundle.putInt("profilePosition", position);
        mBundle.putBoolean("otherMotoProfile", isFromOtherMotoFile);
        mAppDialogFragment.setArguments(mBundle);

        return mAppDialogFragment;

    }

    private static void initAppDialog(AppDialogFragment appDialogFragment, int style, boolean cancelable) {
        appDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, style);
        appDialogFragment.setCancelable(cancelable);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        assert getArguments() != null;
        final String mDialogType = getArguments().getString("dialogType", "");

        // Create a callbackManager to handle the login responses.
        CallbackManager callbackManager = CallbackManager.Factory.create();

        shareFBDialog = new ShareDialog(this);

        // this part is optional
        shareFBDialog.registerCallback(callbackManager, callback);

        View mView;

        switch (mDialogType) {


            case BUFFERING_DIALOG:

                mView = inflater.inflate(R.layout.widget_buffer_progress_bar, container, false);

                return mView;
            case BOTTOM_ADD_IMG_DIALOG:

                mView = inflater.inflate(R.layout.dialog_image_selection, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                return mView;

            case BOTTOM_ADD_IMG_DIALOG_BUSINESS:

                mView = inflater.inflate(R.layout.dialog_image_selection_business, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                return mView;

            case BOTTOM_ADD_VIDEO_DIALOG:

                mView = inflater.inflate(R.layout.dialog_video_selection, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                return mView;


            case BOTTOM_CHAT_EDIT:

                mView = inflater.inflate(R.layout.dialog_bottom_chat_options, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);
                singleChatMsgResModel = (SingleChatRoomResModel) getArguments().getSerializable("singlechatroomresmodel");
                return mView;

            case BOTTOM_POST_ACTION_DIALOG:

                mView = inflater.inflate(R.layout.dialog_post_action, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                return mView;

            case BOTTOM_REPORT_ACTION_DIALOG:

                mView = inflater.inflate(R.layout.dialog_report_action, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                return mView;

            case BOTTOM_SHARED_POST_ACTION_DIALOG:

                mView = inflater.inflate(R.layout.dialog_post_action, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                RelativeLayout mEditLayout = mView.findViewById(R.id.editLayout);

                mEditLayout.setVisibility(View.GONE);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                return mView;

            case BOTTOM_VIEW_PHOTOS_VIDEOS_DIALOG:

                mView = inflater.inflate(R.layout.dialog_view_photos_videos, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                TextView mPictureTv = mView.findViewById(R.id.edit_tv);
                TextView mVideoTv = mView.findViewById(R.id.delete_tv);

                mPictureTv.setText(R.string.view_photos);
                mVideoTv.setText(R.string.view_videos);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                return mView;

            case ALERT_PROFILE_TYPE:

                mView = inflater.inflate(R.layout.dialog_profile_type, container, false);

                CheckBox mBikeChkBox = mView.findViewById( R.id.bike_cb);
                CheckBox mBoatChkBox = mView.findViewById( R.id.boat_cb);
                CheckBox mCarChkBox = mView.findViewById( R.id.car_cb);
                CheckBox mKartChkBox = mView.findViewById( R.id.kart_cb);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                if (mProfileTypes == null || mProfileTypes.isEmpty()) {
                    mProfileTypes = new ArrayList<>();
                }

                for (String profileTypeStr : mProfileTypes) {
                    switch (profileTypeStr.trim()) {
                        case BaseActivity.BIKE_STR:
                            mBikeChkBox.setChecked(true);
                            break;
                        case BaseActivity.BOAT_STR:
                            mBoatChkBox.setChecked(true);
                            break;
                        case BaseActivity.CAR_STR:
                            mCarChkBox.setChecked(true);
                            break;
                        case BaseActivity.KART_STR:
                            mKartChkBox.setChecked(true);
                            break;
                    }
                }

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                mUnBinder = ButterKnife.bind(this, mView);

                return mView;

            case ALERT_OTHER_PROFILE_DIALOG:

                mView = inflater.inflate(R.layout.dialog_other_profile, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                final Spinner mMPSpinner = mView.findViewById( R.id.moto_type_spinner);

                TextView mOkBtn = mView.findViewById( R.id.okay_btn);

                ArrayList<String> mMPSpinnerList = new ArrayList<>();

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                if (mProfileTypes != null && mProfileTypes.size() > 0) {
                    mMPSpinnerList.addAll(mProfileTypes);
                } else {
                    Collections.addAll(mMPSpinnerList, getResources().getStringArray(R.array.empty_array));
                }


                ArrayAdapter<String> mMPSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.widget_spinner_item, mMPSpinnerList);
                mMPSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                mMPSpinner.setAdapter(mMPSpinnerAdapter);

                mMPSpinner.setSelection(((BaseActivity) getContext()).getProfileCurrentPos());

                mOkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, mMPSpinner.getSelectedItemPosition());
                        dismiss();
                    }
                });

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;

            case TAG_FOLLOWING_PROFILE_DIALOG:

                mView = inflater.inflate(R.layout.dialog_tag_following_profile, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                @SuppressWarnings("unchecked")
                List<ProfileResModel> mFollowersList = (List<ProfileResModel>) getArguments().getSerializable(ProfileModel.OTHERS_PROFILE_RES_MODEL);

                final ListView mFollowingListView = mView.findViewById( R.id.following_list_view);
                final TagFollowingProfileAdapter mTagFollowingProfileAdapter = new TagFollowingProfileAdapter(getActivity(), mFollowersList);
                mFollowingListView.setAdapter(mTagFollowingProfileAdapter);

                final EditText mSearchFollowingEt = mView.findViewById( R.id.search_following_et);

                mSearchFollowingEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        mTagFollowingProfileAdapter.getFilter().filter(charSequence);

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;

            case ALERT_TAG_SPONSOR_DIALOG:

                mView = inflater.inflate(R.layout.dialog_tag_sponsor, container, false);

                String mCurrentSponsorTag = getArguments().getString("currentSponsorTag");

                mUnBinder = ButterKnife.bind(this, mView);

                RecyclerView mTagSponsorsRv = mView.findViewById( R.id.add_sponsor_recycler_view);

                LinearLayoutManager mHorLayoutManager = new LinearLayoutManager(getContext());
                mHorLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                mTagSponsorsRv.setLayoutManager(mHorLayoutManager);

                mTagSponsorsList = new ArrayList<>();
                mTagSponsorsHorAdapter = new TagSponsorsHorAdapter(this, mTagSponsorsList);
                mTagSponsorsRv.setAdapter(mTagSponsorsHorAdapter);

                mTaggedSponsorsList = new ArrayList<>();

                mTaggedSponsorsAdapter = new TaggedSponsorsAdapter(this, mTaggedSponsorsList);

                RecyclerView mTagRecyclerView = mView.findViewById( R.id.tag_selected_sponsors_list_view);

                FlexboxLayoutManager mFlexBoxLayoutManager = new FlexboxLayoutManager(getContext());
                mFlexBoxLayoutManager.setFlexWrap(FlexWrap.WRAP);
                mFlexBoxLayoutManager.setFlexDirection(FlexDirection.ROW);
                mFlexBoxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
                mTagRecyclerView.setLayoutManager(mFlexBoxLayoutManager);

                mTagRecyclerView.setAdapter(mTaggedSponsorsAdapter);

                mSearchSponsorEt = mView.findViewById( R.id.search_sponsor_et);

                mSponsorEmailEt = mView.findViewById( R.id.enter_sponsor_email_et);

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;

            case IN_APP_PURCHASE_DIALOG:

                mView = inflater.inflate(R.layout.dialog_in_app_purchase, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                TextView mSetProfileTypeTv = mView.findViewById( R.id.profile_type_tv);
                TextView mSetMonthValTv = mView.findViewById( R.id.monthly_val_tv);
                TextView mSetYearValTv = mView.findViewById( R.id.yearly_val_tv);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                assert mProfileTypes != null;
                mSetProfileTypeTv.setText(mProfileTypes.get(0));
                String mMonthValStr = mProfileTypes.get(1) + " / Month";
                String mYearValStr = mProfileTypes.get(2) + " / Year";
                mSetMonthValTv.setText(mMonthValStr);
                mSetYearValTv.setText(mYearValStr);

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;

            case DIALOG_PROMOTER_PROFILE_VIEW:

                mView = inflater.inflate(R.layout.dialog_profile_view, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                TextView mBlockBtn = mView.findViewById(R.id.block_btn);
                mBlockBtn.setVisibility(View.GONE);

                TextView mProfileNameTxt = mView.findViewById( R.id.profile_name_tv);
                CircleImageView mProfileImgView = mView.findViewById( R.id.profile_img);

                assert mProfileTypes != null;

                mProfileNameTxt.setText(mProfileTypes.get(2));

                if (mProfileTypes.get(3) != null && !mProfileTypes.get(3).isEmpty()) {
                    ((BaseActivity) getActivity()).setImageWithGlide(mProfileImgView, mProfileTypes.get(3), R.drawable.default_profile_icon);
                }

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;


            case DIALOG_PROFILE_VIEW:

                mView = inflater.inflate(R.layout.dialog_profile_view, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                mProfileTypes = getArguments().getStringArrayList("profileTypes");

                TextView mProfileNameTv = mView.findViewById( R.id.profile_name_tv);
                CircleImageView mProfileImg = mView.findViewById( R.id.profile_img);

                assert mProfileTypes != null;

                mProfileNameTv.setText(mProfileTypes.get(2));

                if (mProfileTypes.get(3) != null && !mProfileTypes.get(3).isEmpty()) {
                    ((BaseActivity) getActivity()).setImageWithGlide(mProfileImg, mProfileTypes.get(3), R.drawable.default_profile_icon);
                }

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;

            case ALERT_NO_EVENT_SESSIONS:

                mView = inflater.inflate(R.layout.dialog_no_event_sessions, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.CENTER, WindowManager.LayoutParams.WRAP_CONTENT);

                return mView;

            case BOTTOM_SHARE_DIALOG:

                mView = inflater.inflate(R.layout.share_layout, container, false);

                LinearLayout layout = mView.findViewById(R.id.Friends_lay);
                Space space = mView.findViewById(R.id.space);

                ImageView ivFacebook = mView.findViewById(R.id.imgvwFacebook);
                ColorFilter cf = new PorterDuffColorFilter(Color.rgb(0, 0, 0), PorterDuff.Mode.SRC_IN);
                ivFacebook.setColorFilter(cf);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                mShareContent = getArguments().getString("shareContent");

                mProfilePosition = getArguments().getInt("profilePosition");

                boolean mIsFromOtherMotoProfile = getArguments().getBoolean("otherMotoProfile");

                if (mIsFromOtherMotoProfile) {
                    layout.setVisibility(View.VISIBLE);
                    space.setVisibility(View.VISIBLE);
                } else {
                    layout.setVisibility(View.GONE);
                    space.setVisibility(View.GONE);
                }

                return mView;

            case BOTTOM_LIVE_STREAM_DIALOG:

                mView = inflater.inflate(R.layout.live_stream_layout, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                return mView;

            case BOTTOM_LIVE_STREAM_OPTION_DIALOG:

                mView = inflater.inflate(R.layout.live_stream_option_layout, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                setDialogProperties(Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT);

                return mView;
            case EVENT_CATEGORY_DIALOG:

                mView = inflater.inflate(R.layout.dialog_event_category, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                ArrayList<EventCategoryModel> mEventCategoryList = (ArrayList<EventCategoryModel>) getArguments().getSerializable(EventCategoryModel.EVENT_CATEGORY_RES_MODEL);

                RecyclerView mEventCategoryRecyclerView = mView.findViewById( R.id.rvEventCategory);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mEventCategoryRecyclerView.setLayoutManager(mLayoutManager);
                mEventCategoryRecyclerView.setItemAnimator(new DefaultItemAnimator());

                mEventCategoryAdapter = new EventCategoryAdapter(getActivity(), mEventCategoryList);
                mEventCategoryRecyclerView.setAdapter(mEventCategoryAdapter);

                ImageButton closeDialogBtn = mView.findViewById( R.id.close_btn);

                Button mDoneCategoryBtn = mView.findViewById( R.id.doneCategoryBtn);

                mDoneCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<EventCategoryModel> mSelectedEventCategoryList = mEventCategoryAdapter.getSelectedEventCategory();
                        MotoHub.getApplicationInstance().setSelectedEventCategoryList(mSelectedEventCategoryList);
                        if (mSelectedEventCategoryList == null || mSelectedEventCategoryList.isEmpty()) {
                            Toast.makeText(getContext(), "Please select the category", Toast.LENGTH_SHORT).show();
                        } else {
                            ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, 0);
                            dismiss();
                        }
                    }
                });

                closeDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                return mView;

            case UPGRADE_SPECTATOR_OPTION_DIALOG:

                mView = inflater.inflate(R.layout.dialog_profile_type, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                return mView;


            default:

                mView = inflater.inflate(R.layout.dialog_alert, container, false);

                mUnBinder = ButterKnife.bind(this, mView);

                TextView mDialogTv = mView.findViewById( R.id.dialog_tv);

                TextView mDialogPosBtn = mView.findViewById( R.id.dialog_positive_btn);
                TextView mDialogNegBtn = mView.findViewById( R.id.dialog_negative_btn);
                Space mSpace = mView.findViewById( R.id.btn_space);

                switch (mDialogType) {
                    case ALERT_EXIT_DIALOG:
                        break;
                    case ALERT_TIP_DIALOG:
                        mSpace.setVisibility(View.GONE);
                        mDialogPosBtn.setVisibility(View.GONE);
                        mDialogNegBtn.setText(R.string.ok);
                        mDialogTv.setText(R.string.use_desktop_version);
                        break;
                    case ALERT_BLOCK_DIALOG:
                        mDialogTv.setText(R.string.alert_block);
                        break;
                    case ALERT_UN_BLOCK_DIALOG:
                        mDialogTv.setText(R.string.alert_unblock);
                        break;
                    case ALERT_INTERNET_FAILURE_DIALOG:
                        mSpace.setVisibility(View.GONE);
                        mDialogNegBtn.setVisibility(View.GONE);
                        mDialogPosBtn.setText(R.string.retry);
                        mDialogTv.setText(R.string.internet_err);
                        break;
                    case ALERT_SPECTATOR_UPDATE_DIALOG:
                        mDialogTv.setText(R.string.purchase_racer_profile);
                        break;
                    case ALERT_API_FAILURE_DIALOG:
                        mSpace.setVisibility(View.GONE);
                        mDialogNegBtn.setVisibility(View.GONE);
                        mDialogPosBtn.setText(R.string.retry);
                        mDialogTv.setText(R.string.api_err);
                        break;
                    case LOG_OUT_DIALOG:
                        mDialogPosBtn.setVisibility(View.VISIBLE);
                        mDialogPosBtn.setText(R.string.logout);
                        mDialogNegBtn.setVisibility(View.VISIBLE);
                        mDialogNegBtn.setText(R.string.cancel);
                        mDialogTv.setText(R.string.logout_alert);
                        break;

                    case ALERT_TRIAL_DIALOG:
                        mSpace.setVisibility(View.GONE);
                        mDialogNegBtn.setVisibility(View.GONE);
                        mDialogPosBtn.setText(R.string.ok);
                        mDialogTv.setText(R.string.profile_trial);
                        break;
                    case SET_CONTACT_PERMISSION:
                        mDialogPosBtn.setVisibility(View.VISIBLE);
                        mDialogPosBtn.setText(R.string.open_settings);
                        mDialogNegBtn.setVisibility(View.VISIBLE);
                        mDialogNegBtn.setText(R.string.cancel);
                        mDialogTv.setText(R.string.set_contact_alert);
                        break;
                    case ACCESS_CONTACT_ALERT_DIALOG:
                        mDialogPosBtn.setVisibility(View.VISIBLE);
                        mDialogPosBtn.setText(R.string.allow);
                        mDialogNegBtn.setVisibility(View.VISIBLE);
                        mDialogNegBtn.setText(R.string.cancel);
                        mDialogTv.setText(R.string.access_contact_alert);
                        break;

                }
                return mView;
        }
    }

    private void setDialogProperties(int gravity, int layoutWidth) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | gravity);
            WindowManager.LayoutParams mLayoutParams = getDialog().getWindow().getAttributes();
            mLayoutParams.width = layoutWidth;
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(mLayoutParams);
        }
    }

    public void showFBVideoShareDialog(Uri mVideoUri, Context context) {
        //Uri ur = Uri.parse(mVideoUri.toString().replace("/video","/Kiran.mp4"));
        //if (isAdded()) {

        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(mVideoUri)
                .build();

        ShareVideoContent videoContent;
        shareFBDialog = new ShareDialog((Activity) context);

        if (mShareContent != null) {

            mShareContent = mShareContent.replace(" ", "#");
            String mShareTxt = "#" + mShareContent;
            videoContent = new ShareVideoContent.Builder()
                    .setVideo(video)
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag(mShareTxt)
                            .build())
                    .setContentTitle(mShareContent)
                    .build();
        } else {
            videoContent = new ShareVideoContent.Builder()
                    .setVideo(video)
                    .build();
        }

        shareFBDialog.show(videoContent, ShareDialog.Mode.AUTOMATIC);
        //}

    }

    void showFBShareDialog(String content) {

        shareFBDialog = new ShareDialog(this);

        if (mVideoUrl != null) {

            if (ShareDialog.canShow(ShareVideoContent.class)) {

                ((BaseActivity) getActivity()).getUriVideo(mVideoUrl);
                //getUriVideo(mVideoUrl);


            } else {
                Toast.makeText(getContext(), "Please install the Facebook app for sharing photos.", Toast.LENGTH_SHORT).show();
            }

        } else if (mShareImage != null) {

            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhoto[] photo = new SharePhoto[mShareImage.size()];
                ArrayList<SharePhoto> mSharePhotoList = new ArrayList<>();
                SharePhotoContent shareContent;

                for (int i = 0; i < mShareImage.size(); i++) {
                    photo[i] = new SharePhoto.Builder()
                            .setBitmap(mShareImage.get(i))
                            .build();
                }

                mSharePhotoList.addAll(Arrays.asList(photo).subList(0, mShareImage.size()));
                if (content != null) {
                    //content = content.replace(" ", "#");
                    String mShareTxt = "#" + content;
                    shareContent = new SharePhotoContent.Builder()
                            .addPhotos(mSharePhotoList)
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag(mShareTxt)
                                    .build())
                            .build();
                } else {
                    shareContent = new SharePhotoContent.Builder()
                            .addPhotos(mSharePhotoList)
                            .build();
                }

                shareFBDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);


            } else {
                Toast.makeText(getContext(), "Please install the Facebook app for sharing photos.", Toast.LENGTH_SHORT).show();
            }

        } else {

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(content)
                    .setContentUrl(Uri.parse("www.pickzy.com"))
                    .build();
            shareFBDialog.show(linkContent);

        }

    }

    @Optional
    @OnClick({R.id.cameraLayout, R.id.close_btn, R.id.save_btn, R.id.galleryLayout, R.id.dialog_positive_btn, R.id.dialog_negative_btn,
            R.id.done_btn, R.id.buy_monthly_btn, R.id.buy_yearly_btn, R.id.editLayout, R.id.deleteLayout, R.id.un_follow_btn, R.id.block_btn,
            R.id.tag_profiles_done_btn, R.id.ok_no_session_tv, R.id.facebook_lay, R.id.Friends_lay, R.id.txtvwCancel, R.id.btnGoLive, R.id.btnGoWatch,
            R.id.searchSponsorBtn, R.id.send_email_btn, R.id.cameraLayout_video, R.id.galleryLayout_video, R.id.single_stream_btn, R.id.multi_stream_btn, R.id.deleteConversationsLayout,
            R.id.reportLayout, R.id.cameraLayoutBusiness, R.id.galleryLayoutBusiness})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_negative_btn:
            case R.id.close_btn:
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            case R.id.save_btn:
                dismiss();
                break;
            case R.id.searchSponsorBtn:
                //  searchSponsors();
                break;
            case R.id.send_email_btn:
                String mEmailAddr = mSponsorEmailEt.getText().toString();
                if (mEmailAddr.isEmpty()) {
                    ((BaseActivity) getActivity()).showToast(getContext(), getContext().getString(R.string.enter_email_err));
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddr).matches()) {
                    ((BaseActivity) getActivity()).showToast(getContext(), getContext().getString(R.string.enter_valid_email_err));
                    return;
                }

                Intent mIntent = new Intent(Intent.ACTION_SENDTO);
                mIntent.setData(Uri.parse("mailto:"));
                mIntent.putExtra(Intent.EXTRA_EMAIL, mEmailAddr);
                if (mIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivity(mIntent);
                }
                break;
            case R.id.cameraLayoutBusiness:
            case R.id.cameraLayout:
                ((BaseActivity) getActivity()).cameraIntent();
                dismiss();
                break;
            case R.id.galleryLayoutBusiness:
                ((BaseActivity) getActivity()).galleryIntentMultiple();
                dismiss();
                break;
            case R.id.galleryLayout:
                ((BaseActivity) getActivity()).galleryIntent();
                dismiss();
                break;
            case R.id.cameraLayout_video:
                ((BaseActivity) getActivity()).cameraIntentVideo();
                dismiss();
                break;
            case R.id.galleryLayout_video:
                ((BaseActivity) getActivity()).galleryIntentVideo();
                dismiss();
                break;
            case R.id.dialog_positive_btn:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, 0);
                dismiss();
                break;
            case R.id.done_btn:
                StringBuilder mProfileTypesStr = new StringBuilder();
                for (int i = 0; i < mProfileTypes.size(); i++) {
                    if (i == (mProfileTypes.size() - 1)) {
                        mProfileTypesStr.append(mProfileTypes.get(i));
                    } else {
                        mProfileTypesStr.append(mProfileTypes.get(i));
                        mProfileTypesStr.append(", ");
                    }
                }
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, mProfileTypesStr, mProfileTypes, 0);
                dismiss();
                break;
            case R.id.buy_monthly_btn:
                //((BaseActivity) getActivity()).makeInAppPurchaseCall(mProfileTypes.get(3));
                dismiss();
                break;
            case R.id.buy_yearly_btn:
                //  ((BaseActivity) getActivity()).makeInAppPurchaseCall(mProfileTypes.get(4));
                dismiss();
                break;
            case R.id.deleteLayout:
                if (mDialogType.equals(BOTTOM_VIEW_PHOTOS_VIDEOS_DIALOG)) {
                    ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), BOTTOM_VIEW_VIDEOS_DIALOG, null, null, 0);
                } else {
                    ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), BOTTOM_DELETE_DIALOG, null, null, Integer.valueOf(mProfileTypes.get(0)));
                }
                dismiss();
                break;
            case R.id.editLayout:
                if (mDialogType.equals(BOTTOM_VIEW_PHOTOS_VIDEOS_DIALOG)) {
                    ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), BOTTOM_VIEW_PHOTOS_DIALOG, null, null, 1);
                } else {
                    ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), BOTTOM_EDIT_DIALOG, null, null, Integer.valueOf(mProfileTypes.get(0)));
                }
                dismiss();
                break;
            case R.id.reportLayout:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), BOTTOM_REPORT_ACTION_DIALOG, null, null, Integer.valueOf(mProfileTypes.get(0)));
                dismiss();
                break;
            case R.id.un_follow_btn:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, 0);
                break;
            case R.id.block_btn:
                ((BaseActivity) getActivity()).alertDialogNegativeBtnClick();
                break;
            case R.id.tag_profiles_done_btn:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, 0);
                break;
            case R.id.ok_no_session_tv:
                dismiss();
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, 0);
                break;
            case R.id.facebook_lay:
                showFBShareDialog(mShareContent);
                dismiss();
                break;
            case R.id.Friends_lay:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, mProfilePosition);
                dismiss();
                break;
            case R.id.txtvwCancel:
                dismiss();
                break;
            case R.id.btnGoLive:
                dismiss();
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, mProfilePosition);
                break;
            case R.id.btnGoWatch:
                dismiss();
                Intent mGoWatchActivity = new Intent(getActivity(), ViewLiveVideoViewScreen.class);
                startActivity(mGoWatchActivity);
                break;

            case R.id.deleteConversationsLayout:
                singleChatMsgResModel.setDeleteFlag(true);
                SingleChatRoomModel singleChatRoomModel = new SingleChatRoomModel();
                List<SingleChatRoomResModel> list = new ArrayList<>();
                list.add(singleChatMsgResModel);
                singleChatRoomModel.setResource(list);
                RetrofitClient.getRetrofitInstance().callCreateSingleChatRoom((BaseActivity) getActivity(), singleChatRoomModel, RetrofitClient.CREATE_SINGLE_CHAT_ROOM);
                dismiss();
                break;
            case R.id.single_stream_btn:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), mDialogType, null, null, mProfilePosition);

                break;
            case R.id.multi_stream_btn:
                ((BaseActivity) getActivity()).alertDialogPositiveBtnClick((BaseActivity) getActivity(), BOTTOM_LIVE_STREAM_OPTION_MULTI, null, null, mProfilePosition);

                break;
        }
    }

    private void searchSponsors() {
        String mSearchSponsorStr = mSearchSponsorEt.getText().toString().trim();
        if (!mSearchSponsorStr.isEmpty()) {
            mTagSponsorsList.clear();
            mTagSponsorsHorAdapter.setShowProgressBar(true);
            mTagSponsorsHorAdapter.setShowNoSponsorsFoundText(false);
            mTagSponsorsHorAdapter.notifyDataSetChanged();
            String mFilter = "name LIKE '%" + mSearchSponsorStr + "%'";
            RetrofitClient.getRetrofitInstance().callGetPromotersList(this, mFilter, GET_PROMOTERS_RESPONSE);
        }
    }

    @Override
    public void addSelectedSponsorsToTaggedList(int adapterPosition) {
        if (!mTaggedSponsorsList.contains(mTagSponsorsList.get(adapterPosition))) {
            mTaggedSponsorsList.add(mTagSponsorsList.get(adapterPosition));
            mTaggedSponsorsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void removeCancelledSponsorsFromTaggedList(int adapterPosition) {
        mTaggedSponsorsList.remove(mTagSponsorsList.get(adapterPosition));
        mTaggedSponsorsAdapter.notifyDataSetChanged();
    }

    @Override
    public void deselectTagSponsorItemFromTagSponsorsList(int adapterPosition) {
        if (mTagSponsorsList.contains(mTaggedSponsorsList.get(adapterPosition))) {
            mTagSponsorsList.get(mTagSponsorsList.indexOf(mTaggedSponsorsList.get(adapterPosition))).setIsSelected(false);
            mTagSponsorsHorAdapter.notifyDataSetChanged();
        }
    }

    @Optional
    @OnCheckedChanged({R.id.bike_cb, R.id.boat_cb, R.id.car_cb, R.id.kart_cb})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bike_cb:
                addOrRemoveProfileTypes(BaseActivity.BIKE_STR, isChecked);
                break;
            case R.id.boat_cb:
                addOrRemoveProfileTypes(BaseActivity.BOAT_STR, isChecked);
                break;
            case R.id.car_cb:
                addOrRemoveProfileTypes(BaseActivity.CAR_STR, isChecked);
                break;
            case R.id.kart_cb:
                addOrRemoveProfileTypes(BaseActivity.KART_STR, isChecked);
                break;
        }
    }

    private void addOrRemoveProfileTypes(String profile_type, boolean isChecked) {
        if (isChecked) {
            if (!mProfileTypes.contains(profile_type)) {
                mProfileTypes.add(profile_type);
            }
        } else {
            mProfileTypes.remove(profile_type);
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
    public void retrofitOnResponse(Object responseObj, int responseType) {

        if (responseObj instanceof PromotersModel) {

            PromotersModel mPromotersModel = (PromotersModel) responseObj;

            switch (responseType) {

                case RetrofitClient.GET_PROMOTERS_RESPONSE:

                    mTagSponsorsHorAdapter.setShowProgressBar(false);

                    if (mPromotersModel.getResource() != null && mPromotersModel.getResource().size() > 0) {
                        mTagSponsorsList.addAll(mPromotersModel.getResource());
                    } else {
                        mTagSponsorsHorAdapter.setShowNoSponsorsFoundText(true);
                    }

                    mTagSponsorsHorAdapter.notifyDataSetChanged();

                    break;

            }

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(getActivity()).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(getActivity()).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

        } else if (responseObj instanceof SingleChatRoomModel) {
            SingleChatRoomModel singleChatRoomModel = (SingleChatRoomModel) responseObj;
        }

    }

    @Override
    public void retrofitOnError(int code, String message) {

        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession((BaseActivity) getActivity(), RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            ((BaseActivity) getActivity()).showToast(getActivity(), mErrorMsg);
        }

    }

    @Override
    public void retrofitOnSessionError(int code, String message) {

        String mErrorMsg = code + " - " + message;
        ((BaseActivity) getActivity()).showToast(getActivity(), mErrorMsg);

    }

    @Override
    public void retrofitOnFailure() {
        ((BaseActivity) getActivity()).showToast(getActivity(), getString(R.string.internet_failure));
    }


    /*public void getUriVideo(final String[] videoUrl) {
        mShareVideoUrl = videoUrl;
        // PERMISSION_ACTION_TYPE = PERMISSION_SHARING_WRITE_ACCESS;
        if (((BaseActivity) getActivity()).isPermissionAdded()) {

            final String uri = (UrlUtils.FILE_URL + videoUrl[0] + "?api_key="
                    + getResources().getString(R.string.dream_factory_api_key)
                    + "&session_token="
                    + PreferenceUtils.getInstance(getActivity()).getStrData(PreferenceUtils
                    .SESSION_TOKEN) + "&download=" + true);


            new AsyncTask<Void, Void, Void>() {
                File apkStorage = null;
                File outputFile = null;

                @Override
                protected void onPreExecute() {
                    DialogManager.showProgress(getActivity());
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... Void) {
                    String[] mUrl = videoUrl[0].split("/");

                    try {
                        URL url = new URL(uri);//Create Download URl
                        HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                        c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                        c.connect();//connect the URL Connection

                        //If Connection response is not OK then show Logs
                        if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        }

                        //Get File if SD card is present
                        if (((BaseActivity) getActivity()).isSDCardPresent()) {

                            apkStorage = new File(
                                    Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES)
                                            + getString(R.string.util_app_folder_root_path));
                        } else
                            Toast.makeText(getActivity(), "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                        //If File is not present create directory
                        if (!apkStorage.exists()) {
                            apkStorage.mkdir();
                        }

                        outputFile = new File(apkStorage, mUrl[1]);//Create Output file in Main File

                        //Create New File if not present
                        if (!outputFile.exists()) {
                            outputFile.createNewFile();
                        } else {
                            return null;
                        }

                        FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                        InputStream is = c.getInputStream();//Get InputStream for connection

                        byte[] buffer = new byte[1024];//Set buffer type
                        int len1 = 0;//init length
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);//Write new file
                        }

                        //Close all connection after doing task
                        fos.close();
                        is.close();
                        mVideoFileUri = Uri.fromFile(outputFile);

                    } catch (Exception e) {
                        //Read exception if something went wrong
                        e.printStackTrace();
                        outputFile = null;
                    }


                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    DialogManager.hideProgress();
                    if (outputFile != null) {
                        mVideoFileUri = Uri.fromFile(outputFile);
                        showFBVideoShareDialog(mVideoFileUri);
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!! Video could not been shared.", Toast.LENGTH_LONG).show();
                    }
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }
    }*/

    @Override
    public void SendData(Uri uri, Context context) {
        Log.i("Video URi", "The uri is " + uri);
        showFBVideoShareDialog(uri, context);
    }
}

