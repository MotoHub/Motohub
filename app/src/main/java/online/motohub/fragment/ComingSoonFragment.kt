package online.motohub.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.motohub.R

class ComingSoonFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_coming_soon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        Log.e("CURRENT_FRAGMENT","MyProfileFragment")
        getMyProfile()
        getFeeds()
    }

    private fun getMyProfile() {

    }

    private fun getFeeds() {

    }

    private fun setAdapter() {

    }
}