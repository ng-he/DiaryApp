package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diaryapp.ui.detail.DetailActivity
import com.example.diaryapp.R

class CreateImageAdapter (
    private val context: Context,
    private val imagesUrl: MutableList<String>,
    private val onImageItemClick: (Int) -> Unit = {},
    private val onDelete: (String) -> Unit = {}
) : RecyclerView.Adapter<CreateImageAdapter.CreateImageViewHolder>() {

    inner class CreateImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCreateView: ImageView = itemView.findViewById(R.id.imageCreateView)
        val deleteImageButton: ImageButton = itemView.findViewById(R.id.deleteImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.create_image_item, parent, false)
        return CreateImageViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CreateImageViewHolder, position: Int) {
        if(imagesUrl.isNotEmpty()) {
            if(context is DetailActivity) {
                holder.deleteImageButton.visibility = View.INVISIBLE
            }

            Glide.with(context)
                .load(imagesUrl[position])
                .into(holder.imageCreateView)

            holder.deleteImageButton.setOnClickListener {
                onDelete(imagesUrl[position])
                imagesUrl.removeAt(position)
                notifyDataSetChanged()
            }

            holder.imageCreateView.setOnClickListener {
                onImageItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = imagesUrl.size
}