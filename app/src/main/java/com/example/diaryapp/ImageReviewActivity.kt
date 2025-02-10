package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.diaryapp.adapter.ReviewImageAdapter
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.data.entity.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageReviewActivity : AppCompatActivity() {
    private var selectedPosition: Int = 0
    private var imagesUrl: MutableList<String> = mutableListOf()
    private lateinit var backButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var mainImageView: ImageView
    private lateinit var imagesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedPosition = intent.getIntExtra("position", 0)
        intent.extras!!.getString("images").let {
            if(it != null) {
                imagesUrl = it.split(",").toMutableList()
            }
        }

        backButton = findViewById<ImageButton>(R.id.backButton)
        deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        if(intent.getBooleanExtra("isInEditActivity", false)) {
            deleteButton.visibility = View.INVISIBLE
        }

        mainImageView = findViewById<ImageView>(R.id.mainImageView)
        mainImageView.let {
            Glide.with(this)
                .load(imagesUrl[selectedPosition])
                .into(it)
        }

        imagesRecyclerView = findViewById<RecyclerView>(R.id.imagesRecyclerView)
        imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesRecyclerView.adapter = ReviewImageAdapter(this, imagesUrl, selectedPosition, onItemClick = { position ->
            selectedPosition = position
            Glide.with(this)
                .load(imagesUrl[position])
                .into(mainImageView)
        })

        backButton.setOnClickListener {
            val intent = if(intent.getBooleanExtra("isInEditActivity", false)) {
                Intent(this, EditActivity::class.java)
            } else {
                Intent(this, CreateActivity::class.java)
            }
            intent.putExtras(this.intent.extras!!)
            intent.putExtra("images", imagesUrl.joinToString(","))
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            showDeleteDialog()
        }
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)

        builder.setView(dialogView)
        val dialog = builder.create()
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        val deleteButton: Button = dialogView.findViewById(R.id.deleteConfirmButton)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        deleteButton.setOnClickListener {
            imagesUrl.removeAt(selectedPosition)
            selectedPosition--;
            imagesRecyclerView.adapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            1000,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}