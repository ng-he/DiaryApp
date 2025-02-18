package com.example.diaryapp.ui.permission

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.diaryapp.ui.image.ImageActivity
import com.example.diaryapp.R
import com.example.diaryapp.common.READ_EXTERNAL_STORAGE_REQUEST_CODE
import com.example.diaryapp.common.readImagesPermission

class PermissionActivity : AppCompatActivity() {
    private lateinit var backBtn: ImageButton
    private lateinit var messageTextView: TextView
    private lateinit var continueButton: Button
    private lateinit var descriptionTextView: TextView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var permissionSwitch: Switch

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_permission)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backBtn = findViewById(R.id.backButton)
        messageTextView = findViewById(R.id.messageTextView)
        permissionSwitch = findViewById(R.id.permissionSwitch)
        continueButton = findViewById(R.id.continueButton)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        descriptionTextView.text = Html.fromHtml(getString(R.string.permission_message))

        permissionSwitch.isChecked = !readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        updatePermissionStatus(permissionSwitch.isChecked)

        backBtn.setOnClickListener {
            finish()
        }

        continueButton.setOnClickListener {
            if(intent.getStringExtra("fromActivity") != null) {
                finish()
            } else {
                startActivity(Intent(this, ImageActivity::class.java))
            }
        }

        permissionSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
//                if (!readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
//                    updatePermissionStatus(true) // Permission already granted, update UI
//                } else {
//                    if (readImagesPermission.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
//                        showPermissionRationale() // Chỉ hiển thị dialog nếu đã từng từ chối quyền
//                    } else {
//                        ActivityCompat.requestPermissions(this, readImagesPermission, READ_EXTERNAL_STORAGE_REQUEST_CODE)
//                    }
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:" + applicationContext.packageName)
                        startActivity(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        startActivity(intent)
                    }
                }

                ActivityCompat.requestPermissions(this, readImagesPermission, READ_EXTERNAL_STORAGE_REQUEST_CODE)
            } else {
                updatePermissionStatus(false) // User turned switch off, update UI
//                if(!readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
//                    showRevokeGrantPermissionSettingsDialog()
//                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updatePermissionStatus(true)
            } else {
//                if (readImagesPermission.any { !ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
//                    showPermissionSettingsDialog()
//                } else {
//                    updatePermissionStatus(false)
//                }
                updatePermissionStatus(false)
            }
        }
    }

    private fun updatePermissionStatus(granted: Boolean) {
//        permissionSwitch.setOnCheckedChangeListener(null)

        if (granted) {
            permissionSwitch.isChecked = true
            permissionSwitch.trackDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.switch_on_color), PorterDuff.Mode.SRC_IN)
            messageTextView.text = getString(R.string.permission_allowed)
            messageTextView.setTextColor(getColor(R.color.success_color))
            continueButton.visibility = View.VISIBLE
        } else {
            permissionSwitch.isChecked = false
            permissionSwitch.trackDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.switch_off_color), PorterDuff.Mode.SRC_IN)
            messageTextView.text = getString(R.string.permission_not_allowed)
            messageTextView.setTextColor(getColor(R.color.error_color))
            continueButton.visibility = View.GONE
        }

//        permissionSwitch.setOnCheckedChangeListener { _, checked ->
//            if (checked) {
//                ActivityCompat.requestPermissions(this, readImagesPermission, READ_EXTERNAL_STORAGE_REQUEST_CODE)
//            } else {
//                showRevokeGrantPermissionSettingsDialog()
//            }
//        }
    }

//    private fun showPermissionSettingsDialog() {
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.permission))
//            .setMessage(Html.fromHtml(getString(R.string.permission_message)))
//            .setPositiveButton("Settings") { _, _ ->
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            }
//            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
//                permissionSwitch.isChecked = false
//            }
//            .show()
//    }

//    private fun showRevokeGrantPermissionSettingsDialog() {
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.permission))
//            .setMessage("To revoke permission, please go to settings")
//            .setPositiveButton("Settings") { _, _ ->
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            }
//            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
//                permissionSwitch.isChecked = true
//            }
//            .show()
//    }

//    private fun showPermissionRationale() {
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.permission))
//            .setMessage(Html.fromHtml(getString(R.string.permission_message)))
//            .setPositiveButton("OK") { _, _ ->
//                ActivityCompat.requestPermissions(this, readImagesPermission, READ_EXTERNAL_STORAGE_REQUEST_CODE)
//            }
//            .setNegativeButton(getString(R.string.cancel)){ _, _ ->
//                permissionSwitch.isChecked = false
//            }
//            .show()
//    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            if (Environment.isExternalStorageManager()) {
                Log.d("Permission", "MANAGE_EXTERNAL_STORAGE granted")
            } else {
                Log.e("Permission", "MANAGE_EXTERNAL_STORAGE denied")
            }
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            val isGranted = !readImagesPermission.any {
//                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//            }
//            updatePermissionStatus(isGranted) // Update UI sau khi đã chắc chắn trạng thái được cập nhật
//        }, 200) // Trì hoãn 200ms
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
//            val isGranted = !readImagesPermission.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
//            updatePermissionStatus(isGranted)
//        }
//    }
}