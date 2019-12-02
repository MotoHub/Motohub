package online.motohub.newdesign.adapter

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adap_business_video.view.*
import online.motohub.R
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.model.GalleryVideoResModel
import online.motohub.tags.AdapterTag
import online.motohub.util.UrlUtils
import java.net.URLDecoder

class BusinessVideoAdapter(private val context: Context, private val videosList: ArrayList<GalleryVideoResModel>,
                           private val callBack: AdapterClickCallBack) : androidx.recyclerview.widget.RecyclerView.Adapter<BusinessVideoAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.adap_business_video, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(videosList[position])
    }

    override fun getItemCount(): Int {
        return videosList.size
    }


    inner class Holder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        var img: ImageView = v.img
        var titleTv: TextView = v.titleTxt
        var cardView: androidx.cardview.widget.CardView = v.cardView

        fun bindView(data: GalleryVideoResModel) {
            val tag = AdapterTag()
            tag.pos = layoutPosition
            Glide.with(context)
                    .load(UrlUtils.AWS_S3_BASE_URL + data.thumbnail)
                    .apply(RequestOptions()
                            .dontAnimate().error(R.drawable.video_place_holder))
                    .into(img)

            cardView.tag = tag
            cardView.setOnClickListener {
                callBack.onClick(it!!, it.tag as AdapterTag?)
            }

            if (!TextUtils.isEmpty(data.caption)) {
                if (data.caption.contains(" "))
                    titleTv.text = data.caption
                else {
                    titleTv.text = URLDecoder.decode(data.caption, "UTF-8")
                }
            }
        }
    }

}
