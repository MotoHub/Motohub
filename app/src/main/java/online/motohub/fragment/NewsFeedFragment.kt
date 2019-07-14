package online.motohub.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_news_feed.*
import online.motohub.R
import online.motohub.viewmodel.BaseViewModel
import online.motohub.viewmodel.NewsFeedViewModel

class NewsFeedFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {


    var model: NewsFeedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        swipeRefreshLay.setOnRefreshListener(this)
        model = ViewModelProviders.of(this, BaseViewModel.BaseViewModelFactory(activity!!.application)).get(NewsFeedViewModel::class.java)

        getFeeds()
    }

    override fun onRefresh() {
        swipeRefreshLay.isRefreshing=false
    }

    private fun getFeeds() {

    }

    private fun setAdapter() {

    }
}