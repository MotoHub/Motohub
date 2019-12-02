package online.motohub.newdesign.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_business_photos.*
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.fragment.BaseFragment
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.interfaces.OnLoadMoreListener
import online.motohub.model.GalleryImgResModel
import online.motohub.newdesign.adapter.BusinessPhotosAdapter
import online.motohub.newdesign.viewmodel.BaseViewModelFactory
import online.motohub.newdesign.viewmodel.BusinessPhotosViewModel
import online.motohub.tags.AdapterTag
import online.motohub.util.UrlUtils


class BusinessPhotosFragment : BaseFragment(), AdapterClickCallBack {


    private var model: BusinessPhotosViewModel? = null
    private var photosAdapter: BusinessPhotosAdapter? = null

    private var isLoadMoreCalled = false
    private var isLoading = false
    private var hasNextPage = false

    private var photosList = ArrayList<GalleryImgResModel>()

    private val mOnLoadMoreListener = OnLoadMoreListener {
        isLoadMoreCalled = true
        model!!.getPhotosList(photosList.size, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_business_photos, container, false)
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
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 3)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager

        model = ViewModelProviders.of(this, BaseViewModelFactory(activity!!.application, activeBundle)).get(BusinessPhotosViewModel::class.java)
        registerModel(model)
        model!!.photosLiveData.observe(this, Observer {
            if (it != null)
                photosList.addAll(it)

            setAdapter()

        })
        model!!.initialize()

        listView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
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
        errTxt.visibility = if (photosList.isEmpty()) View.VISIBLE else View.GONE
        if (photosAdapter == null) {
            photosAdapter = BusinessPhotosAdapter(activity!!, photosList, this)
            listView.adapter = photosAdapter
        } else {
            photosAdapter!!.notifyDataSetChanged()
        }
        hasNextPage = model!!.totalCount > photosList.size
        isLoadMoreCalled = false
        isLoading = false
    }

    private var clickPos = 0
    override fun onClick(view: View?, tag: AdapterTag) {
        clickPos = tag.pos
        val pos = tag.pos
        (activity as BaseActivity).moveLoadImageScreen(activity, UrlUtils.AWS_S3_BASE_URL + photosList[pos].galleryImage)

    }
}
