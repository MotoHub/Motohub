package online.motohub.vlc;

public interface VlcListener {

    void onPlaying();

    void onError();

    void onBuffer();

    void onOpening();

    void onStopped();
}
