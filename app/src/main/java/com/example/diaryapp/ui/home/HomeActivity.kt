package com.example.diaryapp.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diaryapp.common.AppAction
import com.example.diaryapp.common.DiaryApp
import com.example.diaryapp.R
import com.example.diaryapp.adapter.NoteAdapter
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.databinding.ActivityMainBinding
import com.example.diaryapp.model.Note
import com.example.diaryapp.repository.NoteRepository
import com.example.diaryapp.ui.create.CreateOrEditActivity
import com.example.diaryapp.ui.detail.DetailActivity
import com.example.diaryapp.ui.language.LanguageActivity
import com.example.diaryapp.ui.permission.PermissionActivity
import com.example.diaryapp.common.languages
import com.example.diaryapp.common.readImagesPermission
import com.example.diaryapp.viewModel.NoteViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var mNoteViewModel: NoteViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mNoteViewModel = NoteViewModel(application, NoteRepository(AppDatabase.getDatabase(application).noteDao()))

        initViews()
        initEvents()
    }

    private fun initViews() {
        val noteAdapter = NoteAdapter()
        noteAdapter.setOnItemClick { note ->
            DiaryApp.currentAction = AppAction.Detail(note)
            startActivity(Intent(this, DetailActivity::class.java))
        }

        viewBinding.noteRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = noteAdapter
        }

        try {
            mNoteViewModel.readAllNotes.observe(this) { notes ->
                viewBinding.emptyAlert.visibility = if(notes.isEmpty()) { View.VISIBLE } else { View.INVISIBLE }
                noteAdapter.setData(notes)
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

        languages.forEach {
            if (DiaryApp.language == it.code) {
                viewBinding.languageButton.setImageResource(it.icon)
            }
        }
    }

    private fun initEvents() {
        viewBinding.languageButton.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        viewBinding.createButton.setOnClickListener {
            DiaryApp.currentAction = AppAction.Create(Note())
            startActivity(Intent(this, CreateOrEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        mNoteViewModel.readAllNotes.value?.forEach { note ->
            if(note.images != null) {
                if (readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
                    startActivity(Intent(this, PermissionActivity::class.java).putExtra("fromActivity", "HOME"))
                }
            }
        }
    }
}