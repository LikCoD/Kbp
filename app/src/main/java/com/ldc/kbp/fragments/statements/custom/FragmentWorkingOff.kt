package com.ldc.kbp.fragments.statements.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.itextpdf.layout.element.Paragraph
import likco.studyum.R
import com.ldc.kbp.createDatePicker
import com.ldc.kbp.getString
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.statements.Statement
import com.ldc.kbp.shortSnackbar
import kotlinx.android.synthetic.main.fragment_statement_working_off.view.*
import org.threeten.bp.LocalDate

class FragmentWorkingOff : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_statement_working_off, container, false).apply {
        val subjects: ArrayList<String> = ArrayList()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_list_item_1, subjects
        )

        val datePicker = createDatePicker(context) {
            date_picker_date_tv.text = it.getString()
        }

        date_picker_date_tv.text = LocalDate.now().getString()

        show_date_picker_img.setOnClickListener { datePicker.show() }

        reason_auto.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.reasons)
            )
        )

        working_off_list.adapter = adapter

        subject_auto.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                Groups.timetable.filter { it.type == "subject" }.map { it.name }
            )
        )

        add_btn.setOnClickListener {
            if (date_picker_date_tv.text == "" || subject_auto.text.toString() == "") {
                shortSnackbar(save_btn, R.string.enter_data)
                return@setOnClickListener
            }
            subjects.add("\"${subject_auto.text}\": ${date_picker_date_tv.text};")
            adapter.notifyDataSetChanged()

            subject_auto.setText("")
        }


        save_btn.setOnClickListener {
            if (reason_auto.text.toString() == "") {
                shortSnackbar(save_btn, R.string.enter_data)
                return@setOnClickListener
            }
            val arr =
                mutableListOf(Paragraph("Прошу разрешить мне отработать практические занятия пропущенные по ${reason_auto.text} причине по следующим учебным дисциплинам:"))
            arr += subjects.map { Paragraph(it) }

            Statement.createStatement(requireActivity(), arr)
        }
    }

}