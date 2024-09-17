package com.example.mini_widget_n01.adaper

import android.content.Context
import android.icu.text.Transliterator.Position
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.mini_widget_n01.db.entity.PhotoModel

class ImageAdapter(private val context: Context,private val imageList: List<PhotoModel>) : BaseAdapter(){
    override fun getCount(): Int  = imageList.size

    override fun getItem(p0: Int): Any = imageList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        var imageView: ImageView

        if(view == null){
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(300, 300)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView = view as ImageView
        }

        val photoModel  = imageList[position]
        val imageUri = Uri.parse(photoModel.path)
        imageView.setImageURI(imageUri)
        return imageView
    }
}
