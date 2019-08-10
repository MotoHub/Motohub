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
import online.motohub.adapter.FindEventsAdapter
import online.motohub.model.EventsResModel
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.FindEventViewModel
import java.util.*

class FindEventFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    var model: FindEventViewModel? = null
    var eventAdapter: FindEventsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_find_event, container, false)
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
        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(FindEventViewModel::class.java)
        registerModel(model)
        model!!.eventsLiveData.observe(this, Observer {
            if (it != null)
                setAdapter(it)
        })
        model!!.initialize()
    }

    override fun onRefresh() {
        swipeRefreshLay.isRefreshing = false
    }


    private fun setAdapter(list: ArrayList<EventsResModel>) {
        if (eventAdapter == null) {
            eventAdapter = FindEventsAdapter(activity, list, model!!.profileObj, true)
            listView.adapter = eventAdapter
        } else {
            eventAdapter!!.notifyDataSetChanged()
        }
    }
}