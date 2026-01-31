package com.jp.jumpeak.util.classes

import android.icu.util.Calendar
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class DateTimePickerDialogUtil(val fragmentManager: FragmentManager,val onDateTimeSelected:(Long) -> Unit) {
    fun show() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(System.currentTimeMillis())
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
        datePicker.show(fragmentManager,"Date Picker")
        datePicker.addOnPositiveButtonClickListener { dateMills ->
            showTimePicker(dateMills)
        }
    }

    private fun showTimePicker(dateMills: Long){
        val calendar = Calendar.getInstance()
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("Select Time")
            .build()
        timePicker.show(fragmentManager,"Time Picker")
        timePicker.addOnPositiveButtonClickListener {
            calendar.apply {
                timeInMillis = dateMills
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onDateTimeSelected(calendar.timeInMillis)
        }
    }
}