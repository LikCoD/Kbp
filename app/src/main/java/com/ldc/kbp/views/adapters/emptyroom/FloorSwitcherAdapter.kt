package com.ldc.kbp.views.adapters.emptyroom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import kotlinx.android.synthetic.main.item_floor_switcher.view.*

class FloorSwitcherAdapter(
    private val context: Context,
    private val amount: Int,
) : RecyclerView.Adapter<FloorSwitcherAdapter.ViewHolder>() {

    val switchers = mutableListOf<Switch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_floor_switcher, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = "Этаж $position"
        holder.name.text = name

        switchers.add(holder.switch)
    }

    override fun getItemCount() = amount

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val switch: Switch = itemView.item_floor_switcher
        val name: TextView = itemView.item_floor_switcher_name
    }
}
