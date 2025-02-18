package com.example.diaryapp.common

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.diaryapp.R
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

fun showErrorAlert(context: Context, message: String) {
    AlertDialog.Builder(context)
        .setTitle("System error: ")
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun setAppLocale(languageCode: String, resources: Resources) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    appDateFormat = SimpleDateFormat("EEE, MMM d, yyyy", locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}