package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.gesture.GestureLibrary
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.adapter.CreateImageAdapter
import com.example.diaryapp.adapter.FeelingAdapter
import com.example.diaryapp.adapter.LanguageAdapter
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.data.entity.Note
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CreateActivity : AppCompatActivity() {
    private lateinit var feelingRecyclerView: RecyclerView
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var backBtn: ImageButton
    private lateinit var imageLibraryButton : ImageButton
    private lateinit var saveBtn: Button
    private lateinit var dateButton: LinearLayout
    private lateinit var dateTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    private var noteId: Int = Int.MIN_VALUE
    private lateinit var note: Note
    private var selectedImages = mutableSetOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setLocale(SharedPrefsManager(this).getLanguage())

        intent.extras?.let {
            noteId = it.getInt("noteId", Int.MIN_VALUE)
            note = if(noteId != Int.MIN_VALUE) {
                Note(
                    noteId = noteId,
                    title = it.getString("title", ""),
                    content = it.getString("content", ""),
                    date = Date(it.getLong("date", Date().time)),
                    images = it.getString("images", null)
                )
            } else {
                Note(
                    title = it.getString("title", ""),
                    content = it.getString("content", ""),
                    date = Date(it.getLong("date", Date().time)),
                    images = it.getString("images", null)
                )
            }

            if(note.images != null) {
                selectedImages = note.images!!.split(",").toMutableSet()
            }
        }

        setContentView(R.layout.activity_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initEvents()

        titleEditText.setText(note.title)
        contentEditText.setText(note.content)
        dateTextView.text = appDateFormat.format(note.date) // Update the button text
    }

    private fun initViews() {
        feelingRecyclerView = findViewById(R.id.feelingRecyclerView)
        feelingRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        feelingRecyclerView.adapter = FeelingAdapter(feelings, onItemClick = {
            note.feeling = it + 1
        })

        val imagesUrl = selectedImages.toMutableList()
        if(imagesUrl.isNotEmpty()) {
            imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
            imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            imagesRecyclerView.adapter =
                CreateImageAdapter(this, imagesUrl.toMutableList(),
                    onImageItemClick = {
                        val intent = Intent(this, ImageReviewActivity::class.java)
                        saveUserInput()

                        if(noteId != Int.MIN_VALUE) {
                            intent.putExtra("noteId", noteId)
                        }

                        intent.putExtra("position", it)
                        intent.putExtras(toBundle(note))
                        startActivity(intent)
                    },
                    onDelete = {
                        selectedImages.remove(it)
                    }
                )
        }

        backBtn = findViewById(R.id.backButton)
        saveBtn = findViewById(R.id.saveButton)
        imageLibraryButton = findViewById(R.id.imageLibraryButton)
        dateButton = findViewById(R.id.date)

        dateTextView = findViewById(R.id.dateTextView)
        dateTextView.text = appDateFormat.format(Date())

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
    }

    private fun initEvents() {
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imageLibraryButton.setOnClickListener {
            val intent = if (readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
                Intent(this, PermissionActivity::class.java)
            } else {
                Intent(this, ImageActivity::class.java)
            }

            saveUserInput()
            intent.putExtras(toBundle(note))
            startActivity(intent)
        }

        dateButton.setOnClickListener {
            // Create a date picker
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()) // Disable past dates

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(note.date.time) // Default to today
                .setTheme(R.style.CustomDatePickerTheme)
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                note.date = Date(selection)
                dateTextView.text = appDateFormat.format(note.date)
            }
        }

        val backToMainActivity: () -> Unit = {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        saveBtn.setOnClickListener {
            saveUserInput()
            val db = AppDatabase.getDatabase(this)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if(noteId != Int.MIN_VALUE) {
                        db.noteDao().updateNote(note)
                    } else {
                        db.noteDao().insertNote(note)
                    }
                    withContext(Dispatchers.Main) {
                        backToMainActivity()
                    }
                } catch (ex: Exception) {
                    println(ex.message)
                }
            }
        }
    }

    private fun saveUserInput() {
        note.title = titleEditText.text.toString()
        note.content = contentEditText.text.toString()
        note.images = if(selectedImages.isNotEmpty()) selectedImages.joinToString(",") else null
    }
}