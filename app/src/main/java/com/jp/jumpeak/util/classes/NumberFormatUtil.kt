package com.jp.jumpeak.util.classes

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class NumberFormatUtil(val et: TextInputEditText) : TextWatcher {
    private var isFormatting = false
    override fun afterTextChanged(s: Editable?) {
        if(isFormatting) return
        val text = s.toString()
        if(text.isEmpty()) return
        isFormatting = true
        try {
            val cleanText = text.replace(",","")
            val parts = cleanText.split(".")
            val integerPart = parts[0].toInt()
            val formattedInteger = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US)).format(integerPart)
            val formatted = when{
                cleanText.endsWith(".") -> "$formattedInteger."
                parts.size == 2 -> "$formattedInteger.${parts[1].take(2)}"
                else -> formattedInteger
            }
            et.apply {
                setText(formatted)
                setSelection(formatted.length)
            }
        }catch (_: NumberFormatException){}
        isFormatting = false
    }
    override fun beforeTextChanged(p0: CharSequence?,p1: Int,p2: Int,p3: Int) {}
    override fun onTextChanged(p0: CharSequence?,p1: Int,p2: Int,p3: Int) {}
}