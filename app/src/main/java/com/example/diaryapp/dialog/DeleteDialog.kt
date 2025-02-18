package com.example.diaryapp.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.diaryapp.R
import com.example.diaryapp.common.DiaryApp

@SuppressLint("InflateParams")
class DeleteDialog(context: Context) : AlertDialog(context, R.style.CustomDialogTheme) {
    private var cancelButton: Button
    private var deleteButton: Button

    init {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)

        setView(dialogView)
        create()

        cancelButton = dialogView.findViewById(R.id.cancelButton)
        deleteButton = dialogView.findViewById(R.id.deleteConfirmButton)
    }

    fun setOnCancelClickListener(l: View.OnClickListener) {
        cancelButton.setOnClickListener(l)
    }

    fun setOnDeleteClickListener(l: View.OnClickListener) {
        deleteButton.setOnClickListener(l)
    }

    override fun show() {
        super.show()
        window?.setLayout(
            (300 * DiaryApp.deviceDensity).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}