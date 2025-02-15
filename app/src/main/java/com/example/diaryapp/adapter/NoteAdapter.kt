package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R // Replace with your actual package name
import com.example.diaryapp.model.Note
import com.example.diaryapp.utils.appDateFormat
import com.example.diaryapp.utils.feelings

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var notes: List<Note> = emptyList()
    private var onItemClick: (Note) -> Unit = {}

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val feelingImageView: ImageView = itemView.findViewById(R.id.feelingImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.apply {
            val note = notes[position]

            if(note.title.isNotEmpty()) {
                titleTextView.text = note.title
            } else {
                titleTextView.visibility = View.GONE
            }

            if(note.content.isNotEmpty()) {
                contentTextView.text = note.content
            } else {
                contentTextView.visibility = View.GONE
            }

            if(note.feeling != null) {
                feelingImageView.setImageResource(feelings[note.feeling!!])
            } else {
                feelingImageView.visibility = View.GONE
            }

            dateTextView.text = appDateFormat.format(note.date)

            if(note.images != null) {
                note.images!!.split(",").toList().let {
                    val countLeft = it.size - 3
                    val imagesUrl = if(it.size > 3) { it.subList(0, 4) } else it
                    imagesRecyclerView.layoutManager = noteImageReviewLayout(itemView.context, imagesUrl.size)
                    imagesRecyclerView.adapter = NoteImageAdapter(images = imagesUrl, imagesLeftCount = countLeft)
                }
            } else {
                imagesRecyclerView.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(note) }
        }
    }

    override fun getItemCount(): Int = notes.size

    private fun noteImageReviewLayout(context: Context, count: Int) : GridLayoutManager {
        return when(count) {
            1 -> GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false)
            3 -> {
                GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false).apply {
                    spanSizeLookup = object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if(position == 2) { 2 } else { 1 }
                        }
                    }
                }
            }
            else -> GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun setOnItemClick(onItemClick: (Note) -> Unit) {
        this.onItemClick = onItemClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }
}
