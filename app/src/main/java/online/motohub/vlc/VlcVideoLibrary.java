package online.motohub.vlc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

/**
 * Play and stop need be in other thread or app can freeze
 */
public class VlcVideoLibrary implements MediaPlayer.EventListener {

    private int width = 0, height = 0;
    private LibVLC vlcInstance;
    private MediaPlayer player;
    private VlcListener vlcListener;
    //The library will select one of this class for rendering depend of constructor called
    private SurfaceView surfaceView;
    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private SurfaceHolder surfaceHolder;

    public VlcVideoLibrary(Context context, VlcListener vlcListener, SurfaceView surfaceView) {
        this.vlcListener = vlcListener;
        this.surfaceView = surfaceView;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public VlcVideoLibrary(Context context, VlcListener vlcListener, TextureView textureView) {
        this.vlcListener = vlcListener;
        this.textureView = textureView;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public VlcVideoLibrary(Context context, VlcListener vlcListener, SurfaceTexture surfaceTexture) {
        this.vlcListener = vlcListener;
        this.surfaceTexture = surfaceTexture;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public VlcVideoLibrary(Context context, VlcListener vlcListener, Surface surface) {
        this.vlcListener = vlcListener;
        this.surface = surface;
        surfaceHolder = null;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public VlcVideoLibrary(Context context, VlcListener vlcListener, Surface surface,
                           SurfaceHolder surfaceHolder) {
        this.vlcListener = vlcListener;
        this.surface = surface;
        this.surfaceHolder = surfaceHolder;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public VlcVideoLibrary(Context context, VlcListener vlcListener, Surface surface, int width,
                           int height) {
        this.vlcListener = vlcListener;
        this.surface = surface;
        this.width = width;
        this.height = height;
        surfaceHolder = null;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public VlcVideoLibrary(Context context, VlcListener vlcListener, Surface surface,
                           SurfaceHolder surfaceHolder, int width, int height) {
        this.vlcListener = vlcListener;
        this.surface = surface;
        this.surfaceHolder = surfaceHolder;
        this.width = width;
        this.height = height;
        vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
    }

    public boolean isPlaying() {
        return vlcInstance != null && player != null && player.isPlaying();
    }

    public void play(String endPoint) {
        if (player == null || player.isReleased()) {
            setMedia(new Media(vlcInstance, Uri.parse(endPoint)));
        } else if (!player.isPlaying()) {
            player.play();
        }
    }

    public void stop() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
        }
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    private void setMedia(Media media) {
        //delay = network buffer + file buffer
        //media.addOption(":network-caching=" + Constants.BUFFER);
        //media.addOption(":file-caching=" + Constants.BUFFER);
        media.addOption(":fullscreen");
        media.setHWDecoderEnabled(true, false);
        player = new MediaPlayer(vlcInstance);
        player.setMedia(media);
        player.setEventListener(this);

        IVLCVout vlcOut = player.getVLCVout();
        //set correct class for render depend of constructor called
        if (surfaceView != null) {
            vlcOut.setVideoView(surfaceView);
            width = surfaceView.getWidth();
            height = surfaceView.getHeight();
        } else if (textureView != null) {
            vlcOut.setVideoView(textureView);
            width = textureView.getWidth();
            height = textureView.getHeight();
        } else if (surfaceTexture != null) {
            vlcOut.setVideoSurface(surfaceTexture);
        } else if (surface != null) {
            vlcOut.setVideoSurface(surface, surfaceHolder);
        } else {
            throw new RuntimeException("You cant set a null render object");
        }
        if (width != 0 && height != 0) vlcOut.setWindowSize(width, height);
        vlcOut.attachViews();
        player.setVideoTrackEnabled(true);
        player.play();
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        switch (event.type) {
            case MediaPlayer.Event.Stopped:
                vlcListener.onStopped();
                break;
            case MediaPlayer.Event.Playing:
                vlcListener.onPlaying();
                break;
            case MediaPlayer.Event.EncounteredError:
                vlcListener.onError();
                break;
            case MediaPlayer.Event.Buffering:
                vlcListener.onBuffer();
                break;
            case MediaPlayer.Event.Opening:
                vlcListener.onOpening();
                break;
            default:
                break;
        }
    }
}