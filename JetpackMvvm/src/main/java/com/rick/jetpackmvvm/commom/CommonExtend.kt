package com.rick.jetpackmvvm.commom

import android.text.TextWatcher
import android.widget.TextView
import com.blankj.utilcode.util.ReflectUtils

object CommonExtend {
    fun TextView.getTextWatchers(): MutableList<TextWatcher>? =
        (ReflectUtils.reflect(this).field("mListeners")
            .get() as ArrayList<TextWatcher?>?)?.filterNotNull()?.toMutableList()
}