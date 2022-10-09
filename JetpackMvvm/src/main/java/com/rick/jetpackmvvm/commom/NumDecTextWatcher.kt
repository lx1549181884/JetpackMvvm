package com.rick.jetpackmvvm.commom

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class NumDecTextWatcher(private val editText: EditText, private val places: Int /*限制小数位数*/) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        s?.indexOfLast { it == '.' }?.apply {
            val maxLength = this + places + 1
            if (this != -1 && maxLength < s.length) {
                val selection =
                    editText.selectionStart.let { if (it > maxLength) maxLength else it }
                editText.setText(s.toString().substring(0, maxLength))
                editText.setSelection(selection)
            }
        }
    }
}