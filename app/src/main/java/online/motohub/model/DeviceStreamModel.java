package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pyd10 on 10/31/2017.
 */

public class DeviceStreamModel implements Serializable {

    public class Body{
        public String Channel;
        public String Reserve;
        public String Serial;
        public String Service;
    }

    public class EasyDarwin{
        @SerializedName("Body")
        public Body mBody;
        @SerializedName("Header")
        public Header mHeader;
    }

    @SerializedName("EasyDarwin")
    public EasyDarwin mEasyDarwin;

}
