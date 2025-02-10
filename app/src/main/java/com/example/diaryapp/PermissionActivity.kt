package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility

class PermissionActivity : AppCompatActivity() {
    private lateinit var backBtn: ImageButton
    private lateinit var messageTextView: TextView
    private lateinit var continueButton: Button
    private lateinit var descriptionTextView: TextView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var permissionSwitch: Switch

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

        backBtn.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtras(this.intent.extras!!)
            startActivity(intent)
        }

        continueButton.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtras(this.intent.extras!!)
            startActivity(intent)
        }

        permissionSwitch.setOnCheckedChangeListener { _, checked ->
            if(checked) {
                ActivityCompat.requestPermissions(this, readImagesPermission, READ_EXTERNAL_STORAGE_REQUEST_CODE)
            } else {
                permissionSwitch.trackDrawable.setColorFilter(ContextCompat.getColor(this, R.color.switch_off_color), PorterDuff.Mode.SRC_IN)
                messageTextView.text = getString(R.string.permission_not_allowed)
                messageTextView.setTextColor(getColor(R.color.error_color))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionSwitch.trackDrawable.setColorFilter(ContextCompat.getColor(this, R.color.switch_on_color), PorterDuff.Mode.SRC_IN)
                messageTextView.text = getString(R.string.permission_allowed)
                messageTextView.setTextColor(getColor(R.color.success_color))
                continueButton.visibility = View.VISIBLE
            } else {
                permissionSwitch.isChecked = false
                permissionSwitch.trackDrawable.setColorFilter(ContextCompat.getColor(this, R.color.switch_off_color), PorterDuff.Mode.SRC_IN)
                messageTextView.text = getString(R.string.permission_not_allowed)
                messageTextView.setTextColor(getColor(R.color.error_color))
            }
        }
    }
}