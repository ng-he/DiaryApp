package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.adapter.NoteAdapter
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.data.entity.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var emptyAlert: LinearLayout

    private lateinit var language: ImageButton
    private lateinit var createBtn: ImageButton
    private lateinit var noteRecyclerView: RecyclerView

    private var sharedPrefsManager = SharedPrefsManager(DiaryApp.instance)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        initEvents()

        val db = AppDatabase.getDatabase(this)
        val setNoteViewData: (List<Note>) -> Unit = {
            val noteAdapter = NoteAdapter(context = this, notes = it, onItemClick = { note ->
                val intent = Intent(this, EditActivity::class.java)
                val bundle = Bundle().apply {
                    putInt("noteId", note.noteId)
                    putString("title", note.title)
                    putString("content", note.content)
                    putLong("date", note.date.time)
                    putInt("feelings", note.feeling)
                    putString("images", note.images)
                }

                intent.putExtras(bundle)
                startActivity(intent)
            })
            noteRecyclerView.layoutManager = LinearLayoutManager(this)
            noteRecyclerView.adapter = noteAdapter
        }

        CoroutineScope(Dispatchers.IO).launch {
             val notes = db.noteDao().getAllNotes()
             withContext(Dispatchers.Main) {
                 emptyAlert = findViewById(R.id.emptyAlert)
                 emptyAlert.visibility = if(notes.isEmpty()) {
                     View.VISIBLE
                 } else {
                     View.INVISIBLE
                 }

                 setNoteViewData(notes)
             }
        }
    }

    private fun initViews() {
        language = findViewById(R.id.languageButton)
        languages.forEach {
            if (sharedPrefsManager.getLanguage() == it.code) {
                language.setImageResource(it.icon)
            }
        }

        createBtn = findViewById(R.id.createButton)
        noteRecyclerView = findViewById(R.id.noteRecyclerView)
    }

    private fun initEvents() {
        language.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        createBtn.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            val bundle = Bundle().apply {
                putString("title", "")
                putString("content", "")
                putLong("date", Date().time)
                putInt("feelings", 0)
                putString("images", null)
            }

            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}