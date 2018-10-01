package online.motohub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpResModel {

    /**
     * Sign Up keys
     */
    public static final String first_name = "first_name";
    public static final String last_name = "last_name";
    public static final String new_password = "new_password";
    public static final String email = "email";
    public static final String phone_number = "phone";

    @SerializedName("success")
    @Expose
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

}
