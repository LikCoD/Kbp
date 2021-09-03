package com.ldc.kbp.views.adapters.groupselector

import android.content.Context
import android.view.View
import com.ldc.kbp.models.Groups
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_group_selector.view.*

class GroupSelectionAdapter(context: Context, items: Set<Groups.Timetable>? = null) :
    Adapter<Groups.Timetable>(context, items?.toList(), R.layout.item_group_selector) {

    override fun onBindViewHolder(view: View, item: Groups.Timetable?, position: Int) {
        view.item_selector_index.text = (position + 1).toString()
        view.item_selector_category.text = Groups.categories.toList()[item!!.categoryIndex]
        view.item_selector_group.text = item.group
        view.item_selector_link.text = item.link

        view.item_selector_line.setOnClickListener {
            onItemClickListener(position, item)
        }
    }
}