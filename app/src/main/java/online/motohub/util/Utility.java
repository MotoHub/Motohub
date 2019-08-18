package online.motohub.util;


import android.os.SystemClock;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import online.motohub.activity.BaseActivity;
import online.motohub.application.MotoHub;
import online.motohub.model.BlockedUserResModel;
import online.motohub.model.FollowProfileEntity;
import online.motohub.model.ProfileResModel;
import online.motohub.model.promoter_club_news_media.PromoterFollowerResModel;

public class Utility {

    public static Utility getInstance() {

        return MotoHub.UTILITY_INSTANCE;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;

        }
        return isValid;
    }

    public boolean isAlreadyFollowed(ArrayList<FollowProfileEntity> mFollowersList, int mMyProfileID) {
        boolean isAlreadyFollowed = false;
        for (FollowProfileEntity mEntity : mFollowersList) {
            if (mEntity.getProfileID() == mMyProfileID) {
                isAlreadyFollowed = true;
            }
        }
        return isAlreadyFollowed;
    }

    public boolean isAlreadyFollowedMe(ArrayList<FollowProfileEntity> mMyFollowersList, int mOtherProfileID) {
        boolean isAlreadyFollowedMe = false;
        for (FollowProfileEntity mEntity : mMyFollowersList) {
            if (mEntity.getProfileID() == mOtherProfileID) {
                isAlreadyFollowedMe = true;
                break;
            }
        }
        return isAlreadyFollowedMe;
    }

    public boolean isAlreadyBlocked(ArrayList<BlockedUserResModel> mBlockedUserList, int mOtherProfileID) {
        boolean isAlreadyBlocked = false;
        for (BlockedUserResModel mEntity : mBlockedUserList) {
            if (mEntity.getBlockedProfileID() == mOtherProfileID) {
                isAlreadyBlocked = true;
            }
        }
        return isAlreadyBlocked;
    }

    public boolean isAlreadyBlockedMe(ArrayList<BlockedUserResModel> mBlockedUserList, int mOtherProfileID) {
        boolean isAlreadyBlocked = false;
        for (BlockedUserResModel mEntity : mBlockedUserList) {
            if (mEntity.getProfileID() == mOtherProfileID) {
                isAlreadyBlocked = true;
                break;
            }
        }
        return isAlreadyBlocked;
    }

    public int getUnFollowRowID(ArrayList<FollowProfileEntity> mFollowersList, int mMyProfileID, int mSelProfileID) {
        int mUnFollowRowID = 0;
        for (FollowProfileEntity mEntity : mFollowersList) {
            if (mEntity.getProfileID() == mMyProfileID && mEntity.getFollowProfileID() == mSelProfileID) {
                mUnFollowRowID = mEntity.getID();
                break;
            }
        }
        return mUnFollowRowID;
    }

    public int getUnBlockRowID(ArrayList<BlockedUserResModel> mBlockedUserList, int mMyProfileID, int mSelProfileID) {
        int mUnFollowRowID = 0;
        for (BlockedUserResModel mEntity : mBlockedUserList) {
            if (mEntity.getProfileID() == mMyProfileID && mEntity.getBlockedProfileID() == mSelProfileID) {
                mUnFollowRowID = mEntity.getID();
                break;
            }
        }
        return mUnFollowRowID;
    }

    public boolean isSpectator(ProfileResModel mProfileObj) {
        boolean isSpectator;
        isSpectator = mProfileObj != null && (mProfileObj.getProfileType() == (Integer.parseInt(BaseActivity.SPECTATOR)));
        return isSpectator;
    }

    public String getUserName(ProfileResModel mProfileObj) {
        String mUsername = "";
        if (mProfileObj != null) {
            if ((mProfileObj.getProfileType() == (Integer.parseInt(BaseActivity.SPECTATOR)))) {
                mUsername = mProfileObj.getSpectatorName();
            } else {
                mUsername = mProfileObj.getDriver();
            }
        }
        return mUsername;
    }

    public String getMyFollowersFollowingsID(ArrayList<FollowProfileEntity> mFollowersList, boolean isMyFollowers) {
        String divider = ",";
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < mFollowersList.size(); i++) {
            if (i == mFollowersList.size() - 1) {
                divider = "";
            }
            if (isMyFollowers) {
                mStringBuilder.append(mFollowersList.get(i).getProfileID()).append(divider);
            } else {
                mStringBuilder.append(mFollowersList.get(i).getFollowProfileID()).append(divider);
            }

        }
        return mStringBuilder.toString();
    }

    public String getMyBlockedUsersID(ArrayList<BlockedUserResModel> mBlockedUsersList, ArrayList<BlockedUserResModel> mBlockedMyProfileUsersList) {
        String divider = ",";
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < mBlockedUsersList.size(); i++) {
            if (i == mBlockedUsersList.size() - 1 && mBlockedMyProfileUsersList.size() == 0) {
                divider = "";
            }
            mStringBuilder.append(mBlockedUsersList.get(i).getBlockedProfileID()).append(divider);
        }
        for (int i = 0; i < mBlockedMyProfileUsersList.size(); i++) {
            if (i == mBlockedMyProfileUsersList.size() - 1) {
                divider = "";
            }
            mStringBuilder.append(mBlockedMyProfileUsersList.get(i).getProfileID()).append(divider);
        }
        return mStringBuilder.toString();
    }

    public String getMyPromoterFollowingsID(ArrayList<PromoterFollowerResModel> mBlockedUsersList) {
        String divider = ",";
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < mBlockedUsersList.size(); i++) {
            if (i == mBlockedUsersList.size() - 1) {
                divider = "";
            }
            mStringBuilder.append(mBlockedUsersList.get(i).getPromoterUserID()).append(divider);
        }
        return mStringBuilder.toString();
    }

    public String getCurrentDateTime() {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.getDefault());
