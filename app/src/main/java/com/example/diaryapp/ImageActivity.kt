package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Grid
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.diaryapp.adapter.ImageAdapter

class ImageActivity : AppCompatActivity() {
    private var selectedImages = mutableSetOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent.extras!!.getString("images").let {
            if(it != null) {
                selectedImages = it.split(",").toMutableSet()
            }
        }

        val imagesGridView = findViewById<GridView>(R.id.imagesGridView)
        imagesGridView.adapter = ImageAdapter(this, fetchImages(), selectedImages)

        val backBtn = findViewById<ImageButton>(R.id.backButton)
        backBtn.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtras(this.intent.extras!!)
            startActivity(intent)
        }

        val doneBtn = findViewById<Button>(R.id.doneButton)
        doneBtn.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtras(this.intent.extras!!)
            intent.putExtra("images", selectedImages.joinToString(","))
            startActivity(intent)
        }
    }

    private fun fetchImages(): List<String> {
        val imageList = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)

                // Create a content URI for the image
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageList.add(uri.toString())
                println("Image: $name, URI: $uri")
            }
        }

        return imageList
    }
}