package online.motohub.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import online.motohub.R
import online.motohub.activity.BaseActivity
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.model.promoter_club_news_media.PromotersResModel
import online.motohub.tags.AdapterTag
import java.util.*

class BusinessProfileListAdapter(private val context: Context, private val businessProfileList: ArrayList<PromotersResModel>,
                                 private val callback: AdapterClickCallBack) : RecyclerView.Adapter<BusinessProfileListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.row_list_view_item, parent, false)
        return Holder(mView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(businessProfileList[position])
    }

    override fun getItemCount(): Int {
        return businessProfileList.size
    }

    inner class Holder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val profileImg: CircleImageView = view.findViewById(R.id.circular_img_view)
        private val profileName: TextView = view.findViewById(R.id.top_tv)

        init {
            view.setOnClickListener(this)
        }

        fun bindData(data: PromotersResModel) {
            (context as BaseActivity).setImageWithGlide(profileImg, data.profileImage, R.drawable.default_profile_icon)
            profileName.text = data.name
        }

        override fun onClick(view: View) {
            val tag = AdapterTag()
            tag.pos = layoutPosition
            callback.onClick(view, tag)
        }

    }

}
