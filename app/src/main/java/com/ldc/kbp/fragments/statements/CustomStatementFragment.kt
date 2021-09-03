package com.ldc.kbp.fragments.statements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ldc.kbp.R
import com.ldc.kbp.shortSnackbar
import com.ldc.kbp.models.statements.Statement
import com.ldc.kbp.models.statements.StatementsType
import com.ldc.kbp.views.adapters.statement.StatementViewAdapter
import kotlinx.android.synthetic.main.fragment_statement.view.*

class CustomStatementFragment(private val statementType: StatementsType) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_statement, container, false)) {
            statement_name_tv.text = statementType.statementName

            val statementViewAdapter =
                StatementViewAdapter(requireContext(), statementType.statementViewInfo)
            statement_view_recycler.adapter = statementViewAdapter

            save_btn.setOnClickListener {
                val statementMain = statementViewAdapter.getStatementMain()

                if (statementMain != null)
                    Statement.createStatement(requireActivity(), statementMain)
                else shortSnackbar(save_btn, R.string.enter_data)
            }

            return this
        }
    }

}