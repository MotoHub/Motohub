package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SingleChatMsg implements Serializable {

    @SerializedName("Order")
    @Expose
    private int mOrder;

    @SerializedName("ID")
    @Expose
    private int mID;

    @SerializedName("singlechatmsg_by_Order")
    @Expose
    private SingleChatMsgOrder msinglechatmsg_by_Order;

    public int getmOrder() {
        return mOrder;
    }

    public void setmOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public SingleChatMsgOrder getMsinglechatmsg_by_Order() {
        return msinglechatmsg_by_Order;
    }

    public void setMsinglechatmsg_by_Order(SingleChatMsgOrder msinglechatmsg_by_Order) {
        this.msinglechatmsg_by_Order = msinglechatmsg_by_Order;
    }

}