package com.example.diaryapp.ui.imageReview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.diaryapp.common.AppAction
import com.example.diaryapp.common.DiaryApp
import com.example.diaryapp.R
import com.example.diaryapp.adapter.ItemImageReviewAdapter
import com.example.diaryapp.adapter.MainImageReviewAdapter
import com.example.diaryapp.databinding.ActivityImageReviewBinding
import com.example.diaryapp.dialog.DeleteDialog
import com.example.diaryapp.ui.create.CreateOrEditActivity
import com.example.diaryapp.ui.detail.DetailActivity
import com.example.diaryapp.ui.permission.PermissionActivity
import com.example.diaryapp.common.readImagesPermission

class ImageReviewActivity : AppCompatActivity() {
    private val selectedPosition = MutableLiveData<Int>()
    private var imagesUrl = mutableListOf<String>()
    private lateinit var viewBinding: ActivityImageReviewBinding
    private val mainImageRecycleViewSnapHelper: SnapHelper = PagerSnapHelper()
    private val imagesRecyclerViewSnapHelper: SnapHelper = LinearSnapHelper()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityImageReviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagesUrl = DiaryApp.currentAction?.note?.images?.split(",")?.toMutableList() ?: mutableListOf()
        
        selectedPosition.value = intent.getIntExtra("position", 0)
        selectedPosition.observe(this) { value ->
            (viewBinding.mainImageRecycleView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(value, 0)
            (viewBinding.imagesRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                value, ((DiaryApp.instance.resources.displayMetrics.widthPixels / 2) - ((64 * DiaryApp.deviceDensity) / 2) - (16 * DiaryApp.deviceDensity)).toInt()
            )
            viewBinding.imagesRecyclerView.adapter!!.notifyDataSetChanged()
        }

        initView()
        initEvent()
    }

    private fun initView() {
        if(DiaryApp.currentAction is AppAction.Detail) {
            viewBinding.deleteButton.visibility = View.INVISIBLE
        }

        viewBinding.mainImageRecycleView.apply {
            layoutManager = LinearLayoutManager(this@ImageReviewActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = MainImageReviewAdapter(imagesUrl)
            mainImageRecycleViewSnapHelper.attachToRecyclerView(viewBinding.mainImageRecycleView)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val snapView = mainImageRecycleViewSnapHelper.findSnapView(layoutManager)
                        val newPosition = (layoutManager as LinearLayoutManager).getPosition(snapView!!)
                        if (newPosition != selectedPosition.value!!) {
                            selectedPosition.value = newPosition
                        }
                    }
                }
            })
        }

        viewBinding.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ImageReviewActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ItemImageReviewAdapter(imagesUrl, selectedPosition, onItemClick = { position -> selectedPosition.value = position })
            imagesRecyclerViewSnapHelper.attachToRecyclerView(viewBinding.imagesRecyclerView)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val snapView = imagesRecyclerViewSnapHelper.findSnapView(layoutManager)
                        val newPosition = (layoutManager as LinearLayoutManager).getPosition(snapView!!)
                        if (newPosition != selectedPosition.value!!) {
                            selectedPosition.value = newPosition
                        }
                    }
                }
            })
        }
    }

    private fun initEvent() {
        viewBinding.backButton.setOnClickListener {
            val intent = if(DiaryApp.currentAction is AppAction.Detail) {
                Intent(this, DetailActivity::class.java)
            } else {
                Intent(this, CreateOrEditActivity::class.java)
            }

            DiaryApp.currentAction?.note?.images = if (imagesUrl.isNotEmpty()) imagesUrl.joinToString(",") else null
            startActivity(intent)
            finish()
        }

        viewBinding.deleteButton.setOnClickListener {
            showDeleteDialog()
        }
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    private fun showDeleteDialog() {
        val deleteDialog = DeleteDialog(this@ImageReviewActivity)

        deleteDialog.setOnCancelClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.setOnDeleteClickListener {
            imagesUrl.removeAt(selectedPosition.value!!)
            if(imagesUrl.size == 0) {
                deleteDialog.dismiss()
                DiaryApp.currentAction?.note?.images = null
                finish()
            } else {
                if(selectedPosition.value!! > 0) {
                    selectedPosition.value = selectedPosition.value!! - 1
                }
                viewBinding.imagesRecyclerView.adapter!!.notifyDataSetChanged()
                viewBinding.mainImageRecycleView.adapter!!.notifyDataSetChanged()
                deleteDialog.dismiss()
            }
        }

        deleteDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            startActivity(Intent(this, PermissionActivity::class.java).putExtra("fromActivity", "IMAGE_REVIEW"))
        }
    }

    override fun onBackPressed() {
        val intent = if(DiaryApp.currentAction is AppAction.Detail) {
            Intent(this, DetailActivity::class.java)
        } else {
            Intent(this, CreateOrEditActivity::class.java)
        }

        DiaryApp.currentAction?.note?.images = if (imagesUrl.isNotEmpty()) imagesUrl.joinToString(",") else null
        startActivity(intent)
        finish()

        super.onBackPressed()
    }
}