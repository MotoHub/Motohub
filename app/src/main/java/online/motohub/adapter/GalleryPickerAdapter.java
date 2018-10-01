package online.motohub.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.interfaces.OnLoadMoreListener;
import online.motohub.model.LocalFolderModel;
import online.motohub.model.LocalImgModel;
import online.motohub.model.LocalVideoModel;

public class GalleryPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<LocalImgModel> mImgList;
    private List<LocalFolderModel> mImgFolderList;
    private List<LocalVideoModel> mVideoList;
    private OnItemClickListener mOnItemClickListener;
    private LayoutInflater mInflater;

    public static final int IMAGE_TYPE = 1, VIDEO_TYPE = 2, IMAGE_FOLDER_TYPE = 3, VIEW_PROGRESS = 4;

    private int galleryType;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;


    public GalleryPickerAdapter(Context context, int galleryType) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        this.galleryType = galleryType;
        if (this.galleryType == IMAGE_TYPE || this.galleryType == IMAGE_FOLDER_TYPE) {
            mImgList = new ArrayList<>();
            mImgFolderList = new ArrayList<>();

        } else if (this.galleryType == VIDEO_TYPE) {
            mVideoList = new ArrayList<>();
        }
    }

    public GalleryPickerAdapter(Context context, int galleryType, RecyclerView recyclerView) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        this.galleryType = galleryType;
        if (this.galleryType == IMAGE_TYPE || this.galleryType == IMAGE_FOLDER_TYPE) {
            mImgList = new ArrayList<>();
            mImgFolderList = new ArrayList<>();

        } else if (this.galleryType == VIDEO_TYPE) {
            mVideoList = new ArrayList<>();
        }
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final StaggeredGridLayoutManager linearLayoutManager = (StaggeredGridLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();

                            int[] lastPositions = new int[linearLayoutManager.getSpanCount()];
                            lastPositions = linearLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
                            lastVisibleItem = Math.max(lastPositions[0], lastPositions[1]);

                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached Do something
                                if (onLoadMoreListener != null) {
                                    loading = true;
                                    onLoadMoreListener.onLoadMore();
                                }

                            }
                        }
                    });
        }
    }

    public void setGalleryType(int galleryType) {
        this.galleryType = galleryType;
    }

    public void setImgList(List<LocalImgModel> mImgList) {
        this.mImgList = mImgList;
        notifyDataSetChanged();
    }

    public void setImgFolderList(List<LocalFolderModel> mImgFolderList) {
        this.mImgFolderList = mImgFolderList;
        notifyDataSetChanged();
    }

    public void setVideoList(List<LocalVideoModel> mVideoList) {
        this.mVideoList = mVideoList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {

        switch (this.galleryType) {
            case IMAGE_TYPE:
                return this.mImgList.size();
            case IMAGE_FOLDER_TYPE:
                return this.mImgFolderList.size();
            case VIDEO_TYPE:
                return this.mImgList.size();
            default:
                return 0;
        }

    }

    @Override
    public int getItemViewType(int position) {

        switch (this.galleryType) {
            case IMAGE_TYPE:
                return IMAGE_TYPE;
            case IMAGE_FOLDER_TYPE:
                return IMAGE_FOLDER_TYPE;
            case VIDEO_TYPE:
                return mImgList.get(position) != null ? VIDEO_TYPE : VIEW_PROGRESS;
            default:
                return 0;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case IMAGE_TYPE:
                view = mInflater.inflate(R.layout.row_picker_image_layout, parent, false);
                return new ImageHolder(view);
            case IMAGE_FOLDER_TYPE:
                view = mInflater.inflate(R.layout.row_picker_image_folder, parent, false);
                return new ImageFolderHolder(view);
            case VIDEO_TYPE:
                view = mInflater.inflate(R.layout.row_picker_video_layout, parent, false);
                return new VideoHolder(view);
            case VIEW_PROGRESS:
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progressbar_item, parent, false);
                return new ProgressViewHolder(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageHolder) {

            ImageHolder imageHolder = (ImageHolder) holder;
            fillImageHolder(imageHolder, position);

        } else if (holder instanceof VideoHolder) {

            VideoHolder videoHolder = (VideoHolder) holder;
            fillVideoHolder(videoHolder, position);

        } else if (holder instanceof ImageFolderHolder) {
            ImageFolderHolder imageFolderHolder = (ImageFolderHolder) holder;
            fillImageFolderHolder(imageFolderHolder, position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    private void fillImageFolderHolder(ImageFolderHolder holder, final int position) {

        LocalFolderModel model = mImgFolderList.get(position);

        holder.mFolderNameText.setText(model.getDisplayName());

        if (position < mImgList.size()) {
            Glide.with(mContext)
                    .load(mImgList.get(position).getFileUri().getPath())
                    .apply(new RequestOptions().error(R.drawable.img_place_holder))
                    .into(holder.mFolderImageView);
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(true, position);
                }
            }
        });
    }

    private void fillImageHolder(final ImageHolder imageHolder, final int position) {
        Glide.with(mContext)
                .load(mImgList.get(position).getFileUri().getPath())
                .apply(new RequestOptions()
                        .error(R.drawable.img_place_holder))
                .into(imageHolder.mImg);
        imageHolder.mImgLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(false, position);
                }
            }
        });
    }

    private void fillVideoHolder(VideoHolder videoHolder, final int position) {
        Glide.with(mContext)
                .load(mImgList.get(position).getFileUri().getPath())
                .apply(new RequestOptions()
                        .error(R.drawable.video_place_holder))
                .into(videoHolder.mThumbnail);

        videoHolder.mTitleTextView.setText(mImgList.get(position).getFileName());
        videoHolder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    if (!loading) {
                        mOnItemClickListener.onItemClick(false, position);
                    } else {
                        ((BaseActivity) mContext).showToast(mContext, "Please wait until loading the content!!!");
                    }

                }
            }
        });
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_picker_img_pager_thumbnail_image_view)
        ImageView mImg;
        @BindView(R.id.row_adapter_picker_image_layout)
        RelativeLayout mImgLay;

        ImageHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_adapter_picker_video_thumbnail_iv)
        ImageView mThumbnail;

        @BindView(R.id.row_adapter_picker_video_title_text)
        TextView mTitleTextView;

        @BindView(R.id.row_adapter_picker_video_container)
        CardView mContainer;

        @BindView(R.id.playBtn)
        ImageView mPlayBtn;

        VideoHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }


    class ImageFolderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_picker_img_folder_name_tv)
        TextView mFolderNameText;
        @BindView(R.id.row_picker_img_folder_backgroundImg)
        ImageView mFolderImageView;

        @BindView(R.id.row_picker_img_folder_container)
        RelativeLayout mContainer;

        ImageFolderHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(boolean isFolder, int position);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

}
