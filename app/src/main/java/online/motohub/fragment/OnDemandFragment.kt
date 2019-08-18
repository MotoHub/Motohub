package online.motohub.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_on_demand.*
import online.motohub.R
import online.motohub.adapter.OnDemandVideoAdapter
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.interfaces.OnLoadMoreListener
import online.motohub.model.PromoterVideoModel
import online.motohub.tags.AdapterTag
import online.motohub.viewmodel.BaseViewModelFactory
import online.motohub.viewmodel.OnDemandVideosViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class OnDemandFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, AdapterClickCallBack {


    private var model: OnDemandVideosViewModel? = null
    private var onDemandAdapter: OnDemandVideoAdapter? = null

    private var isLoadMoreCalled = false
    private var isLoading = false
    private var hasNextPage = false

    private var videoList = ArrayList<PromoterVideoModel.PromoterVideoResModel?>()

    private var searchStr = ""

    private val mOnLoadMoreListener = OnLoadMoreListener {
        isLoadMoreCalled = true
        model!!.getOnDemandVideos(searchStr, videoList.size, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_on_demand, container, false)
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
        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(OnDemandVideosViewModel::class.java)
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

    private fun searchOnDemand(searchStr: String) {
        try {
            videoList.clear()
            setAdapter()
            var text: String? = null
            try {
                text = URLEncoder.encode(searchStr, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            model!!.getOnDemandVideos(text!!, videoList.size, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View?, pos: AdapterTag?) {

    }

    override fun onRefresh() {
        videoList.clear()
        model!!.getOnDemandVideos(searchStr, videoList.size, false)
    }

    private fun setAdapter() {
        if (onDemandAdapter == null) {
            onDemandAdapter = OnDemandVideoAdapter(videoList, model!!.profileObj, activity, this@OnDemandFragment);
            listView.adapter = onDemandAdapter
        } else {
            onDemandAdapter!!.notifyDataSetChanged()
        }
        hasNextPage = model!!.totalCount > videoList.size
        isLoadMoreCalled = false
        isLoading = false
        if (swipeRefreshLay.isRefreshing)
            swipeRefreshLay.isRefreshing = false
    }
}