package com.ldc.kbp.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R

class NoScrollableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : PinnedScrollView(context, attrs, defStyle) {
    private var attrsArray =
        context.obtainStyledAttributes(attrs, R.styleable.NoScrollableRecyclerView)

    private val layoutOrientation =
        attrsArray.getInt(R.styleable.NoScrollableRecyclerView_layoutOrientation, 0)
    private val recyclerOrientation =
        attrsArray.getInt(R.styleable.NoScrollableRecyclerView_recyclerOrientation, 0)
    private val reverseLayout =
        attrsArray.getBoolean(R.styleable.NoScrollableRecyclerView_reverseLayout, false)

    val recyclerView: RecyclerView = RecyclerView(context)
    private val recyclerLayout: LinearLayout = LinearLayout(context).apply {
        orientation = layoutOrientation

        addView(recyclerView)
    }

    init {
        addView(recyclerLayout)
    }

    fun setup(
        adapter: RecyclerView.Adapter<*>,
        manager: RecyclerView.LayoutManager = getManager()
    ) = setUp(adapter, manager)

    fun setup(
        adapter: RecyclerView.Adapter<*>,
        cols: Int,
        manager: RecyclerView.LayoutManager = getManager(cols)
    ) = setUp(adapter, manager)

    private fun setUp(adapter: RecyclerView.Adapter<*>, manager: RecyclerView.LayoutManager){
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
    }

    private fun getManager() = LinearLayoutManager(context, recyclerOrientation, reverseLayout)

    private fun getManager(cols: Int) = GridLayoutManager(context, cols, recyclerOrientation, reverseLayout)

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        recyclerLayout.addView(child, params)
    }
}