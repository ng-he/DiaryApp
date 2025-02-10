package com.example.diaryapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Locale
import android.Manifest
import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.text.DateFormat

class DiaryApp : Application() {
    companion object {
        lateinit var instance: DiaryApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val sharedPrefsManager = SharedPrefsManager(this)
        val languageCode = sharedPrefsManager.getLanguage()
        setLocale(languageCode)
        setAppLocale(languageCode, this.resources)
    }
}

class SharedPrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveLanguage(languageCode: String) {
        prefs.edit().putString("language", languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language", "en") ?: "en"
    }
}

data class Language(
    @StringRes var displayName: Int,
    val code: String,
    @DrawableRes var icon: Int
)

val languages = listOf(
    Language(R.string.english, "en", R.drawable.ic_english),
    Language(R.string.hindi, "hi", R.drawable.ic_hindi),
    Language(R.string.spanish, "es", R.drawable.ic_spanish),
    Language(R.string.french, "fr", R.drawable.ic_france),
    Language(R.string.arabic, "ar", R.drawable.arabic),
    Language(R.string.bengali, "bn", R.drawable.ic_bengali),
)

@SuppressLint("ConstantLocale")
var appDateFormat: DateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

val feelings = listOf(
    R.drawable.ic_feeling_1,
    R.drawable.ic_feeling_2,
    R.drawable.ic_feeling_3,
    R.drawable.ic_feeling_4,
    R.drawable.ic_feeling_5,
    R.drawable.ic_feeling_6,
)

const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101

val readImagesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
} else {
    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}