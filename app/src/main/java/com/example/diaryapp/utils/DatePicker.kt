package com.example.diaryapp.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.diaryapp.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date

class DatePicker(
    context: Context,
    private val fragmentManager: FragmentManager,
    private val onPositiveButtonClickListener: (Long) -> Unit,
    defaultSelect: Long = Date().time
) {
    private var datePicker: MaterialDatePicker<Long>

    init {
        val constraintsBuilder = CalendarConstraints.Builder()

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(context.getString(R.string.select_date))
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(defaultSelect) // Default to today
            .setTheme(R.style.CustomDatePickerTheme)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            onPositiveButtonClickListener(selection)
        }
    }

    fun show() {
        datePicker.show(fragmentManager, "DATE_PICKER")
    }
}