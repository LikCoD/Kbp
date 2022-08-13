package com.ldc.kbp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likco.studyum.R
import com.ldc.kbp.disableActions
import com.ldc.kbp.views.layoutmanagers.FixedGridLayoutManager

class GridRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : ScrollView(context, attrs, defStyle) {
    var horizontalScrollChangeListener: (Int) -> Unit = {}
    var verticalScrollChangeListener: (Int) -> Unit = {}

    private var attrsArray =
        context.obtainStyledAttributes(attrs, R.styleable.GridRecyclerView)

    private val recyclerOrientation =
        attrsArray.getInt(R.styleable.GridRecyclerView_orientation, 0)
    private val reverseLayout =
        attrsArray.getBoolean(R.styleable.GridRecyclerView_reverse, false)
    var spanCount =
        attrsArray.getInt(R.styleable.GridRecyclerView_spanCount, 1)
    set(value) {
        (recyclerView.layoutManager as GridLayoutManager).spanCount = value
        field = value
    }
    private val spaceAfter =
        attrsArray.getDimension(R.styleable.GridRecyclerView_spaceAfter, 0F)

    val recyclerView: RecyclerView = RecyclerView(context)

    val layout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        addView(recyclerView)
        addView(Space(context).apply {
            layoutParams = LayoutParams(spaceAfter.toInt(), spaceAfter.toInt())
        })
    }

    private var scrollXRecycler = 0

    override fun scrollBy(x: Int, y: Int) {
        verticalScrollChangeListener(scrollY)
        scrollContainers.forEach {
            if (it.orientation == LinearLayout.VERTICAL)
                it.scrollContainer.scrollBy(0, y)
        }
        super.scrollBy(x, y)
    }

    fun scrollToX(x: Int) {
        recyclerView.scrollBy(x - scrollXRecycler, 0)
    }

    var scrollContainers = listOf<PinnedScrollView.Container>()
        set(value) {
            value.forEach { if (!it.enableScroll) it.scrollContainer.disableActions() }
            field = value
        }

    init {
        recyclerView.setOnScrollChangeListener { _, _, _, oldScrollX, _ ->
            scrollXRecycler -= oldScrollX
            horizontalScrollChangeListener(scrollXRecycler)
            scrollContainers.forEach {
                if (it.orientation == LinearLayout.HORIZONTAL)
                    it.scrollContainer.scrollBy(-oldScrollX, 0)
            }
        }
        recyclerView.layoutManager =
            FixedGridLayoutManager(context, spanCount, recyclerOrientation, reverseLayout)
        addView(layout)
    }

}