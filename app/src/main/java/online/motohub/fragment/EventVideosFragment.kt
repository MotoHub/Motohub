package online.motohub.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_event_videos.*
import online.motohub.R
import online.motohub.adapter.OnDemandEventsAdapter
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.model.OndemandNewResponse
import online.motohub.tags.AdapterTag
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.OnDemandEventsViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EventVideosFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, AdapterClickCallBack {

    private var model: OnDemandEventsViewModel? = null
    private var onDemandAdapter: OnDemandEventsAdapter? = null

    private var videoList = ArrayList<OndemandNewResponse?>()

    private var searchStr = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setupUI(parentLay)
        var activeBundle = arguments
        if (activeBundle == null) {
            activeBundle = Bundle()
        }
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager

        swipeRefreshLay.setOnRefreshListener(this)
        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(OnDemandEventsViewModel::class.java)
        registerModel(model)
        model!!.onDemandVideoLiveData.observe(this, Observer {
            if (it != null)
                videoList.addAll(it)

            setAdapter()

        })
        model!!.initialize()

        RxTextView.textChanges(searchEdt)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .filter {
                    SystemClock.sleep(1000) // Simulate the heavy stuff.
                    true
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : rx.Observer<CharSequence> {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        Log.d("Error", "Error.", e)
                    }

                    override fun onNext(charSequence: CharSequence) {
                        val mSearchStrTemp = charSequence.toString()
                        searchOnDemand(mSearchStrTemp)
                    }
                });
    }

    private fun searchOnDemand(searchStr: String) {
        try {
            showSnackBar(parentLay, "Under Development")
            //TODO LocalSearch
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View?, pos: AdapterTag?) {

    }

    override fun onRefresh() {
        videoList.clear()
        model!!.getOnDemandEvents()
    }

    private fun setAdapter() {
        if (onDemandAdapter == null) {
            onDemandAdapter = OnDemandEventsAdapter(activity, videoList, model!!.profileObj);
            listView.adapter = onDemandAdapter
        } else {
            onDemandAdapter!!.notifyDataSetChanged()
        }
        if (swipeRefreshLay.isRefreshing)
            swipeRefreshLay.isRefreshing = false
    }
}