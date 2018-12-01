package online.motohub.adapter.promoter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import online.motohub.R;
import online.motohub.activity.LoadVideoFromServerActivity;
import online.motohub.activity.promoter.PromoterGalleryViewPager;
import online.motohub.util.AppConstants;
import online.motohub.util.UrlUtils;

public class PromoterImgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String[] img_array;

    public PromoterImgListAdapter(String[] img_array, Context ctx) {
        this.img_array = img_array;
        this.mContext = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_list, parent, false);
        return new PromoterImgListAdapter.ImageListViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        PromoterImgListAdapter.ImageListViewHolder mViewHolderPromoters = (PromoterImgListAdapter.ImageListViewHolder) holder;
        try {

            /*GlideUrl glideUrl = new GlideUrl(UrlUtils.AWS_FILE_URL + img_array[position], new LazyHeaders.Builder()
                    .addHeader("X-DreamFactory-Api-Key", mContext.getString(R.string.dream_factory_api_key))
                    .build());*/

            Glide.with(mContext)
                    .load(UrlUtils.AWS_S3_BASE_URL + img_array[position])
                    .apply(new RequestOptions()
                            .dontAnimate()
                            .error(R.drawable.default_cover_img)
                            .dontAnimate())
                    .into(mViewHolderPromoters.promoter_img);

            if (img_array[position].contains("Videos")) {
                mViewHolderPromoters.playicon.setVisibility(View.VISIBLE);
            } else {
                mViewHolderPromoters.playicon.setVisibility(View.GONE);
            }


            mViewHolderPromoters.promoter_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!img_array[position].contains("Videos")) {

                        //String[] str_array = {"item1","item2","item3"};
                        List<String> list = new ArrayList<String>(Arrays.asList(img_array));
                        for (int i = list.size() - 1; i >= 0; i--) {
                            if (list.get(i).contains("Videos"))
                                list.remove(i);
                        }

                        String[] str_array = list.toArray(new String[0]);

                        Intent intent = new Intent(mContext, PromoterGalleryViewPager.class);
                        intent.putExtra("img", str_array);
                        intent.putExtra("pos", position);
                        mContext.startActivity(intent);
                    } else {
                        String url = img_array[position].substring(0, img_array[position].length() - 10);
                        url = url.replace("PostVideosThumb", "PostVideos");
                        Intent mLoadVideoFromServerActivity = new Intent(mContext, LoadVideoFromServerActivity.class);
                        mLoadVideoFromServerActivity.putExtra(AppConstants.VIDEO_PATH, UrlUtils.AWS_S3_BASE_URL + url);
                        mContext.startActivity(mLoadVideoFromServerActivity);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return img_array.length;
    }

    private static class ImageListViewHolder extends RecyclerView.ViewHolder {

        private ImageView promoter_img, playicon;

        ImageListViewHolder(View view) {
            super(view);
            promoter_img = view.findViewById(R.id.img);
            playicon = view.findViewById(R.id.playicon);
        }

    }

}
