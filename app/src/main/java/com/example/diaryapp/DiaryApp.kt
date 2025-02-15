package com.example.diaryapp

import android.app.Application
import com.example.diaryapp.utils.SharedPrefsManager
import com.example.diaryapp.utils.setAppLocale

class DiaryApp : Application() {
    companion object {
        var language: String = ""
        var currentAction: AppAction? = null
        var deviceDensity: Float = 0.0F
        lateinit var instance: DiaryApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        currentAction = null
        deviceDensity = resources.displayMetrics.density
        language = SharedPrefsManager(this).getLanguage()
    }
}