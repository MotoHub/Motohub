package online.motohub.util;


import java.util.ArrayList;
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
                break;
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
                break;
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
}
