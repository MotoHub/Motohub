package online.motohub.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pyd10 on 10/31/2017.
 */

public class GetDeviceStreamModel implements Serializable {

    @SerializedName("EasyDarwin")
    public EasyDarwin mEasyDarwin;

    public class Body {
        public String Protocol;
        public String URL;

        public String getProtocol() {
            return Protocol;
        }

        public void setProtocol(String protocol) {
            Protocol = protocol;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }
    }

    public class EasyDarwin {
        @SerializedName("Body")
        public Body mBody;
        @SerializedName("Header")
        public Header mHeader;
    }


}




