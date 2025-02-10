package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R

class FeelingAdapter (private val feelings: List<Int>, private val onItemClick: (Int) -> Unit = {}) :
    RecyclerView.Adapter<FeelingAdapter.FeelingViewHolder>() {

    inner class FeelingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.feelingImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeelingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feeling_item, parent, false)
        return FeelingViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: FeelingViewHolder, position: Int) {
        holder.icon.setImageResource(feelings[position])

        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = feelings.size
}