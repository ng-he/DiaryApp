package com.example.diaryapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.diaryapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
    arrayOf(Manifest.permission.READ_MEDIA_IMAGES/*, Manifest.permission.MANAGE_EXTERNAL_STORAGE*/)
} else {
    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}