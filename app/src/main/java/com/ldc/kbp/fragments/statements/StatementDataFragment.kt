package com.ldc.kbp.fragments.statements

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ldc.kbp.*
import com.ldc.kbp.models.statements.Statement
import com.ldc.kbp.models.statements.StatementsType
import kotlinx.android.synthetic.main.fragment_statement_data.view.*
import org.threeten.bp.LocalDate

class StatementDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_statement_data, container, false)) {
            val datePicker = createDatePicker(context) {
                date_picker_date_tv.text = it.getString()
            }

            date_picker_date_tv.text = LocalDate.now().getString()

            show_date_picker_img.setOnClickListener { datePicker.show() }

            name_et.setText(config.surname)

            adder_lt.isVisible =
                Statement.type == StatementsType.RECOVERY || Statement.type == StatementsType.ACADEMIC

            if (Statement.type == StatementsType.RECOVERY || Statement.type == StatementsType.ACADEMIC || Statement.type == StatementsType.RECOVERY_ACADEMIC)
                group_lt.isVisible = false else group_et.setText(config.group)

            department_et.setText(config.department)

            continue_btn.setOnClickListener {
                if (date_picker_date_tv.text != "" && name_et.text.toString() != "") {
                    Statement.date = date_picker_date_tv.text.toString()
                    Statement.group = group_et.text.toString()
                    Statement.name = name_et.text.toString()
                    Statement.department = department_et.text.toString()

                    if (full_name_et.text.toString() != "" && live_address_et.text.toString() != ""
                        && reg_address_et.text.toString() != "" && phone_et.text.toString() != ""
                    ) {
                        Statement.fullName = full_name_et.text.toString()
                        Statement.liveAddress = live_address_et.text.toString()
                        Statement.regAddress = reg_address_et.text.toString()
                        Statement.phone = phone_et.text.toString()
                    }

                    requireActivity().supportFragmentManager.beginTransaction().let {
                        it.replace(
                            R.id.nav_host_fragment,
                            Statement.type.fragment ?: CustomStatementFragment(Statement.type)
                        )
                        it.commit()
                    }
                } else shortSnackbar(continue_btn, R.string.enter_data)
            }
            return this
        }
    }

}