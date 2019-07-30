package online.motohub.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_news_feed.*
import online.motohub.R
import online.motohub.adapter.PostsAdapter
import online.motohub.model.PostsResModel
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.NewsFeedViewModel
import java.util.*

class NewsFeedFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, PostsAdapter.TotalRetrofitPostsResultCount {


    var model: NewsFeedViewModel? = null
    var feedAdapter: PostsAdapter? = null

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
                setAdapter(it)
        })
        model!!.initialize()

    }

    override fun onRefresh() {
        swipeRefreshLay.isRefreshing = false
    }


    private fun setAdapter(list: ArrayList<PostsResModel>) {
        if (feedAdapter == null) {
            feedAdapter = PostsAdapter(list, model!!.profileObj, activity, false)
            listView.adapter = feedAdapter
        } else {
            feedAdapter!!.notifyDataSetChanged()
        }
    }

    override fun getTotalPostsResultCount(): Int {
        return 10
    }
}