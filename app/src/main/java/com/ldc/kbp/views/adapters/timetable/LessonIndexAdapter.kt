package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import com.ldc.kbp.models.Deprecates
import kotlinx.android.synthetic.main.item_bell.view.*
import org.threeten.bp.LocalDate

class LessonIndexAdapter(
    private val context: Context,
    private val itemsCount: Int,
    private val spaceAfter: Int
) : RecyclerView.Adapter<LessonIndexAdapter.ViewHolder>() {

    private val bellsLayout = mutableListOf<View>()
    private var isBellShown = false

    fun updateBells() {
        isBellShown = !isBellShown

        bellsLayout.forEach { it.isVisible = isBellShown }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 1){
            return ViewHolder(Space(context).apply { layoutParams = ViewGroup.LayoutParams(-1, spaceAfter) })
        }
        val view = LayoutInflater.from(context).inflate(R.layout.item_bell, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemsCount) 0 else 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemView is Space){
            return
        }
        fun TextView.highlight() = Deprecates.setTextAppearance(this, R.style.head_text)

        holder.itemView.item_bell_index_tv.text = (position + 1).toString()

        holder.itemView.item_bell_workdays_time_tv.text = context.resources.getStringArray(R.array.bell_workdays)[position]
        holder.itemView.item_bell_weekends_time_tv.text = context.resources.getStringArray(R.array.bell_saturday)[position]

        when (LocalDate.now().dayOfWeek.ordinal) {
            in 0..4 -> {
                holder.itemView.item_bell_workdays_time_tv.highlight()
                holder.itemView.item_bell_workdays.highlight()
            }
            5 -> {
                holder.itemView.item_bell_weekends_time_tv.highlight()
                holder.itemView.item_bell_weekends.highlight()
            }
        }

        bellsLayout.add(holder.itemView.item_bell_layout)
    }

    override fun getItemCount(): Int = itemsCount + 1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}