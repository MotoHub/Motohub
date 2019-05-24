package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import online.motohub.R;
import online.motohub.model.GalleryImgResModel;
import online.motohub.util.ScreenSize;
import online.motohub.util.UrlUtils;

public class GalleryImgAdapter extends RecyclerView.Adapter<GalleryImgAdapter.Holder> {

    private Context mContext;
    private List<GalleryImgResModel> models;
    //private OnItemClickListener mOnItemClickListener;
    private SparseBooleanArray mSelectedItemsIds;

    public GalleryImgAdapter(Context mContext, List<GalleryImgResModel> models) {
        this.mContext = mContext;
        this.models = models;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    /*public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }*/

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.row_gallery_image_layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        try {

            GalleryImgResModel model = models.get(position);

      /*  GlideUrl glideUrl = new GlideUrl(UrlUtils.FILE_URL + model.getGalleryImage(),
                new LazyHeaders.Builder()
                        .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                        .build());*/

            GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + model.getGalleryImage(),
                    new LazyHeaders.Builder()
                            .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                            .build());

            Glide.with(mContext)
                    .load(UrlUtils.AWS_S3_BASE_URL + model.getGalleryImage())
                    .apply(new RequestOptions()
                            .dontAnimate().error(R.drawable.img_place_holder))
                    .into(holder.mImgView);

            holder.iv_check.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);

        /*holder.mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.getLayoutPosition());
                }
            }
        });*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    /*public interface OnItemClickListener {
        void onItemClick(int position);
    }*/

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

    class Holder extends RecyclerView.ViewHolder {

        ImageView mImgView, iv_check;

        Holder(View v) {
            super(v);
            mImgView = v.findViewById(R.id.row_gallery_image_view);
            iv_check = v.findViewById(R.id.iv_check);
            ViewGroup.LayoutParams params = mImgView.getLayoutParams();
            params.width = ScreenSize.getWidth(50);
            params.height = ScreenSize.getWidth(50);
            mImgView.setLayoutParams(params);
        }
    }


}