//        mDateFormat.setTimeZone(TimeZone.getTimeZone(AppConstants.NZ_TIME_ZONE));
        return mDateFormat.format(new Date());
    }

    public String findTime(String createDate) {
        String diffTime = "";
        if (TextUtils.isEmpty(createDate)) {
            return diffTime;
        }
        DateFormat mServerFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mServerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        DateFormat mLocalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long current = Calendar.getInstance().getTimeInMillis();
        String currentDate = mLocalFormat.format(current);

        Date mCreateDate = null, mCurrentDate = null;

        try {
            mCreateDate = mServerFormat.parse(createDate);
            mCurrentDate = mLocalFormat.parse(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert mCurrentDate != null;
        long diff = mCurrentDate.getTime() - mCreateDate.getTime();
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        int diffInDays = (int) (diff / (60 * 60 * 1000 * 24));
        int diffInMonths = diffInDays / 30;
        int diffInYear = diffInMonths / 12;
        if (diffInYear > 0) {
            if (diffInYear == 1) {
                diffTime = diffInYear + " Year ago";
            } else {
                diffTime = diffInYear + " Years ago";
            }
        } else if (diffInMonths > 0) {
            if (diffInMonths == 1) {
                diffTime = diffInMonths + " Month ago";
            } else {
                diffTime = diffInMonths + " Months ago";
            }
        } else if (diffInDays > 0) {
            if (diffInDays == 1) {
                diffTime = diffInDays + " Day ago";
            } else if (diffInDays >= 7) {
                if (diffInDays / 7 == 1) {
                    diffTime = diffInDays / 7 + " Week ago";
                } else {
                    diffTime = diffInDays / 7 + " Weeks ago";
                }
            } else {
                diffTime = diffInDays + " Days ago";
            }
        } else if (diffHours > 0) {
            if (diffHours == 1) {
                diffTime = diffHours + " Hour ago";
            } else {
                diffTime = diffHours + " Hours ago";
            }
        } else if (diffMinutes > 0) {
            if (diffMinutes == 1) {
                diffTime = diffMinutes + " Minute ago";
            } else {
                diffTime = diffMinutes + " Minutes ago";
            }
        } else {
            diffTime = "Just Now";
        }
        return diffTime;
    }
    private long mLastClickTime = 0;
    public boolean isMultiClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
            return true;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }
    public String[] mergeArrayList(String[]... arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int destPos = 0;
        for (String[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }
        return result;
    }
    public String[] getImgVideoList(String str) {
        String[] mArray = null;
        if (!TextUtils.isEmpty(str)) {
            str = str.replace("]", "")
                    .replace("[", "")
                    .replace("\n", "")
                    .replace("\"", "")
                    .replace("\\", "")
                    .replace(" ", "");
            if (str.isEmpty())
                return null;

            mArray = str.split(",");
        }
        return mArray;
    }
}
