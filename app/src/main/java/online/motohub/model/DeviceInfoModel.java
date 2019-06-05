package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pyd10 on 10/31/2017.
 */

public class DeviceInfoModel implements Serializable {

    @SerializedName("EasyDarwin")
    public EasyDarwin mEasyDarwin;

    public class EasyDarwin {
        @SerializedName("Body")
        public Body mBody;
        @SerializedName("Header")
        public Header mHeader;
    }

    public class Body {
        public String Serial;
        public String SnapURL;
    }


}
