package online.motohub.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_news_feed.*
import kotlinx.android.synthetic.main.view_header_news_feed.*
import online.motohub.R
import online.motohub.activity.*
import online.motohub.activity.club.ClubProfileActivity
import online.motohub.activity.news_and_media.NewsAndMediaProfileActivity
import online.motohub.activity.performance_shop.PerformanceShopProfileActivity
import online.motohub.activity.promoter.PromoterProfileActivity
import online.motohub.activity.promoter.PromotersImgListActivity
import online.motohub.activity.track.TrackProfileActivity
import online.motohub.adapter.NewsFeedAdapter
import online.motohub.constants.AppConstants
import online.motohub.constants.BundleConstants
import online.motohub.fragment.dialog.AppDialogFragment
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.interfaces.OnLoadMoreListener
import online.motohub.model.*
import online.motohub.model.promoter_club_news_media.PromotersModel
import online.motohub.retrofit.RetrofitClient
import online.motohub.tags.AdapterTag
import online.motohub.util.UrlUtils
import online.motohub.util.Utility
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.NewsFeedViewModel
import org.greenrobot.eventbus.EventBus
import java.net.URLDecoder
import java.util.*
import kotlin.collections.ArrayList

class NewsFeedFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, AdapterClickCallBack, View.OnClickListener {


    private var model: NewsFeedViewModel? = null
    private var feedAdapter: NewsFeedAdapter? = null

    private var isLoadMoreCalled = false
    private var isLoading = false
    private var hasNextPage = false

    private var feedsList = ArrayList<PostsResModel?>()

