package com.ldc.kbp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.R
import com.ldc.kbp.WebController
import com.ldc.kbp.config
import com.ldc.kbp.disableActions
import com.ldc.kbp.models.Journal
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.views.adapters.journal.*
import kotlinx.android.synthetic.main.fragment_journal.view.*

class JournalFragment : Fragment() {

    private lateinit var root: View

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_journal, container, false)) {
            root = this
            val webController = WebController(
                requireContext(),
                "https://nehai.by/ej/index.php?logout",
                true
            )


            val bottomSheetBehavior = BottomSheetBehavior.from(journal_bottom_sheet)

            test_web.addView(webController.webView)

            webController.onLoad = { url, html ->
                if (config.isStudent) webController.setup = null

                when {
                    url == "https://nehai.by/ej/parent_journal.php" && config.isStudent -> {
                        test_web.removeView(webController.webView)

                        val journal = Journal.parseJournal(html)
                        update(journal)
                    }
                    url == "https://nehai.by/ej/teather_journal.php" && !config.isStudent -> {
                        val selector = JournalTeacherSelector.parseTeacherSelector(html)
                        updateSelector(selector, webController)

                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                        webController.setup = null
                        webController.loadRes = true
                        webController.onLoadRes = {
                            bottomSheetBehavior.isHideable = true
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                            test_web.removeView(webController.webView)

                            val journal = Journal.parseTeacherJournal(it)
                            update(journal)
                        }
                    }
                }
            }


            webController.setup = {
                if (config.isStudent) {
                    val surname = config.surname.substringBefore(" ")

                    evaluateJavascript("document.getElementById('student_name').value = '$surname';") {}
                    evaluateJavascript("document.getElementById('group_id').value = '${config.groupId}';") {}
                    evaluateJavascript("document.getElementById('birth_day').value = '${config.password}';") {}

                    evaluateJavascript("check_login()") {}
                } else {
                    webController.setup = {
                        evaluateJavascript("document.getElementById('login').value = '${config.groupId}';") {}
                        evaluateJavascript("document.getElementById('password').value = '${config.password}';") {}

                        evaluateJavascript("check_login()") {}
                    }
                    webController.link = "https://nehai.by/ej/t.php"
                    webController.load()
                }
            }
            webController.load()

            journal_subjects_scroll.setOnScrollChangeListener { _, x, y, _, _ ->
                journal_date_scroll.scrollX = x
                journal_groups_name_scroll.scrollY = y
                journal_average_scroll.scrollY = y
            }

            journal_average_img.setOnClickListener {
                journal_average_scroll.isVisible = !journal_average_scroll.isVisible
            }

            journal_date_scroll.disableActions()
            journal_groups_name_scroll.disableActions()
            journal_average_scroll.disableActions()

            return this
        }
    }


    private fun update(journal: Journal) {
        JournalSubjectsNameAdapter(requireContext(), journal.months[0].subjects.map { it.name }, root.journal_subjects_recycler)
        JournalDateAdapter(requireContext(), journal.months, root.journal_date_recycler)
        JournalAverageAdapter(requireContext(), journal.months.flatMap { it.subjects }, root.journal_average_recycler)
        root.journal_marks_recycler.adapter = JournalMarksAdapter(requireContext(), journal)
    }

    private fun updateSelector(selector: JournalTeacherSelector, webController: WebController){
        JournalSubjectsNameAdapter(requireContext(), selector.groups.keys.map { it.name }, root.journal_groups_recycler)

        val selectorAdapter = JournalSubjectsAdapter(requireContext(), selector)

        selectorAdapter.onClick = { index, pos ->
            webController.webView.evaluateJavascript("document.getElementsByTagName('ul')[${index + 2}].getElementsByTagName('li')[$pos].click()"){}
        }

        root.journal_subjects_selector_recycler.adapter = selectorAdapter
    }
}
