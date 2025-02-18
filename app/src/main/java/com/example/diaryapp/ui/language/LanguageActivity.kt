package com.example.diaryapp.ui.language

import android.annotation.SuppressLint
import android.content.Intent
import com.example.diaryapp.adapter.LanguageAdapter
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.common.DiaryApp
import com.example.diaryapp.ui.home.HomeActivity
import com.example.diaryapp.R
import com.example.diaryapp.common.SharedPrefsManager
import com.example.diaryapp.common.languages
import com.example.diaryapp.common.setAppLocale

class LanguageActivity : AppCompatActivity() {
    private lateinit var selectedLanguage: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val doneBtn = findViewById<ImageButton>(R.id.doneButton)

        selectedLanguage = DiaryApp.language

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LanguageAdapter(languages, onItemClick = {
            selectedLanguage = it
        })

        doneBtn.setOnClickListener {
            if(selectedLanguage == "") {
                Toast.makeText(this, "Choose your language first", Toast.LENGTH_LONG).show()
            } else {
                DiaryApp.language = selectedLanguage
                SharedPrefsManager(this).saveLanguage(selectedLanguage)
                setAppLocale(selectedLanguage, this.resources)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}

