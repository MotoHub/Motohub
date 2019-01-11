package online.motohub.model;

import java.util.ArrayList;

/**
 * Created by Pickzy-05 on 3/13/2018.
 */

public class SpectatorLiveModel {

    public static final String TABLE = "SpectatorTable";
    public static final String ID = "id";

    public static final String CAPTION = "Caption";
    public static final String FILEURL = "VideoUrl";
    public static final String OTHERPROFILEID = "OtherProfileID";
    public static final String EVENTID = "EventID";
    public static final String USERID = "UserID";
    public static final String USERTYPE = "UserType";
    public static final String FILETYPE = "FileType";
    public static final String PROFILE_ID = "ProfileID";
    public static final String EVENT_FINISH_DATE = "EventFinishDate";
    public static final String THUMBNAIL = "Thumbnail";
    public static final String LIVE_POST_PROFILE_ID = "LivePostProfileID";

    private ArrayList<SpectatorLiveEntity> resource;

    public ArrayList<SpectatorLiveEntity> getResource() {
        return resource;
    }

    public void setResource(ArrayList<SpectatorLiveEntity> resource) {
        this.resource = resource;
    }
}
