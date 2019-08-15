package online.motohub.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_news_feed.*
import online.motohub.R
import online.motohub.adapter.NewsFeedAdapter
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.interfaces.OnLoadMoreListener
import online.motohub.model.PostsResModel
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.NewsFeedViewModel
import java.util.*

class NewsFeedFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, AdapterClickCallBack {



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
            feedAdapter = NewsFeedAdapter(activity, feedsList, model!!.profileObj, false,this)
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

    override fun onClick(view: View?, pos: Int) {
        when(view?.id){
            R.id.onoff_notify ->{

            }
        }
    }

}