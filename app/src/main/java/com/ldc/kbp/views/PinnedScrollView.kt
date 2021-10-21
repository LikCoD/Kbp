package com.ldc.kbp.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.jrummyapps.android.widget.TwoDScrollView
import com.ldc.kbp.disableActions

open class PinnedScrollView @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TwoDScrollView(context, attrs, defStyle) {

    data class Container(val scrollContainer: View, val orientation: Int, val enableScroll: Boolean)

    var containers: List<Container>? = null
        set(value) {
            value?.forEach {
                if (!it.enableScroll) it.scrollContainer.disableActions()
            }
            field = value
        }

    override fun scrollTo(x: Int, y: Int) {
        containers?.forEach {
            when (it.orientation) {
                LinearLayout.HORIZONTAL -> it.scrollContainer.scrollX = x
                LinearLayout.VERTICAL -> it.scrollContainer.scrollY = y
                else -> it.scrollContainer.scrollTo(x, y)
            }
        }

        super.scrollTo(x, y)
    }
}