    private val mOnLoadMoreListener = OnLoadMoreListener {
        isLoadMoreCalled = true
        model!!.getFeeds(feedsList.size, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        performanceShopLay.setOnClickListener(this)
        newsAndMediaLay.setOnClickListener(this)
        tracksLay.setOnClickListener(this)
        clubsLay.setOnClickListener(this)
        promotersLay.setOnClickListener(this)
        writePostBtn.setOnClickListener(this)

        var activeBundle = arguments
        if (activeBundle == null) {
            activeBundle = Bundle()
        }
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager

        swipeRefreshLay.setOnRefreshListener(this)
        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(NewsFeedViewModel::class.java)
        registerModel(model)
        model!!.newsFeedLiveData.observe(this, Observer {
            if (it != null)
                feedsList.addAll(it)

            setAdapter()

        })
        model!!.initialize()

        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val mVisibleItemCount = layoutManager.childCount
                val mTotalItemCount = layoutManager.itemCount
                val mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && hasNextPage) {
                    if (mVisibleItemCount + mFirstVisibleItemPosition >= mTotalItemCount && mFirstVisibleItemPosition >= 0) {
                        isLoading = true
                        mOnLoadMoreListener.onLoadMore()

                    }
                }
            }
        })


    }

    override fun onRefresh() {
        feedsList.clear()
        model!!.getFeeds(feedsList.size, false)
    }

    private fun setAdapter() {
        if (feedAdapter == null) {
            feedAdapter = NewsFeedAdapter(activity, feedsList, model!!.profileObj, this, this)
            listView.adapter = feedAdapter
        } else {
            feedAdapter!!.notifyDataSetChanged()
        }
        hasNextPage = model!!.totalCount > feedsList.size
        isLoadMoreCalled = false
        isLoading = false
        if (swipeRefreshLay.isRefreshing)
            swipeRefreshLay.isRefreshing = false
    }

    override fun onClick(v: View?) {
        //TODO If you are adding any additional view here please mention tag in xml
        val tag = v!!.tag as String
        val bundle = Bundle()
        bundle.putString(BundleConstants.MY_PROFILE_OBJ, Gson().toJson(model!!.profileObj))
        bundle.putString(BundleConstants.BUSINESS_PROFILE_TYPE, tag)
        when (view?.id) {
            R.id.performanceShopLay, R.id.newsAndMediaLay, R.id.tracksLay, R.id.clubsLay, R.id.promoterLay -> {
                startActivity(Intent(activity, BusinessProfileListActivity::class.java).putExtras(bundle))
            }
            R.id.writePostBtn -> {
                EventBus.getDefault().postSticky(model!!.profileObj)
                startActivityForResult(Intent(activity, WritePostActivity::class.java)
                        .putExtra(AppConstants.IS_NEWSFEED_POST, false)
                        .putExtra(AppConstants.USER_TYPE, "user"), AppConstants.WRITE_POST_REQUEST)
            }
        }
    }

    private var clickPos = 0
    override fun onClick(view: View?, tag: AdapterTag) {
        if (swipeRefreshLay.isRefreshing) {
            return
        }
        clickPos = tag.pos
        val pos = tag.pos
        val isBlocked = tag.isBlocked
        val isLiked = tag.isLiked
        when (view?.id) {
            R.id.sharedUserImg -> {
                moveProfileScreen(pos, true)
            }
            R.id.sharedProfileNameTxt, R.id.sharedPostTimeTxt, R.id.userImg, R.id.profileNameTxt, R.id.postTimeTxt -> {
                moveProfileScreen(pos)
            }
            R.id.sharedNotifyIcon, R.id.notifyIcon -> {
                if (isBlocked) {
                    unBlockNotification(pos)
                } else {
                    blockNotification(pos)
                }
            }
            R.id.sharedDownIcon, R.id.downIcon -> {
                showFeedMenuPopup(pos)
            }
            R.id.imgVideoView -> {
                moveImgVideoScreen(pos)
            }
            R.id.commentView, R.id.commentsCountTxt, R.id.commentBtn -> {
                moveCommentsListScreen(pos)
            }
//            R.id.likeCountTxt -> {
//            }
//            R.id.viewCountTxt -> {
//            }
//            R.id.shareCountTxt -> {
//            }
            R.id.likeBtn -> {
                if (isLiked) {
                    unLikePost(pos)
                } else {
                    likePost(pos)
                }
            }
            R.id.shareBtn -> {
                sharePost(pos)
            }
        }
    }

    private fun moveCommentsListScreen(pos: Int) {
        (activity as BaseActivity).movePostCommentScreen(activity, feedsList[pos]!!.id!!, model!!.profileObj!!)
    }

    private fun moveImgVideoScreen(pos: Int) {
        var finalArr: Array<String>? = null
        val mImgArray = Utility.getInstance().getImgVideoList(feedsList[pos]!!.postPicture)
        val mVideoArray = Utility.getInstance().getImgVideoList(feedsList[pos]!!.postVideoThumbnailURL)
        if (mImgArray != null && mVideoArray != null) {
            finalArr = Utility.getInstance().mergeArrayList(mVideoArray, mImgArray)
        } else if (mVideoArray != null && mVideoArray.size == 1) {
            //TODO UpdateView Count API
            updateViewCount(pos)
            (activity as BaseActivity).LoadVideoScreen(activity, UrlUtils.AWS_S3_BASE_URL + mVideoArray[0],
                    pos, RetrofitClient.UPDATE_FEED_COUNT)
            return
        } else if (mVideoArray != null && mVideoArray.size > 1) {
            finalArr = Arrays.copyOf(mVideoArray, mVideoArray.size)
        } else if (mImgArray != null && mImgArray.size == 1) {
            (activity as BaseActivity).moveLoadImageScreen(activity, UrlUtils.AWS_S3_BASE_URL + mImgArray[0])
            return
        } else if (mImgArray != null && mImgArray.size > 1) {
            finalArr = Arrays.copyOf(mImgArray, mImgArray.size)
        }
        val intent = Intent(activity, PromotersImgListActivity::class.java)
        intent.putExtra("img", finalArr)
        startActivity(intent)
    }

    private fun updateViewCount(pos: Int) {
        val filter = "ID = " + feedsList[pos]!!.id!!
        RetrofitClient.getRetrofitInstance().getViewCount(activity as BaseActivity, filter, RetrofitClient.FEED_VIDEO_COUNT)
    }

    private fun showFeedMenuPopup(pos: Int) {
        val mPos = ArrayList<String>()
        mPos.add(pos.toString())
        if (feedsList[pos]!!.profileID == model!!.profileObj!!.id) {
            (activity as BaseActivity).showAppDialog(AppDialogFragment.BOTTOM_POST_ACTION_DIALOG, mPos)
        } else {
            (activity as BaseActivity).showAppDialog(AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG, mPos)
        }
    }

    override fun alertDialogPositiveBtnClick(dialogType: String?, position: Int) {
        super.alertDialogPositiveBtnClick(dialogType, position)

        when (dialogType) {
            AppDialogFragment.BOTTOM_DELETE_DIALOG -> {
                RetrofitClient.getRetrofitInstance().callDeleteProfilePosts(this, feedsList[position]!!.id, RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE)
            }
            AppDialogFragment.BOTTOM_REPORT_ACTION_DIALOG -> {
                startActivity(
                        Intent(activity, ReportActivity::class.java)
                                .putExtra(PostsModel.POST_ID, feedsList[position]!!.id)
                                .putExtra(ProfileModel.PROFILE_ID, model!!.profileObj!!.id)
                                .putExtra(ProfileModel.USER_ID, model!!.profileObj!!.userID))
            }
        }
    }

    private fun moveProfileScreen(pos: Int) {
        if (feedsList[pos]!!.newSharedPostID == null) {
            if (feedsList[pos]!!.profileID == model!!.profileObj!!.id) {
                if (activity !is MyMotoFileActivity) {
                    (activity as BaseActivity).moveMyProfileScreenWithResult(activity, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT)
                }
                return
            }
        } else {
            if (feedsList[pos]!!.whoPostedProfileID == model!!.profileObj!!.id) {
                if (activity !is MyMotoFileActivity) {
                    (activity as BaseActivity).moveMyProfileScreenWithResult(activity, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT)
                }
                return
            }
        }
        val mPostUserType = feedsList[pos]!!.userType.trim { it <= ' ' }
        if (mPostUserType == PromotersModel.PROMOTER || mPostUserType == PromotersModel.NEWS_AND_MEDIA ||
                mPostUserType == PromotersModel.TRACK || mPostUserType == PromotersModel.CLUB
                || mPostUserType == PromotersModel.SHOP || mPostUserType == AppConstants.SHARED_POST || mPostUserType == AppConstants.VIDEO_SHARED_POST) {
            val mClassName: Class<*>
            var mUserType = ""
            if (feedsList[pos]!!.promoterByWhoPostedProfileID != null) {
                EventBus.getDefault().postSticky(feedsList[pos]!!.promoterByWhoPostedProfileID)
                mUserType = feedsList[pos]!!
                        .promoterByWhoPostedProfileID.userType
            } else {
                //mBundle.putSerializable(PromotersModel.PROMOTERS_RES_MODEL, feedsList[pos]!!.getPromoterByProfileID());
                //MotoHub.getApplicationInstance().setmPromoterResModel(feedsList[pos]!!.getPromoterByProfileID());
                if (feedsList[pos]!!.promoterByProfileID != null) {
                    EventBus.getDefault().postSticky(feedsList[pos]!!.promoterByProfileID)
                    mUserType = feedsList[pos]!!
                            .promoterByProfileID.userType
                }
            }

            mClassName = when (mUserType) {
                PromotersModel.NEWS_AND_MEDIA -> NewsAndMediaProfileActivity::class.java
                PromotersModel.CLUB -> ClubProfileActivity::class.java
                PromotersModel.PROMOTER -> PromoterProfileActivity::class.java
                PromotersModel.TRACK -> TrackProfileActivity::class.java
                PromotersModel.SHOP -> PerformanceShopProfileActivity::class.java
                else -> PromoterProfileActivity::class.java
            }
            EventBus.getDefault().postSticky(model!!.profileObj!!)
            (activity as BaseActivity).startActivityForResult(Intent(activity, mClassName), AppConstants.FOLLOWERS_FOLLOWING_RESULT)

        } else {
            (activity as BaseActivity).moveOtherProfileScreen(activity, model!!.profileObj!!.id,
                    feedsList[pos]!!.profilesByWhoPostedProfileID.id)
        }
    }

    override fun moveProfileScreen(pos: Int, isSharedProfile: Boolean) {
        super.moveProfileScreen(pos, isSharedProfile)
        if (isSharedProfile) {
            if (Integer.parseInt(feedsList[pos]!!.whoSharedProfileID) == model!!.profileObj!!.id) {
                if (activity !is MyMotoFileActivity) {
                    (activity as BaseActivity).moveMyProfileScreenWithResult(activity, 0, AppConstants.FOLLOWERS_FOLLOWING_RESULT)
                }
                return
            }
            val sharedProfileID: Int = if (feedsList[pos]!!.userType.trim { it <= ' ' } == AppConstants.VIDEO_SHARED_POST ||
                    feedsList[pos]!!.userType.trim { it <= ' ' } == AppConstants.USER_VIDEO_SHARED_POST) {
                feedsList[pos]!!.videoSharesByNewSharedPostID.profiles_by_ProfileID.id
            } else {
                feedsList[pos]!!.newSharedPost.profiles_by_ProfileID.id
            }
            (activity as BaseActivity).moveOtherProfileScreen(activity, model!!.profileObj!!.id,
                    sharedProfileID)
        } else {
            moveProfileScreen(pos)
        }
    }

    private fun blockNotification(pos: Int) {
        val postID = feedsList[pos]!!.id!!
        val userID = model!!.profileObj!!.userID
        val profileID = model!!.profileObj!!.id

        val mJsonArray = JsonArray()
        val jsonObject = JsonObject()
        jsonObject.addProperty(NotificationBlockedUsersModel.PostID, postID)
        jsonObject.addProperty(NotificationBlockedUsersModel.UserID, userID)
        jsonObject.addProperty(NotificationBlockedUsersModel.ProfileID, profileID)
        mJsonArray.add(jsonObject)

        val mJsonObj = JsonObject()
        mJsonObj.add("resource", mJsonArray)

        RetrofitClient.getRetrofitInstance().blockNotifications(this, mJsonObj, RetrofitClient.BLOCK_NOTIFY)
    }

    private fun unBlockNotification(pos: Int) {
        val postID = feedsList[pos]!!.id!!
        val profileID = model!!.profileObj!!.id
        val filter = "((ProfileID=$profileID)AND(PostID=$postID))"

        RetrofitClient.getRetrofitInstance().unBlockNotifications(this, filter, RetrofitClient.UNBLOCK_NOTIFY)
    }

    private fun unLikePost(pos: Int) {
        val postID = feedsList[pos]!!.id!!
        val profileID = model!!.profileObj!!.id
        val filter = "((ProfileID=$profileID)AND(PostID=$postID))"
        RetrofitClient.getRetrofitInstance().callUnLikeForPosts(this, filter, RetrofitClient.POST_UNLIKE)
    }

    private fun likePost(pos: Int) {
        val mJsonObject = JsonObject()
        val mItem = JsonObject()

        mItem.addProperty("PostID", feedsList[pos]!!.id!!)
        mItem.addProperty("ProfileID", model!!.profileObj!!.id)

        val mJsonArray = JsonArray()
        mJsonArray.add(mItem)
        mJsonObject.add("resource", mJsonArray)

        RetrofitClient.getRetrofitInstance().postLikesForPosts(this, mJsonObject, RetrofitClient.POST_LIKES)
    }

    private fun sharePost(pos: Int) {
        var content: String? = null
        try {
            content = URLDecoder.decode(feedsList[pos]!!.postText, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val isImgFile: Boolean
        val mImgList = Utility.getInstance().getImgVideoList(feedsList[pos]!!.postPicture)
        isImgFile = if (mImgList == null) {
            false
        } else
            !(mImgList.size == 1 && mImgList[0].trim { it <= ' ' } == "")
        val isVideoFile: Boolean
        val mVideoList = Utility.getInstance().getImgVideoList(feedsList[pos]!!.postVideoURL)
        isVideoFile = if (mVideoList == null) {
            false
        } else
            !(mVideoList.size == 1 && mVideoList[0].trim { it <= ' ' } == "")
        val isOtherMotoProfile = feedsList[pos]!!.profileID != model!!.profileObj!!.id
        if (isImgFile) {
            val mBitmapList = (activity as BaseActivity).getBitmapImageGlide(mImgList)
            if (mBitmapList != null) {
                (activity as BaseActivity).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, mBitmapList, null, pos, isOtherMotoProfile)
            }
        } else if (isVideoFile) {
            val mVideosList = Utility.getInstance().getImgVideoList(feedsList[pos]!!.postVideoURL)
            (activity as BaseActivity).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, mVideosList, pos, isOtherMotoProfile)

        } else {
            (activity as BaseActivity).showFBShareDialog(AppDialogFragment.BOTTOM_SHARE_DIALOG, content, null, null, pos, isOtherMotoProfile)
        }
    }

    override fun retrofitOnResponse(responseObj: Any?, responseType: Int) {
        super.retrofitOnResponse(responseObj, responseType)
        if (responseObj is FeedLikesModel) {
            val feedLikesList = responseObj.resource
            when (responseType) {
                RetrofitClient.POST_LIKES -> if (feedLikesList.size > 0)
                    feedAdapter!!.resetLikeAdapter(clickPos, feedLikesList[0])
                RetrofitClient.POST_UNLIKE -> feedAdapter!!.resetDisLike(clickPos)
            }
        } else if (responseObj is NotificationBlockedUsersModel) {
            val notifyBlockedList = responseObj.resource
            when (responseType) {
                RetrofitClient.BLOCK_NOTIFY -> if (notifyBlockedList.size > 0)
                    feedAdapter!!.resetBlock(clickPos, notifyBlockedList[0])
                RetrofitClient.UNBLOCK_NOTIFY -> feedAdapter!!.resetUnBlock(clickPos)
            }
        } else if (responseObj is PostsModel) {
            if (responseObj.resource != null && responseObj.resource.size > 0) {
                when (responseObj) {
                    RetrofitClient.DELETE_PROFILE_POSTS_RESPONSE -> {
                        feedsList.removeAt(clickPos)
                        setAdapter()
                        showMessage(getString(R.string.post_delete))
                    }
                }
            }
        }
    }
}