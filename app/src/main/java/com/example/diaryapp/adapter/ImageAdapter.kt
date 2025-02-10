package com.example.diaryapp.adapter

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diaryapp.R

class ImageAdapter(
    private val context: Context,
    private val images: List<String>,
    private val selectedImages: MutableSet<String> = mutableSetOf(),
) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return images[position].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val image = images[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.image_item, parent, false)
        val imageView: ImageView = view.findViewById(R.id.imageItem)
        val checkedImageView: ImageView = view.findViewById(R.id.checkedImageView)

        Glide.with(context)
            .load(image)
            .into(imageView)

        checkedImageView.visibility =
            if(selectedImages.contains(image)) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

        view.setOnClickListener {
            if(!selectedImages.contains(image)) {
                selectedImages.add(image)
            } else {
                selectedImages.remove(image)
            }
            notifyDataSetChanged()
        }

        return view
    }
}