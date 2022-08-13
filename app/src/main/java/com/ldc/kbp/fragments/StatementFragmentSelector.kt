package com.ldc.kbp.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import likco.studyum.R
import com.ldc.kbp.checkPermission
import com.ldc.kbp.fragments.statements.StatementDataFragment
import com.ldc.kbp.models.statements.Statement
import com.ldc.kbp.models.statements.StatementsType
import kotlinx.android.synthetic.main.fragment_statement_selector.view.*


class StatementFragmentSelector : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_statement_selector, container, false).apply {
        item_list.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.statements)
        )

        item_list.setOnItemClickListener { _, _, _, id ->
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requireActivity()) {
                item_list.isEnabled = false

                Statement.type = StatementsType.values()[id.toInt()]

                requireActivity().supportFragmentManager.beginTransaction().let {
                    /*it.replace(R.id.nav_host_fragment, StatementDataFragment())
                    it.commit()*/
                }
            }
        }
    }

}