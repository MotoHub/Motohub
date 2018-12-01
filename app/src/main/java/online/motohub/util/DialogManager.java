package online.motohub.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.EventLivePromoterChatAdapter;
import online.motohub.interfaces.CommonInterface;
import online.motohub.interfaces.CommonReturnInterface;
import online.motohub.interfaces.SharePostInterface;
import online.motohub.model.EventGrpChatMsgResModel;
import online.motohub.model.ProfileResModel;


public class DialogManager extends BasePopup {

    static Dialog mLiveOptionDialog;
    static Dialog mUpgradeRacerOptionDialog;
    static Dialog mAlertDialog;
    static Dialog mProgressDialog;
    static Dialog mPromoterChatDialog;

    public static Dialog getDialog(Context mContext, int mLayout) {

        Dialog mDialog = new Dialog(mContext, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mDialog.setContentView(mLayout);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        return mDialog;
    }

    public static Dialog getLoaderDialog(Context mContext, int mLayout) {

        Dialog mDialog = new Dialog(mContext, R.style.MyProgressBarTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mDialog.setContentView(mLayout);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        return mDialog;
    }

    public static void showMultiLiveOptionPopup(final Context mContext, final CommonReturnInterface mCommonReturnInterface, String text1, String text2) {
        if (mLiveOptionDialog != null && mLiveOptionDialog.isShowing()) {
            mLiveOptionDialog.dismiss();
        }
        mLiveOptionDialog = getDialog(mContext, R.layout.live_stream_option_layout);
        mLiveOptionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        Button mSingleLiveBtn = mLiveOptionDialog.findViewById(R.id.single_stream_btn);
        mSingleLiveBtn.setText(text1);
        Button mMultiLiveBtn = mLiveOptionDialog.findViewById(R.id.multi_stream_btn);
        mMultiLiveBtn.setText(text2);
        mSingleLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveOptionDialog.dismiss();
                mCommonReturnInterface.onSuccess(1);
            }
        });
        mMultiLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveOptionDialog.dismiss();
                mCommonReturnInterface.onSuccess(2);
            }
        });
        mLiveOptionDialog.show();
    }

    public static void showLiveOptionPopup(final Context mContext, final CommonReturnInterface mCommonReturnInterface) {
        if (mLiveOptionDialog != null && mLiveOptionDialog.isShowing()) {
            mLiveOptionDialog.dismiss();
        }
        mLiveOptionDialog = getDialog(mContext, R.layout.live_stream_layout);
        mLiveOptionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        Button mStartLiveBtn = mLiveOptionDialog.findViewById(R.id.btnGoLive);
        Button mViewLiveBtn = mLiveOptionDialog.findViewById(R.id.btnGoWatch);

        mStartLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveOptionDialog.dismiss();
                mCommonReturnInterface.onSuccess(1);
            }
        });
        mViewLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveOptionDialog.dismiss();
                mCommonReturnInterface.onSuccess(2);
            }
        });
        mLiveOptionDialog.show();
    }

    public static void showUpgradeRacerOptionPopup(final Context mContext, final CommonReturnInterface mCommonReturnInterface) {
        if (mUpgradeRacerOptionDialog != null && mUpgradeRacerOptionDialog.isShowing()) {
            mUpgradeRacerOptionDialog.dismiss();
        }
        mUpgradeRacerOptionDialog = getDialog(mContext, R.layout.popup_upgrade_racer);
        mUpgradeRacerOptionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;

        TextView mContentTxt = mUpgradeRacerOptionDialog.findViewById(R.id.content_txt);

        Button mUpgradeBtn = mUpgradeRacerOptionDialog.findViewById(R.id.upgrade_btn);
        Button mPayBtn = mUpgradeRacerOptionDialog.findViewById(R.id.pay_btn);
        Button mCancelBtn = mUpgradeRacerOptionDialog.findViewById(R.id.cancel_btn);

        mContentTxt.setText(mContext.getString(R.string.purchase_racer_profile_win_car) + "4.99 to enter");
        mPayBtn.setText(mContext.getString(R.string.pay) + "4.99");

        mUpgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpgradeRacerOptionDialog.dismiss();
                mCommonReturnInterface.onSuccess(1);
            }
        });
        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpgradeRacerOptionDialog.dismiss();
                mCommonReturnInterface.onSuccess(2);
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpgradeRacerOptionDialog.dismiss();
            }
        });
        mUpgradeRacerOptionDialog.show();
    }

    private static Dialog getLoadingDialog(Context mContext, int mLay) {

        Dialog mDialog = getLoaderDialog(mContext, mLay);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        return mDialog;
    }

    public static void showProgress(Context context) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        try {
            mProgressDialog = getLoadingDialog(context, R.layout.progress);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
            mProgressDialog.show();
        } catch (Exception e) {
            Log.e(AppConstants.TAG, e.getMessage());
        }

    }

    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                Log.e(AppConstants.TAG, e.getMessage());
            }
        }
    }

    public static void showShareDialogWithCallback(final Context mContext, final SharePostInterface mCallback) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = getDialog(mContext, R.layout.popup_share_post_alert);
        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        mAlertDialog.setCanceledOnTouchOutside(false);
        TextView mMessageTxt = mAlertDialog
                .findViewById(R.id.msg_txt);
        Button mOkBtn = mAlertDialog.findViewById(R.id.btnDone);
        Button mSkipBtn = mAlertDialog.findViewById(R.id.btnSkip);
        final AppCompatEditText shareEdt = mAlertDialog.findViewById(R.id.shareEdt);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = shareEdt.getText().toString();
                if (msg.trim().isEmpty()) {
                    ((BaseActivity) mContext).showToast(mContext, mContext.getString(R.string.share_text));
                } else {
                    mCallback.onSuccess(msg);
                    mAlertDialog.dismiss();
                }
            }
        });
        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSuccess("");
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog.show();
    }

    public static void showAlertDialogWithCallback(Context mContext, final CommonInterface mCallback, String mMessage) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = getDialog(mContext, R.layout.popup_common_alert);
        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        TextView mMessageTxt = mAlertDialog
                .findViewById(R.id.msg_txt);
        Button mOkBtn = mAlertDialog.findViewById(R.id.ok_btn);

        mMessageTxt.setText(mMessage);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

            }
        });
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCallback.onSuccess();
            }
        });
        mAlertDialog.show();
    }

    public static void showRetryAlertDialogWithCallback(Context mContext, final CommonInterface mCallback, String mMessage) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = getDialog(mContext, R.layout.popup_common_alert);
        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        TextView mMessageTxt = mAlertDialog
                .findViewById(R.id.msg_txt);
        Button mOkBtn = mAlertDialog.findViewById(R.id.ok_btn);
        mOkBtn.setText(R.string.retry);
        mMessageTxt.setText(mMessage);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSuccess();
                mAlertDialog.dismiss();

            }
        });
        mAlertDialog.show();
    }

    public static void showUnFollowBlockDialogWithCallback(Context mContext, final CommonReturnInterface mCallback, ProfileResModel mProfileEntity) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = getDialog(mContext, R.layout.dialog_profile_view);
        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        TextView mProfileNameTxt = mAlertDialog
                .findViewById(R.id.profile_name_tv);
        ImageButton mCloseBtn = mAlertDialog
                .findViewById(R.id.close_btn);
        CircleImageView mProfileImg = mAlertDialog
                .findViewById(R.id.profile_img);

        TextView mUnFollowBtn = mAlertDialog
                .findViewById(R.id.un_follow_btn);

        TextView mBlockBtn = mAlertDialog
                .findViewById(R.id.block_btn);
        mProfileNameTxt.setText(Utility.getInstance().getUserName(mProfileEntity));

        if (!mProfileEntity.getProfilePicture().isEmpty())
            ((BaseActivity) mContext).setImageWithGlide(mProfileImg, mProfileEntity.getProfilePicture(), R.drawable.default_profile_icon);

        mUnFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSuccess(1);
                mAlertDialog.dismiss();
            }
        });
        mBlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSuccess(2);
                mAlertDialog.dismiss();
            }
        });
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public static void showPromoterChatPopup(final Context mContext, ArrayList<EventGrpChatMsgResModel> mPromoterChatList, String mEventName) {
        if (mPromoterChatDialog != null && mPromoterChatDialog.isShowing()) {
            mPromoterChatDialog.dismiss();
        }
        mPromoterChatDialog = getDialog(mContext, R.layout.dialog_promoter_chat);
        mPromoterChatDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromBottom;
        ImageButton mCloseBtn = mPromoterChatDialog.findViewById(R.id.close_btn);

        TextView mTitleTv = mPromoterChatDialog.findViewById(R.id.title_tv);
        mTitleTv.setText(mEventName);
        RecyclerView mPromoterChatRv = mPromoterChatDialog.findViewById(R.id.rvEventChat);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mPromoterChatRv.setLayoutManager(mLayoutManager);
        mPromoterChatRv.setItemAnimator(new DefaultItemAnimator());

        EventLivePromoterChatAdapter mAdapter = new EventLivePromoterChatAdapter(mContext, mPromoterChatList);
        mPromoterChatRv.setAdapter(mAdapter);

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPromoterChatDialog.dismiss();
            }
        });
        mPromoterChatDialog.show();
    }

    public static void showSaveToMyVideos(final Context mContext, final CommonReturnInterface mCallback, int mUserId, int mPostUserId) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = getDialog(mContext, R.layout.popup_save_my_videos);
        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.Theme_AppCompat_Dialog;


        Button mSaveMyVideosBtn = mAlertDialog.findViewById(R.id.save_my_video_btn);
        Button mReportBtn = mAlertDialog.findViewById(R.id.report_btn);
        if (mUserId == mPostUserId) {
            mReportBtn.setVisibility(View.GONE);
        } else {
            mReportBtn.setVisibility(View.VISIBLE);
        }


        mSaveMyVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSuccess(2);
                mAlertDialog.dismiss();
            }
        });
        mReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                mCallback.onSuccess(1);
            }
        });
        mAlertDialog.show();
    }
}
