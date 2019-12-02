package online.motohub.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.activity.CommentReplyActivity;
import online.motohub.activity.PostCommentsActivity;
import online.motohub.activity.VideoCommentReplyActivity;
import online.motohub.activity.VideoCommentsActivity;
import online.motohub.model.ProfileResModel;
import online.motohub.util.Utility;

public class CommentTagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_USER = 1;
    private ArrayList<ProfileResModel> mUnFollowedProfileListData;
    private Context mContext;

    public CommentTagAdapter(ArrayList<ProfileResModel> unFollowedProfileListData, Context ctx) {
        this.mUnFollowedProfileListData = unFollowedProfileListData;
        this.mContext = ctx;
    }

    @Override
    public int getItemCount() {
        return mUnFollowedProfileListData.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position >= mUnFollowedProfileListData.size()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_USER;
        }

    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_USER) ? position : -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView;

        switch (viewType) {

            case VIEW_TYPE_USER:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_feed_likes, parent, false);
                return new ViewHolderUser(mView);

            case VIEW_TYPE_LOADING:

                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_small_progress_bar, parent, false);
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mView.getLayoutParams();
                mLayoutParams.bottomMargin = 30;
                mLayoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT;
                mLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                mView.setLayoutParams(mLayoutParams);
                return new ViewHolderLoader(mView);

            default:
                return null;

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_USER:
                try {

                    final ViewHolderUser mViewHolderUserProfile = (ViewHolderUser) holder;
                    mViewHolderUserProfile.mProfileImg.setTag(position);
                    mViewHolderUserProfile.mUsername.setTag(position);

                    ((BaseActivity) mContext).setImageWithGlide(mViewHolderUserProfile.mProfileImg, mUnFollowedProfileListData.get(position).getProfilePicture(), R.drawable.default_profile_icon);
                    mViewHolderUserProfile.mUsername.setText(Utility.getInstance().getUserName(mUnFollowedProfileListData.get(position)));

                    mViewHolderUserProfile.mCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mContext instanceof PostCommentsActivity)
                                ((PostCommentsActivity) mContext).addTaggedFriends(mUnFollowedProfileListData.get(position));
                            else if (mContext instanceof VideoCommentsActivity)
                                ((VideoCommentsActivity) mContext).addTaggedFriends(mUnFollowedProfileListData.get(position));
                            else if (mContext instanceof CommentReplyActivity)
                                ((CommentReplyActivity) mContext).addTaggedFriends(mUnFollowedProfileListData.get(position));
                            else if (mContext instanceof VideoCommentReplyActivity)
                                ((VideoCommentReplyActivity) mContext).addTaggedFriends(mUnFollowedProfileListData.get(position));
                        }
                    });
                    // mViewHolderUserProfile.mUsername.setOnClickListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_LOADING:
                final ViewHolderLoader mViewHolderLoader = (ViewHolderLoader) holder;
                if (mUnFollowedProfileListData.size() != ((TotalRetrofitResultCount) mContext).getTotalUnFollowedResultCount()) {
                    mViewHolderLoader.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mViewHolderLoader.mProgressBar.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    public interface TotalRetrofitResultCount {
        int getTotalUnFollowedResultCount();
    }

    private class ViewHolderUser extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImg;
        private TextView mUsername;
        private CardView mCardView;

        ViewHolderUser(View view) {
            super(view);
            mProfileImg = view.findViewById(R.id.comment_user_img);
            mUsername = view.findViewById(R.id.comment_user_name_txt);
            mCardView = view.findViewById(R.id.cardView);
        }

    }

    private class ViewHolderLoader extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;

        ViewHolderLoader(View view) {
            super(view);
            mProgressBar = view.findViewById(R.id.smallProgressBar);
        }

    }

}
