package com.example.diaryapp.common

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveLanguage(languageCode: String) {
        prefs.edit().putString("language", languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language", "") ?: ""
    }
}