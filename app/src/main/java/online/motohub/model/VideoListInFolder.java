package online.motohub.model;

import android.graphics.Bitmap;

public class VideoListInFolder {

    String video_ID;
    String video_Title;
    String video_Duration;
    String video_Data;
    Bitmap bitmap;
    long video_size;

    public Long getVideo_size() {
        return video_size;
    }

    public void setVideo_size(Long video_size) {
        this.video_size = video_size;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getVideo_ID() {
        return video_ID;
    }

    public void setVideo_ID(String video_ID) {
        this.video_ID = video_ID;
    }

    public String getVideo_Title() {
        return video_Title;
    }

    public void setVideo_Title(String video_Title) {
        this.video_Title = video_Title;
    }

    public String getVideo_Duration() {
        return video_Duration;
    }

    public void setVideo_Duration(String video_Duration) {
        this.video_Duration = video_Duration;
    }

    public String getVideo_Data() {
        return video_Data;
    }

    public void setVideo_Data(String video_Data) {
        this.video_Data = video_Data;
    }
}