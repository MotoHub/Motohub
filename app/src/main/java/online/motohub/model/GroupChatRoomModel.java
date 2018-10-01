package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupChatRoomModel {

    public static final String GRP_CHAT_ROOM_ID = "GroupChatRoomID";
    public static final String GRP_CHAT_ROOM_RES_MODEL = "GroupChatRoomResModel";
    public static final String GRP_PICTURE = "GroupPicture";

    @SerializedName("resource")
    @Expose
    private List<GroupChatRoomResModel> mResource = null;

    public List<GroupChatRoomResModel> getResource() {
        return mResource;
    }

    public void setResource(List<GroupChatRoomResModel> resource) {
        this.mResource = resource;
    }

}
