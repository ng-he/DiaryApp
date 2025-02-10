package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.adapter.CreateImageAdapter
import com.example.diaryapp.adapter.FeelingAdapter
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.data.entity.Note
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditActivity : AppCompatActivity() {
    private lateinit var feelingRecyclerView : RecyclerView
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var backBtn : ImageButton
    private lateinit var dateButton : LinearLayout
    private lateinit var dateTextView : TextView
    private lateinit var deleteButton : ImageButton
    private lateinit var editButton : ImageButton
    private lateinit var titleEditText : EditText
    private lateinit var contentEditText : EditText

    var note = Note()
    private var selectedImages = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setLocale(SharedPrefsManager(this).getLanguage())

        intent.extras?.let {
            note = Note(
                noteId = it.getInt("noteId", Int.MIN_VALUE),
                title = it.getString("title", ""),
                content = it.getString("content", ""),
                date = Date(it.getLong("date", Date().time)),
                images = it.getString("images", null)
            )

            if(note.images != null) {
                selectedImages = note.images!!.split(",").toMutableSet()
            }
        }

        setContentView(R.layout.activity_edit)
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
        feelingRecyclerView.adapter = FeelingAdapter(feelings)

        val imagesUrl = selectedImages.toMutableList()
        if(imagesUrl.isNotEmpty()) {
            imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
            imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            imagesRecyclerView.adapter =
                CreateImageAdapter(this, imagesUrl.toMutableList(),
                    onImageItemClick = {
                        val intent = Intent(this, ImageReviewActivity::class.java)

                        intent.putExtra("noteId", note.noteId)
                        intent.putExtra("isInEditActivity", true)
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
        dateButton = findViewById(R.id.date)
        dateTextView = findViewById(R.id.dateTextView)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
    }

    private fun initEvents() {
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            showDeleteDialog(note)
        }

        editButton.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra("noteId", note.noteId)
            intent.putExtras(toBundle(note))
            startActivity(intent)
        }

        dateButton.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(note.date.time)
                .setTheme(R.style.CustomDatePickerTheme)
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener { selection ->
                note.date = Date(selection)
                dateTextView.text = appDateFormat.format(note.date)
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showDeleteDialog(note: Note) {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
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
            val db = AppDatabase.getDatabase(this)

            val backToMainActivity = {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val id = db.noteDao().deleteNote(note)
                println(id)
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    backToMainActivity()
                }
            }
        }

        dialog.show()
        dialog.window?.setLayout(
            1000,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}