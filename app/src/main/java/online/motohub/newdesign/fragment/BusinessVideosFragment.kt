package online.motohub.newdesign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_business_videos.*
import online.motohub.R
import online.motohub.activity.ViewSpecLiveActivity
import online.motohub.fragment.BaseFragment
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.interfaces.OnLoadMoreListener
import online.motohub.model.GalleryVideoResModel
import online.motohub.newdesign.adapter.BusinessVideoAdapter
import online.motohub.newdesign.constants.AppConstants
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.BusinessVideosViewModel
import online.motohub.tags.AdapterTag


class BusinessVideosFragment : BaseFragment(), AdapterClickCallBack {


    private var model: BusinessVideosViewModel? = null
    private var videosAdapter: BusinessVideoAdapter? = null

    private var isLoadMoreCalled = false
    private var isLoading = false
    private var hasNextPage = false

    private var videosList = ArrayList<GalleryVideoResModel>()

    private val mOnLoadMoreListener = OnLoadMoreListener {
        isLoadMoreCalled = true
        model!!.getVideosList(videosList.size, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_business_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        var activeBundle = arguments
        if (activeBundle == null) {
            activeBundle = Bundle()
        }
        val layoutManager = GridLayoutManager(activity, 3)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(BusinessVideosViewModel::class.java)
        registerModel(model)
        model!!.videosLiveData.observe(this, Observer {
            if (it != null)
                videosList.addAll(it)

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

    private fun setAdapter() {
        errTxt.visibility = if (videosList.isEmpty()) View.VISIBLE else View.GONE
        if (videosAdapter == null) {
            videosAdapter = BusinessVideoAdapter(activity!!, videosList, this)
            listView.adapter = videosAdapter
        } else {
            videosAdapter!!.notifyDataSetChanged()
        }
        hasNextPage = model!!.totalCount > videosList.size
        isLoadMoreCalled = false
        isLoading = false
    }

    private var clickPos = 0
    override fun onClick(view: View?, tag: AdapterTag) {
        clickPos = tag.pos
        val pos = tag.pos
        val mAutoVideoIntent = Intent(activity, ViewSpecLiveActivity::class.java)
        mAutoVideoIntent.putParcelableArrayListExtra(AppConstants.VIDEOLIST, videosList)
        mAutoVideoIntent.putExtra(AppConstants.POSITION, pos)
        mAutoVideoIntent.putExtra(AppConstants.TAG, "BusinessVideosFragment")
        startActivity(mAutoVideoIntent)

    }

}