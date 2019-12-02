package online.motohub.newdesign.adapter

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adap_business_photo.view.*


import online.motohub.R
import online.motohub.interfaces.AdapterClickCallBack
import online.motohub.model.GalleryImgResModel
import online.motohub.tags.AdapterTag
import online.motohub.util.UrlUtils

class BusinessPhotosAdapter(private val context: Context, private val photosList: ArrayList<GalleryImgResModel>,
                            private val callBack: AdapterClickCallBack) : androidx.recyclerview.widget.RecyclerView.Adapter<BusinessPhotosAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.adap_business_photo, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(photosList[position])
    }

    override fun getItemCount(): Int {
        return photosList.size
    }

    inner class Holder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        var img: ImageView = v.img
        var cardView: androidx.cardview.widget.CardView = v.cardView

        fun bindView(data: GalleryImgResModel) {
            val tag = AdapterTag()
            tag.pos = layoutPosition
            Glide.with(context)
                    .load(UrlUtils.AWS_S3_BASE_URL + data.galleryImage)
                    .apply(RequestOptions()
                            .dontAnimate().error(R.drawable.img_place_holder))
                    .into(img)

            cardView.tag = tag
            cardView.setOnClickListener {
                callBack.onClick(it!!, it.tag as AdapterTag?)
            }
        }
    }


}
