package com.example.diaryapp

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.diaryapp.data.entity.Note
import java.text.SimpleDateFormat
import java.util.Locale

fun showUncompletedFunctionAlert(context: Context, functionName: String) {
    val builder = AlertDialog.Builder(context).setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.setMessage("Chức năng \"$functionName\" vẫn đang được \"nấu\" – hãy kiên nhẫn một chút, nó sẽ sớm 'chín' thôi")
    dialog.show()
}

fun toBundle(note: Note) : Bundle {
    val bundle = Bundle().apply {
        putString("title", note.title)
        putString("content", note.content)
        putLong("date", note.date.time)
        putInt("feelings", note.feeling)
        putString("images", note.images)
    }

    return bundle
}

fun setAppLocale(languageCode: String, resources: Resources) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    appDateFormat = SimpleDateFormat("EEE, MMM d, yyyy", locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun Context.setLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}