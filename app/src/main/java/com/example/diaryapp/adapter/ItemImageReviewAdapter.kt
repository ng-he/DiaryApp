package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diaryapp.R
import com.google.android.material.card.MaterialCardView

class ItemImageReviewAdapter (
    private val imagesUrl: MutableList<String>,
    private var selectedPosition: MutableLiveData<Int>,
    private val onItemClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<ItemImageReviewAdapter.ItemImageViewHolder>() {

    inner class ItemImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemReviewImageView: ImageView = itemView.findViewById(R.id.itemReviewImageView)
        val imageCardView: MaterialCardView = itemView.findViewById(R.id.imageCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_review_item, parent, false)
        return ItemImageViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemImageViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if(imagesUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imagesUrl[position])
                .into(holder.itemReviewImageView)

            holder.imageCardView.strokeWidth = if(selectedPosition.value == position) { 5 } else { 0 }

            holder.itemView.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = imagesUrl.size
}