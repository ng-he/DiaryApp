package com.example.diaryapp.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diaryapp.AppAction
import com.example.diaryapp.DiaryApp
import com.example.diaryapp.ui.imageReview.ImageReviewActivity
import com.example.diaryapp.R
import com.example.diaryapp.adapter.CreateImageAdapter
import com.example.diaryapp.utils.appDateFormat
import com.example.diaryapp.model.Note
import com.example.diaryapp.databinding.ActivityDetailBinding
import com.example.diaryapp.dialog.DeleteDialog
import com.example.diaryapp.utils.feelings
import com.example.diaryapp.ui.create.CreateOrEditActivity
import com.example.diaryapp.ui.home.HomeActivity
import com.example.diaryapp.ui.permission.PermissionActivity
import com.example.diaryapp.utils.readImagesPermission
import com.example.diaryapp.viewModel.NoteViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityDetailBinding
    private val note = DiaryApp.currentAction?.note ?: Note()
    private var selectedImages = if (note.images != null) {
        note.images!!.split(",").toMutableSet()
    } else {
        mutableSetOf()
    }

    private lateinit var mNoteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mNoteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        initViews()
        initEvents()
    }

    private fun initViews() {
        if(note.feeling != null) {
            viewBinding.feelingImageView.setImageResource(feelings[note.feeling!!])
        } else {
            viewBinding.feelingTextView.visibility = View.GONE
        }

        val imagesUrl = selectedImages.toMutableList()
        if(imagesUrl.isNotEmpty()) {
            viewBinding.imagesRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = CreateImageAdapter(
                    context = this@DetailActivity,
                    imagesUrl = imagesUrl.toMutableList(),
                    onImageItemClick = { position ->
                        val intent = Intent(this@DetailActivity, ImageReviewActivity::class.java).putExtra("position", position)
                        startActivity(intent)
                    },
                    onDelete = {
                        selectedImages.remove(it)
                    }
                )
            }
        }

        viewBinding.titleEditText.apply {
            if (note.title.isNotEmpty()) {
                viewBinding.titleEditText.setText(note.title)
            } else {
                visibility = View.GONE
            }
        }

        viewBinding.contentEditText.apply {
            if (note.content.isNotEmpty()) {
                viewBinding.contentEditText.setText(note.content)
            } else {
                visibility = View.GONE
            }
        }

        viewBinding.dateTextView.text = appDateFormat.format(note.date) // Update the button text
    }

    private fun initEvents() {
        viewBinding.backButton.setOnClickListener {
            DiaryApp.currentAction = null
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        viewBinding.deleteButton.setOnClickListener {
            showDeleteDialog()
        }

        viewBinding.editButton.setOnClickListener {
            DiaryApp.currentAction = AppAction.Edit(note)
            startActivity(Intent(this, CreateOrEditActivity::class.java))
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showDeleteDialog() {
        val deleteDialog = DeleteDialog(
            context = this@DetailActivity,
            builder = AlertDialog.Builder(this, R.style.CustomDialogTheme),
            onCancel = { dialog -> dialog.dismiss() },
            onDelete = { dialog ->
                try {
                    mNoteViewModel.deleteNote(note)
                    dialog.dismiss()
                    Toast.makeText(this@DetailActivity, getString(R.string.delete_note_success), Toast.LENGTH_LONG).show()
                    DiaryApp.currentAction = null
                    finish()
                } catch (ex: Exception) {
                    Toast.makeText(this@DetailActivity, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        )

        deleteDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if(note.images != null) {
            if (readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
                startActivity(Intent(this, PermissionActivity::class.java).putExtra("fromActivity", "DETAIL"))
            }
        }
    }

    override fun onBackPressed() {
        DiaryApp.currentAction = null
        startActivity(Intent(this, HomeActivity::class.java))
        finish()

        super.onBackPressed()
    }
}