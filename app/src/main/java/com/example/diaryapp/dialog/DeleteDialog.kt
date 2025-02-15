package com.example.diaryapp.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.diaryapp.R
import com.example.diaryapp.DiaryApp

class DeleteDialog(
    context: Context,
    builder: AlertDialog.Builder,
    private val onCancel: (AlertDialog) -> Unit = {},
    private val onDelete: (AlertDialog) -> Unit = {}
) {
    private var dialog: AlertDialog

    init {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)

        builder.setView(dialogView)
        dialog = builder.create()
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        val deleteButton: Button = dialogView.findViewById(R.id.deleteConfirmButton)

        cancelButton.setOnClickListener { onCancel(dialog) }
        deleteButton.setOnClickListener { onDelete(dialog) }
    }

    fun show() {
        dialog.show()
        dialog.window?.setLayout(
            (300 * DiaryApp.deviceDensity).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}