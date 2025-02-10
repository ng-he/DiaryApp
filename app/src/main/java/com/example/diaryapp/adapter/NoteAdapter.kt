package com.example.diaryapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R // Replace with your actual package name
import com.example.diaryapp.data.entity.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val context: Context,
    private val notes: List<Note>,
    private val onItemClick: (Note) -> Unit = {}
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // ViewHolder for binding views
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val feelingImageView: ImageView = itemView.findViewById(R.id.feelingImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        // Set title
        holder.titleTextView.text = note.title

        // Set content
        holder.contentTextView.text = note.content

        // Set formatted date
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        holder.dateTextView.text = dateFormat.format(note.date)

        var imagesUrl = note.images?.split(",")?.toList()
        if(imagesUrl != null) {
            val countLeft = imagesUrl.size - 3
            if(imagesUrl.size > 3) {
                imagesUrl = imagesUrl.subList(0, 4)
            }

            val imageAdapter = NoteImageAdapter(context = context, images = imagesUrl, imagesLeftCount = countLeft)
            holder.imagesRecyclerView.layoutManager = noteImageReviewLayout(imagesUrl.size)
            holder.imagesRecyclerView.adapter = imageAdapter
        }

        // Set feeling image based on the feeling value
        val feelingIconRes = when (note.feeling) {
            1 -> R.drawable.ic_feeling_1
            2 -> R.drawable.ic_feeling_2
            3 -> R.drawable.ic_feeling_3
            4 -> R.drawable.ic_feeling_4
            5 -> R.drawable.ic_feeling_5
            6 -> R.drawable.ic_feeling_6
            else -> R.drawable.ic_feeling_1 // Default
        }
        holder.feelingImageView.setImageResource(feelingIconRes)

        // Placeholder for setting GridView images (adjust as needed)
        // You can use an adapter for GridView here if images are in a list
        // e.g., holder.imagesGridView.adapter = ImagesAdapter(context, imageList)

        holder.itemView.setOnClickListener {
            onItemClick(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    private fun noteImageReviewLayout(count: Int) : GridLayoutManager {
        return when(count) {
            1 -> GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false)
            else -> GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}
