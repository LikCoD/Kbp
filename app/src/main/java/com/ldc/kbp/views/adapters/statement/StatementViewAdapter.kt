package com.ldc.kbp.views.adapters.statement

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.RequiresApi
import com.itextpdf.layout.element.Paragraph
import com.ldc.kbp.R
import com.ldc.kbp.createDatePicker
import com.ldc.kbp.getString
import com.ldc.kbp.models.statements.StatementViewInfo
import com.ldc.kbp.models.statements.Views
import com.ldc.kbp.toLocalDate
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_statement_view.view.*

@RequiresApi(Build.VERSION_CODES.O)
class StatementViewAdapter(
    context: Context,
    items: List<StatementViewInfo>? = null
) : Adapter<StatementViewInfo>(context, items, R.layout.item_statement_view) {

    private var viewsText: MutableList<() -> String?> = mutableListOf()

    override fun onBindViewHolder(view: View, item: StatementViewInfo?, position: Int) {
        viewsText.add { item!!.view.text }

        view.item_statement_view_description.text = item!!.view.text

        when (item.view) {
            Views.DATE -> {
                val datePickerPopup = createDatePicker(context) {
                    view.item_statement_view_description.text = it.toLocalDate().getString()
                    viewsText[position] = { it.toLocalDate().getString() }
                }
                view.item_statement_view.addView(ImageView(context).apply {
                    setImageResource(R.drawable.ic_calendar)
                    setOnClickListener {
                        datePickerPopup.show()
                    }
                })

            }
            Views.TEXT -> view.item_statement_view.addView(EditText(context).apply {
                layoutParams = ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT)
                viewsText[position] = { text.toString().trim() }
            })
            Views.SPINNER -> view.item_statement_view.addView(Spinner(context).apply {
                adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.d_types)
                )
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                viewsText[position] = { selectedItem.toString() }
            })
            Views.NONE -> {
                viewsText[position] = { "" }
            }
        }

        view.item_statement_view_text.text = item.description
    }

    fun getStatementMain(): List<Paragraph>? {
        if (viewsText.any { it() == null }) return null

        val paragraphs: MutableList<Paragraph> = mutableListOf(Paragraph())

        items?.forEachIndexed { index, statementView ->
            if (statementView!!.nextParagraph) paragraphs.add(Paragraph())

            paragraphs.last().add(
                "${statementView.statementTextBefore} ${viewsText[index]()} ${statementView.statementTextAfter} "
            )
        }

        return paragraphs
    }
}