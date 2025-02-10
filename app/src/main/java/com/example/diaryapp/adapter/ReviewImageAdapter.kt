package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diaryapp.R
import com.google.android.material.card.MaterialCardView

class ReviewImageAdapter (
    private val context: Context,
    private val imagesUrl: MutableList<String>,
    private var selectedPosition: Int = 0,
    private val onItemClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<ReviewImageAdapter.ReviewImageViewHolder>() {

    inner class ReviewImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemReviewImageView: ImageView = itemView.findViewById(R.id.itemReviewImageView)
        val imageCardView: MaterialCardView = itemView.findViewById(R.id.imageCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_review_item, parent, false)
        return ReviewImageViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ReviewImageViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if(imagesUrl.isNotEmpty()) {
            Glide.with(context)
                .load(imagesUrl[position])
                .into(holder.itemReviewImageView)

            holder.imageCardView.strokeWidth = if(selectedPosition == position) { 5 } else { 0 }

            holder.itemView.setOnClickListener {
                selectedPosition = position
                onItemClick(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = imagesUrl.size
}