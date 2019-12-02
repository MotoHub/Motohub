package online.motohub.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import online.motohub.R;
import online.motohub.model.FolderNameVideo;
import online.motohub.model.VideoListInFolder;

public class RecycleViewFolderList extends RecyclerView.Adapter<RecycleViewFolderList.ViewHolder> {

    private static Activity activity;
    List<FolderNameVideo> data = Collections.emptyList();
    List<VideoListInFolder> videoImg = Collections.emptyList();
    private LayoutInflater inflater;
    private int lastPosition = -1;

    public RecycleViewFolderList(Activity activity, List<FolderNameVideo> data, List<VideoListInFolder> mUriList) {

        RecycleViewFolderList.activity = activity;
        this.data = data;
        this.videoImg = mUriList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.folderlist_adapter_recycle, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            FolderNameVideo current = data.get(i);
            VideoListInFolder currentVideoImg = videoImg.get(i);
            new ImageLoader().execute(currentVideoImg.getVideo_ID(), viewHolder.videoView);
            viewHolder.textNameFolder.setText(current.getFolderName());
            viewHolder.textNumberVideo.setText("Number of Videos" + ": " + current.getNumberofFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ImageLoader extends AsyncTask<Object, String, Bitmap> {

        private Bitmap bitmap = null;

        private ImageView view;


        @Override
        protected Bitmap doInBackground(Object... parameters) {

            // Get the passed arguments here
            view = (ImageView) parameters[1];
            String uri = (String) parameters[0];

            Bitmap b = MediaStore.Video.Thumbnails.getThumbnail(activity.getContentResolver(),
                    Long.parseLong(uri), MediaStore.Video.Thumbnails.MINI_KIND, null);
            // Create bitmap from passed in Uri here
            // ...
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                view.setImageBitmap(bitmap);

            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textNameFolder;
        TextView textNumberVideo;
        ImageView videoView;

        public ViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.row_picker_video_img_folder_backgroundImg);
            textNameFolder = itemView.findViewById(R.id.textNameFolder);
            textNumberVideo = itemView.findViewById(R.id.textNumberVideo);
        }
    }
}