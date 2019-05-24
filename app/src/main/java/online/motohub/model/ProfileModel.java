package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProfileModel implements Serializable {

    public static final String OTHERS_PROFILE_RES_MODEL = "ProfileResModel";
    public static final String MY_PROFILE_RES_MODEL = "MyProfileResModel";
    public static final String PROFILE_CREATED_AFTER_REGISTRATION =
            "ProfileCreatedAfterRegistration";
    public static final String IS_COVER_PICTURE = "IsCoverPicture";
    public static final String SPINNER_LIST = "SpinnerList";

    public static final String ID = "ID";
    public static final String PROFILE_PICTURE = "ProfilePicture";
    public static final String COVER_PICTURE = "CoverPicture";
    public static final String DRIVER = "Driver";
    public static final String MOTO_NAME = "MotoName";
    public static final String SPECTATOR_NAME = "SpectatorName";
    public static final String MAKE = "Make";
    public static final String MODEL = "Model";
    public static final String HP = "HP";
    public static final String ENGINE_SPECS = "EngineSpecs";
    public static final String PANEL_AND_PAINT = "PanelPaint";
    public static final String WHEELS_AND_TYRES = "WheelsTyres";
    public static final String MORE_INFORMATION = "MoreInformation";
    public static final String PROFILE_TYPE = "ProfileType";
    public static final String USER_ID = "UserID";
    public static final String PURCHASED = "Purchased";
    public static final String PURCHASE_DATE = "PurchaseDate";
    public static final String PURCHASE_TYPE = "PurchaseType";
    public static final String FOLLOWING = "Following";
    public static final String FOLLOWERS = "Followers";
    public static final String FOLLOWER_NEW = "FollowerNew";
    public static final String ECU = "ECU";
    public static final String GEARBOX = "GearBox";
    public static final String DIFFERENTIAL = "Differencial";
    public static final String INJECTION = "Injection";
    public static final String CLUTCH = "Clutch";
    public static final String FUELSYSTEM = "FuelSystem";
    public static final String TURBO = "Turbo";
    public static final String SUSPENSION = "Suspension";
    public static final String TRAILER = "Trailer";

    public static final String TYRES = "Tyres";
    public static final String EXHAUST = "Exhaust";
    public static final String FORKS = "Forks";
    public static final String SHOCK = "Shock";
    public static final String LAPTIMER = "LapTimer";
    public static final String REARSETS = "RearSets";
    public static final String HANDLEBARS = "HandleBars";
    public static final String CHAIN = "Chain";
    public static final String FAIRINGS = "Fairings";
    public static final String SPROCKETS = "Sprockets";
    public static final String ENGINECOVERS = "EngineCovers";
    public static final String BRAKEMASTER = "BrakeMaster";
    public static final String BRAKEPAD = "BrakePads";
    public static final String FILTER = "Filter";
    public static final String BRAKEFLUID = "BrakeFluid";
    public static final String ENGINEOIL = "EngineOil";
    public static final String CHAINLUBE = "ChainLube";

    public static final String BOOTS = "Boots";
    public static final String HELMET = "Helmet";
    public static final String SUIT = "Suit";
    public static final String GLOVES = "Gloves";
    public static final String BACKPROTECTION = "BackProtection";
    public static final String CHESTPROTECTION = "ChestProtection";

    public static final String IS_FOLLOWING = "isFollowing";
    public static final String BLOCKED = "isBlocked";

    public static final String VEHICLE_INFO_LIKES_BY_ID = "vehicleinfolikes_by_LikedProfileID";
    public static final String PROFILE_ID = "ProfileID";
    public static final String FULL_PROFILE_LIST = "FullProfileList";
    public static final String PROMOTER_FOLLOWER_BY_PROFILE_ID = "promoterfollower_by_ProfileID";
    public static final String PHONE = "PhoneNumber";
    public static final String ActiveUserCount = "ActiveUserCount";

    @SerializedName("resource")
    @Expose
    private ArrayList<ProfileResModel> mResource = null;

    @SerializedName("meta")
    @Expose
    private MetaModel meta;

    public ArrayList<ProfileResModel> getResource() {
        if (mResource == null)
            mResource = new ArrayList<>();
        return mResource;
    }

    public void setResource(ArrayList<ProfileResModel> resource) {
        this.mResource = resource;
    }

    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }

}
