package online.motohub.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import online.motohub.R;
import online.motohub.model.VideoListInFolder;

public class RecycleViewVideoList extends RecyclerView.Adapter<RecycleViewVideoList.ViewHolder> {

    private static Activity activity;
    List<VideoListInFolder> data = Collections.emptyList();
    private LayoutInflater inflater;
    private int lastPosition = -1;

    public RecycleViewVideoList(Activity activity, List<VideoListInFolder> data) {

        RecycleViewVideoList.activity = activity;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.videolist_adapter_recycle, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            VideoListInFolder current = data.get(i);

            viewHolder.textTitle.setText(current.getVideo_Title());

            new ImageLoader().execute(current.getVideo_ID(), viewHolder.videoView);

            long minutes = TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(current.getVideo_Duration()));

            if (minutes < 1) {

                minutes = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(current.getVideo_Duration()));
                viewHolder.textSecond.setText(minutes + " sec");
            } else {
                viewHolder.textSecond.setText(minutes + " min");
            }
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


        ImageView videoView;
        TextView textTitle, textSecond;


        public ViewHolder(View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSecond = itemView.findViewById(R.id.textSecond);

        }
    }
}