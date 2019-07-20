package online.motohub.bl;


import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import online.motohub.BuildConfig;
import retrofit2.Call;
import retrofit2.Response;

/**
 * This class abstracts out errors so it is easy to work on it
 * <p>
 * 1. It make sure to return ErrorMessage as String, which can be shown to user
 * 2. Model overrides error from Server
 * 3. You can override some of the server error message if you want
 * <p>
 */

public class MTError extends Exception {

    private final int serverErrorCode;
    private final String errTag = "Error: --";
    private String url_path = null;
    private MTErrorModel errorModel;
    Throwable error = null;
    final String errorMessage;

    public MTError(Throwable error, String errorMessage) {
        super();
        Log.e(errTag, "Error from server");
        this.error = error;
        this.errorMessage = errorMessage;
        this.serverErrorCode = 0;
        this.errorModel = null;
    }

    public MTError(String message, String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
        this.serverErrorCode = 0;
        this.errorModel = null;
    }

    public MTError(String message) {
        super(message);
        this.errorMessage = message;
        this.serverErrorCode = 0;
        this.errorModel = null;
    }

    public MTError(Response errorResponse) {
        super();
        ResponseBody body = errorResponse.errorBody();
        String message = "";

        try {
            if (body != null)
                message = body.string();
        } catch (IOException e) {
            Log.e(errTag, e.getMessage());
            message = "";
        }

        this.errorMessage = message;
        if (BuildConfig.DEBUG) {
            Log.e(errTag, this.errorMessage);
        }

        this.serverErrorCode = errorResponse.code();

        try {
            HttpUrl url = errorResponse.raw().request().url();
            url_path = url.encodedPath();
        } catch (Exception e) {
            Log.e(errTag, e.getMessage());
        }

        try {
            this.errorModel = new MTErrorModel(this.errorMessage);
        } catch (Exception e) {
            Log.e(errTag, e.getMessage());
            this.errorModel = null;
        }

    }

    public <T> MTError(Call<T> call, Response<T> response) {
        this(response);
        if (call != null && call.request() != null) {
            HttpUrl url = call.request().url();
            url_path = url.encodedPath();
        }
    }

    /**
     * @return Message to show to user
     */
    public String getErrorMessage() {
        String message = null;
        if (this.errorModel != null) {
            message = this.errorModel.getMessage();
        } else if (errorMessage == null) {
            message = MTErrorDefinition.ERROR_UNKNOW;
        } else {
            message = errorMessage;
        }
        if (url_path != null) {
            String overRide = ServerErrorMessageFixer.shouldOverRideMessage(message, url_path);
            if (overRide != null) return overRide;
        }
        return message;
    }

    @Override
    public String getMessage() {
        if (error != null) {
            return error.getMessage();
        }
        return super.getMessage();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        if (error != null) {
            return error.getStackTrace();
        }
        return super.getStackTrace();
    }

    @Override
    public String getLocalizedMessage() {
        if (error != null) {
            return error.getLocalizedMessage();
        }
        return super.getLocalizedMessage();
    }

    public int getServerErrorCode() {
        if (serverErrorCode == 0) {
            return getErrorCodeFromModel();
        }
        return serverErrorCode;
    }

    public MTErrorModel getErrorModel() {
        return errorModel;
    }

    public int getErrorCodeFromModel() {
        if (errorModel != null) return errorModel.error_code;
        return 0;
    }
}
