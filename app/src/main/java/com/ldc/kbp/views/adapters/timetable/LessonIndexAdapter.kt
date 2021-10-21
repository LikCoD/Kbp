package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
) : RecyclerView.Adapter<LessonIndexAdapter.ViewHolder>() {

    private val bellsLayout = mutableListOf<View>()
    private var isBellShown = false

    fun updateBells() {
        isBellShown = !isBellShown

        bellsLayout.forEach { it.isVisible = isBellShown }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fun TextView.highlight() = Deprecates.setTextAppearance(this, R.style.head_text)

        holder.indexTv.text = (position + 1).toString()

        holder.workdaysTime.text = context.resources.getStringArray(R.array.bell_workdays)[position]
        holder.weekendsTime.text = context.resources.getStringArray(R.array.bell_saturday)[position]

        when (LocalDate.now().dayOfWeek.ordinal) {
            in 0..4 -> {
                holder.workdaysTime.highlight()
                holder.workdaysType.highlight()
            }
            5 -> {
                holder.weekendsTime.highlight()
                holder.weekendsType.highlight()
            }
        }

        bellsLayout.add(holder.bellsLayout)
    }

    override fun getItemCount(): Int = itemsCount

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val indexTv: TextView = itemView.item_bell_index_tv

        val workdaysTime: TextView = itemView.item_bell_workdays_time_tv
        val weekendsTime: TextView = itemView.item_bell_weekends_time_tv
        val workdaysType: TextView = itemView.item_bell_workdays
        val weekendsType: TextView = itemView.item_bell_weekends

        val bellsLayout: ConstraintLayout = itemView.item_bell_layout
    }
}