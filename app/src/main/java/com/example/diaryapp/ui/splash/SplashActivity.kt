package com.example.diaryapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.diaryapp.ui.home.HomeActivity
import com.example.diaryapp.ui.language.LanguageActivity
import com.example.diaryapp.R
import com.example.diaryapp.utils.SharedPrefsManager
import com.example.diaryapp.utils.setAppLocale

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val selectedLanguage = SharedPrefsManager(this).getLanguage()
        val intent = if (selectedLanguage == "") {
            Intent(this, LanguageActivity::class.java)
        } else {
            setAppLocale(selectedLanguage, this.resources)
            Intent(this, HomeActivity::class.java)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }
}