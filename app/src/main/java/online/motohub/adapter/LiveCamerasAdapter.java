package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import online.motohub.R;
import online.motohub.model.LiveStreamEntity;


public class LiveCamerasAdapter extends RecyclerView.Adapter<LiveCamerasAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private CameraListener mListener;
    private ArrayList<LiveStreamEntity> mLiveStreamList;

    public LiveCamerasAdapter(Context context, CameraListener listener, ArrayList<LiveStreamEntity> liveStreamList) {
        this.mInflater = LayoutInflater.from(context);
        mListener = listener;
        this.mLiveStreamList = liveStreamList;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_camera_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

//        String mCamera = "Camera "+String.valueOf(position+1);
        String mCamera = mLiveStreamList.get(position).getStreamName();

        holder.mCameraView.setText(mCamera);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cameraClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mLiveStreamList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView mCameraView;
        ViewHolder(View itemView) {
            super(itemView);
            mCameraView =  itemView.findViewById(R.id.CameraImageViewText);

        }

    }

    public interface CameraListener {

        void cameraClicked(int adapterPosition);
    }

}
