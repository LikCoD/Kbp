package com.ldc.kbp.fragments.statements.custom

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.ldc.kbp.*
import com.ldc.kbp.models.statements.Statement
import kotlinx.android.synthetic.main.fragment_statement_working_off.view.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class FragmentWorkingOff : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_statement_working_off, container, false)) {
            val subjects: ArrayList<String> = ArrayList()
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_list_item_1, subjects
            )

            val datePicker = createDatePicker(context) {
                date_picker_date_tv.text = it.toLocalDate().getString()
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

            add_btn.setOnClickListener {
                if (date_picker_date_tv.text != "" && subject_et.text.toString() != "") {
                    subjects.add("\"${subject_et.text}\": ${date_picker_date_tv.text};")
                    adapter.notifyDataSetChanged()

                    subject_et.setText("")
                } else shortSnackbar(save_btn, R.string.enter_data)
            }


            save_btn.setOnClickListener {
                val arr = mutableListOf(Paragraph("Прошу разрешить мне отработать практические занятия пропущенные по ${reason_auto.text} причине по следующим учебным дисциплинам:"),)
                arr += subjects.map { Paragraph(it) }

                Statement.createStatement(requireActivity(), arr)
            }

            return this
        }
    }

}