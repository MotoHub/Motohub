package online.motohub.newdesign.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_find_event.*
import online.motohub.R
import online.motohub.activity.EventsFindActivity
import online.motohub.fragment.BaseFragment
import online.motohub.model.EventsResModel
import online.motohub.newdesign.activity.ComingSoonActivity
import online.motohub.newdesign.adapter.FindEventsAdapter
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.FindEventViewModel
import java.util.*

class FindEventFragment : BaseFragment(), androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

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
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager

        swipeRefreshLay.setOnRefreshListener(this)
        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(FindEventViewModel::class.java)
        registerModel(model)
        model!!.eventsLiveData.observe(this, Observer {
            if (it != null)
                setAdapter(it)
        })
        model!!.initialize()

        findEventsView.setOnClickListener {
            startActivity(Intent(activity, EventsFindActivity::class.java))
        }
        viewEventResultsView.setOnClickListener {
            startActivity(Intent(activity, ComingSoonActivity::class.java))
        }
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