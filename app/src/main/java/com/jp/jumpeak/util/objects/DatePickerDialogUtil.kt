package com.jp.jumpeak.util.objects

import com.google.android.material.datepicker.MaterialDatePicker

object DatePickerDialogUtil {
    fun setup(onDateSelected:(Long) -> Unit):MaterialDatePicker<Long> {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(System.currentTimeMillis())
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
        datePicker.addOnPositiveButtonClickListener { dateMills ->
            onDateSelected(dateMills)
        }
        return datePicker
    }
}