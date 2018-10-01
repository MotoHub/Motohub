package online.motohub.interfaces;

public interface RetrofitResInterface {

    void retrofitOnResponse(Object responseObj, int responseType);

    void retrofitOnError(int code, String message);

    void retrofitOnSessionError(int code, String message);

    void retrofitOnFailure();

}
