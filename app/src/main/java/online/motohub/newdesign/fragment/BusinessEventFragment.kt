package online.motohub.newdesign.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_business_news_feed.listView
import kotlinx.android.synthetic.main.fragment_events.*
import online.motohub.R
import online.motohub.fragment.BaseFragment
import online.motohub.model.EventsResModel
import online.motohub.newdesign.adapter.FindEventsAdapter
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.BusinessEventViewModel
import java.util.*

class BusinessEventFragment : BaseFragment() {

    var model: BusinessEventViewModel? = null
    var eventAdapter: FindEventsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
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

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(BusinessEventViewModel::class.java)
        registerModel(model)
        model!!.eventsLiveData.observe(this, androidx.lifecycle.Observer {
            if (it != null)

                setAdapter(it)
        })
        model!!.initialize()
    }

    private fun setAdapter(list: ArrayList<EventsResModel>) {
        errTxt.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        if (eventAdapter == null) {
            eventAdapter = FindEventsAdapter(activity!!, list, model!!.profileObj, true)
            listView.adapter = eventAdapter
        } else {
            eventAdapter!!.notifyDataSetChanged()
        }
    }

}