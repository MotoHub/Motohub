package com.pedro.vlc;

/**
 * Created by pedro on 25/06/17.
 */
public interface VlcListener {

    void onPlaying();

    void onError();

    void onBuffer();

    void onOpening();

    void onStopped();
}
