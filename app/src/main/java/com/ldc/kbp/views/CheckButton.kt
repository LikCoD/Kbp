package com.ldc.kbp.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView

class CheckButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    @DrawableRes var selected: Int? = null,
    @DrawableRes var noSelected: Int? = null
) : AppCompatTextView(context, attrs, defStyle) {
    override fun setSelected(selected: Boolean) {
        onSelectionChanged(isSelected)
        super.setSelected(selected)
    }

    override fun performClick(): Boolean {
        isSelected = !isSelected
        return super.performClick()
    }

    var onSelectionChanged: (Boolean) -> Unit = {}

    init {
        setOnClickListener {  }
    }
}