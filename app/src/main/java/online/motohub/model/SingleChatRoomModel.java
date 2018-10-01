package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleChatRoomModel {

    public static final String SINGLE_CHAT_ROOM_RES_MODEL = "single_chat_room_res_model";

    public static final String SINGLE_CHAT_ROOM_ID = "single_chat_room_ids";

    @SerializedName("resource")
    @Expose
    private List<SingleChatRoomResModel> resource = null;

    public List<SingleChatRoomResModel> getResource() {
        return resource;
    }

    public void setResource(List<SingleChatRoomResModel> resource) {
        this.resource = resource;
    }

}
