package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diaryapp.R

class NoteImageAdapter (
    private val context: Context,
    private val images: List<String>,
    private val imagesLeftCount: Int = 0,
) : RecyclerView.Adapter<NoteImageAdapter.NoteImageViewHolder>() {

    inner class NoteImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageItemView: ImageView = itemView.findViewById(R.id.imageItemView)
        val countLeftTextView: TextView = itemView.findViewById(R.id.countLeftTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_image_item, parent, false)
        return NoteImageViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: NoteImageViewHolder, position: Int) {
        Glide.with(context)
            .load(images[position])
            .into(holder.imageItemView)

//        if(images.size == 3) {
//            when(position) {
//                0, 1 -> {
//                    holder.imageItemView.layoutParams.width = 32
//                    holder.imageItemView.layoutParams.height = 32
//                }
//
//                2 -> {
//                    holder.imageItemView.layoutParams.width = LayoutParams.MATCH_PARENT
//                    holder.imageItemView.layoutParams.height = LayoutParams.MATCH_PARENT
//                }
//            }
//
//        } else if(images.size > 3) {
//            holder.imageItemView.layoutParams.width = 32
//            holder.imageItemView.layoutParams.height = 32
//        }


        if (position == 3) {
            holder.countLeftTextView.text = "+${imagesLeftCount}"
            holder.countLeftTextView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = images.size
}