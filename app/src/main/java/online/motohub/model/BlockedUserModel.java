package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlockedUserModel {

    public static final String BLOCKED_PROFILE_ID = "BlockedProfileID";
    public static final String PROFILES_BY_BLOCKED_PROFILE_ID = "profiles_by_BlockedProfileID";

    @SerializedName("resource")
    @Expose
    private List<BlockedUserResModel> mResource = null;

    public List<BlockedUserResModel> getResource() {
        return mResource;
    }

    public void setResource(List<BlockedUserResModel> resource) {
        this.mResource = resource;
    }

}
