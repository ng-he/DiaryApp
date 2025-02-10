package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Intent
import com.example.diaryapp.adapter.LanguageAdapter
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LanguageActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val doneBtn = findViewById<ImageButton>(R.id.doneButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LanguageAdapter(languages, SharedPrefsManager(this), onItemClick = {
            setAppLocale(it, this.resources)
            finish()
            startActivity(intent)
        })

        doneBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

