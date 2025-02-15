package com.example.diaryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diaryapp.R

class MainImageReviewAdapter(
    private val imagesUrl: MutableList<String>
): RecyclerView.Adapter<MainImageReviewAdapter.MainImageViewHolder>() {

    inner class MainImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainImageView: ImageView = itemView.findViewById(R.id.mainImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_image_review_item, parent, false)
        return MainImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainImageViewHolder, position: Int) {
        if(imagesUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imagesUrl[position])
                .into(holder.mainImageView)
        }
    }

    override fun getItemCount(): Int = imagesUrl.size
}