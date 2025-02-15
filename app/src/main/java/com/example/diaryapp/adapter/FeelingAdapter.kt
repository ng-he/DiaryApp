package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R
import com.google.android.material.card.MaterialCardView

class FeelingAdapter(
    private val feelings: List<Int>,
    private var selectedFeeling: Int? = null,
    private val onItemClick: (Int?) -> Unit = {},
) : RecyclerView.Adapter<FeelingAdapter.FeelingViewHolder>() {

    inner class FeelingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.feelingImage)
        val cardView: MaterialCardView = itemView.findViewById(R.id.feelingImageCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeelingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feeling_item, parent, false)
        return FeelingViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: FeelingViewHolder, position: Int) {
        holder.icon.setImageResource(feelings[position])
        holder.cardView.strokeWidth = if(selectedFeeling == position) { 10 } else { 0 }

        holder.itemView.setOnClickListener {
            selectedFeeling = if(selectedFeeling != position) position else null
            onItemClick(selectedFeeling)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = feelings.size
}