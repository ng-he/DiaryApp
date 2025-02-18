package com.example.diaryapp.ui.create

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diaryapp.common.AppAction
import com.example.diaryapp.common.DiaryApp
import com.example.diaryapp.ui.imageReview.ImageReviewActivity
import com.example.diaryapp.R
import com.example.diaryapp.adapter.CreateImageAdapter
import com.example.diaryapp.adapter.FeelingAdapter
import com.example.diaryapp.common.DatabaseActionState
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.common.appDateFormat
import com.example.diaryapp.model.Note
import com.example.diaryapp.databinding.ActivityCreateBinding
import com.example.diaryapp.repository.NoteRepository
import com.example.diaryapp.ui.detail.DetailActivity
import com.example.diaryapp.common.feelings
import com.example.diaryapp.common.readImagesPermission
import com.example.diaryapp.common.showErrorAlert
import com.example.diaryapp.ui.home.HomeActivity
import com.example.diaryapp.ui.image.ImageActivity
import com.example.diaryapp.ui.permission.PermissionActivity
import com.example.diaryapp.utils.DatePicker
import com.example.diaryapp.viewModel.NoteViewModel
import java.util.*

class CreateOrEditActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCreateBinding
    private lateinit var mNoteViewModel: NoteViewModel

    private val note: Note = DiaryApp.currentAction?.note ?: Note()
    private var selectedImages = if (note.images != null) {
        note.images!!.split(",").toMutableSet()
    } else {
        mutableSetOf()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityCreateBinding.inflate(layoutInflater)
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
        viewBinding.feelingRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateOrEditActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = FeelingAdapter(feelings, selectedFeeling = note.feeling, onItemClick = {
                note.feeling = it
            })
        }

        viewBinding.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateOrEditActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = CreateImageAdapter(this@CreateOrEditActivity, selectedImages.toMutableList(),
                onImageItemClick = { position ->
                    saveUserInput()
                    val intent = Intent(this@CreateOrEditActivity, ImageReviewActivity::class.java).putExtra("position", position)
                    startActivity(intent)
                },
                onDelete = {
                    selectedImages.remove(it)
                    if(selectedImages.isEmpty()) {
                        note.images = null
                    }
                }
            )
        }

        viewBinding.titleEditText.setText(note.title)
        viewBinding.contentEditText.setText(note.content)
        viewBinding.dateTextView.text = appDateFormat.format(note.date)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initEvents() {
        viewBinding.backButton.setOnClickListener {
            val intent = if(DiaryApp.currentAction is AppAction.Edit) {
                Intent(this, DetailActivity::class.java)
            } else {
                Intent(this, HomeActivity::class.java)
            }

            DiaryApp.currentAction = if(DiaryApp.currentAction is AppAction.Edit) { AppAction.Detail(note) } else { null }
            startActivity(intent)
            finish()
        }

        viewBinding.imageLibraryButton.setOnClickListener {
            saveUserInput()
            if (readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
                startActivity(Intent(this, PermissionActivity::class.java))
            } else {
                startActivity(Intent(this, ImageActivity::class.java))
            }
        }

        viewBinding.date.setOnClickListener {
            val datePicker = DatePicker(
                context = this,
                fragmentManager = supportFragmentManager,
                defaultSelect = note.date.time,
                onPositiveButtonClickListener = { selection ->
                    note.date = Date(selection)
                    viewBinding.dateTextView.text = appDateFormat.format(note.date)
                }
            )

            datePicker.show()
        }

        viewBinding.saveButton.setOnClickListener {
            saveUserInput()

            if(note.title.isEmpty() && note.content.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_title_or_content_at_least), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            when(DiaryApp.currentAction) {
                is AppAction.Create -> {
                    mNoteViewModel.insertNote(note) { state ->
                        when(state) {
                            is DatabaseActionState.Error -> {
                                showErrorAlert(this, state.ex.message ?: "")
                            }

                            DatabaseActionState.Success -> {
                                Toast.makeText(this, getString(R.string.you_added_a_new_note), Toast.LENGTH_LONG).show()
                                DiaryApp.currentAction = null
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                        }
                    }
                }

                is AppAction.Edit ->  {
                    mNoteViewModel.updateNote(note) { state ->
                        when(state) {
                            is DatabaseActionState.Error -> {
                                showErrorAlert(this, state.ex.message ?: "")
                            }

                            DatabaseActionState.Success -> {
                                Toast.makeText(this, getString(R.string.updated_success), Toast.LENGTH_LONG).show()
                                DiaryApp.currentAction = AppAction.Detail(note)
                                startActivity(Intent(this, DetailActivity::class.java))
                            }
                        }
                    }
                }

                else -> {
                    Toast.makeText(this, "System error: Invalid app action (${DiaryApp.currentAction})", Toast.LENGTH_LONG).show()
                    DiaryApp.currentAction = null
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }

            finish()
        }
    }

    private fun saveUserInput() {
        note.title = viewBinding.titleEditText.text.toString()
        note.content = viewBinding.contentEditText.text.toString()
        note.images = if(selectedImages.isNotEmpty()) selectedImages.joinToString(",") else null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        if(note.images != null) {
            if (readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
                startActivity(Intent(this, PermissionActivity::class.java).putExtra("fromActivity", "CREATE"))
            }
        }
    }

    override fun onBackPressed() {
        val intent = if(DiaryApp.currentAction is AppAction.Edit) {
            Intent(this, DetailActivity::class.java)
        } else {
            Intent(this, HomeActivity::class.java)
        }

        DiaryApp.currentAction = if(DiaryApp.currentAction is AppAction.Edit) { AppAction.Detail(note) } else { null }
        startActivity(intent)
        finish()

        super.onBackPressed()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val focusedView = currentFocus
            val outRect = Rect()

            if(focusedView !is EditText && !outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                }
                focusedView?.clearFocus()
            }
        }
        return super.dispatchTouchEvent(event)
    }
}