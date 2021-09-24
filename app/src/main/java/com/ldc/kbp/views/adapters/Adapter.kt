package com.ldc.kbp.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class Adapter<T>(val context: Context, items: List<T?>?, val layout: Int) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    var items = items
        set(value) {
            if (field != null && value != null)
                if (field!!.size == value.size)
                    field!!.forEachIndexed { index, item ->
                        if (item != value[index]) notifyItemChanged(index)
                    }
                else dataSetChanged()
            else if (field == null && value != null)
                notifyItemRangeChanged(0, value.size - 1)
            else if (value == null && field != null)
                dataSetChanged()

            field = value
        }

    @SuppressLint("NotifyDataSetChanged")
    fun dataSetChanged() =
        notifyDataSetChanged()

    var onItemClickListener: ((Int, T) -> Unit) = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(layout, parent, false))

    override fun getItemCount(): Int = items?.size ?: 0

    abstract fun onBindViewHolder(view: View, item: T?, position: Int)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        onBindViewHolder(holder.itemView, items!![position], position)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}