package online.motohub.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import online.motohub.R;
import online.motohub.model.GalleryVideoResModel;
import online.motohub.util.ScreenSize;
import online.motohub.util.UrlUtils;

public class GalleryVideoAdapter extends RecyclerView.Adapter<GalleryVideoAdapter.Holder> {

    private Context mContext;
    private List<GalleryVideoResModel> videoResModels;
    private SparseBooleanArray mSelectedItemsIds;

    public GalleryVideoAdapter(Context mContext, List<GalleryVideoResModel> videoResModels) {
        this.mContext = mContext;
        this.videoResModels = videoResModels;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    /*public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }*/

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = inflater.inflate(R.layout.row_picker_video_pager_thumbnail, parent, false);
        View v = inflater.inflate(R.layout.example, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final GalleryVideoResModel model = videoResModels.get(position);

        GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + model.getThumbnail(), new LazyHeaders.Builder()
                .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                .build());

        Glide.with(mContext)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .error(R.drawable.video_place_holder))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        holder.mPlayBtn.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.mImageView);

        /*holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(model, holder.getLayoutPosition());
                }
            }
        });*/
        if (model.getCaption() != null && !model.getCaption().equals("null")) {
            holder.titleTv.setVisibility(View.VISIBLE);
            holder.titleTv.setText("" + model.getCaption());
        } else {
            holder.titleTv.setVisibility(View.GONE);
        }

        holder.iv_check.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return videoResModels.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView mPlayBtn, iv_check;
        AppCompatImageView mImageView;
        TextView titleTv;

        Holder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.photoACImgV);
            mPlayBtn = v.findViewById(R.id.playBtn);
            iv_check = v.findViewById(R.id.iv_check);
            titleTv = v.findViewById(R.id.titleTv);
            ViewGroup.LayoutParams parem = mImageView.getLayoutParams();
            parem.height = ScreenSize.getWidth(50);
            mImageView.setLayoutParams(parem);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(GalleryVideoResModel model, int position);
    }

    public void filterList(ArrayList<GalleryVideoResModel> videoResModels) {
        this.videoResModels = videoResModels;
        notifyDataSetChanged();
    }

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}
