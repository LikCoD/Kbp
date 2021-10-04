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
import org.jsoup.Jsoup

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

            main_layout.addView(webController.webView.apply { isVisible = false })

            webController.onLoad = { url, html ->
                if (config.isStudent) webController.setup = null

                when {
                    url == "https://nehai.by/ej/parent_journal.php" && config.isStudent -> {
                        update(html)
                    }
                    url == "https://nehai.by/ej/teather_journal.php" && !config.isStudent -> {
                        val selector = JournalTeacherSelector.parseTeacherSelector(html)
                        updateSelector(selector, webController, bottomSheetBehavior)

                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                        webController.setup = null
                        webController.loadRes = true
                        webController.onLoadRes = {

                            journal_group_selector_layout.isVisible = false

                            update(it, false, webController, bottomSheetBehavior)
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

            journal_browser_img.setOnClickListener {
                webController.webView.isVisible = true
            }

            journal_date_scroll.disableActions()
            journal_groups_name_scroll.disableActions()
            journal_average_scroll.disableActions()

            return this
        }
    }


    private fun update(
        html: String,
        isStudent: Boolean = true,
        webController: WebController? = null,
        behavior: BottomSheetBehavior<*>? = null
    ) {
        val journal = if (isStudent) Journal.parseJournal(html) else Journal.parseTeacherJournal(html)

        JournalSubjectsNameAdapter(
            requireContext(),
            journal.subjects.map { it.name },
            root.journal_subjects_recycler
        )
        JournalDateAdapter(requireContext(), journal.dates, root.journal_date_recycler)
        JournalAverageAdapter(requireContext(), journal.subjects, root.journal_average_recycler)

        val marksAdapter = JournalMarksAdapter(requireContext(), journal.subjects) { subject, cell ->
            if (isStudent || webController == null || behavior == null) return@JournalMarksAdapter

            root.journal_add_mark_recycler.isVisible = true

            val marks = Jsoup.parse(html).getElementsByClass("buttonsMark").map { it.attr("value") }
                .drop(1).toMutableList()
            marks.add("x")

            root.journal_add_mark_recycler.adapter = JournalAddMarksAdapter(requireContext(), marks) {
                val buttonIndex = when (it) {
                    "н" -> 10
                    "10" -> 11
                    "x" -> {
                        webController.js("document.getElementsByTagName('table')[2].getElementsByTagName('tr')[${subject.index + 2}].getElementsByTagName('td')[${cell.index}].getElementsByTagName('span').length - 1") { length ->
                            webController.js("document.getElementsByTagName('table')[2].getElementsByTagName('tr')[${subject.index + 2}].getElementsByTagName('td')[${cell.index}].getElementsByTagName('span')[$length].innerHTML") { index ->
                                val bIndex = when (index) {
                                    "н" -> "10"
                                    "10" -> "11"
                                    else -> index
                                }

                                webController.js("document.getElementsByTagName('table')[2].getElementsByTagName('tr')[${subject.index + 2}].getElementsByTagName('td')[${cell.index}].getElementsByTagName('span')[$length].click()") {
                                    webController.js("document.getElementsByClassName('buttonsMark')[$bIndex].click()")
                                }
                            }
                        }

                        cell.marks.removeLastOrNull()
                        root.journal_marks_recycler.adapter?.notifyItemChanged(subject.index)

                        return@JournalAddMarksAdapter
                    }
                    else -> it.toInt()
                }

                webController.js("document.getElementsByTagName('table')[2].getElementsByTagName('tr')[${subject.index + 2}].getElementsByTagName('td')[${cell.index}].getElementsByTagName('span')[0].click()") {
                    webController.js("document.getElementById('moreMark').click()") {
                        webController.js("document.getElementsByTagName('table')[2].getElementsByTagName('tr')[${subject.index + 2}].getElementsByTagName('td')[${cell.index}].getElementsByTagName('span')[${cell.marks.size}].click()") {
                            webController.js("document.getElementsByClassName('buttonsMark')[$buttonIndex].click()")
                        }
                    }
                }

                cell.marks.add(Journal.Mark(it))
                root.journal_marks_recycler.adapter?.notifyItemChanged(subject.index)
            }

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        root.journal_marks_recycler.adapter = marksAdapter
    }

    private fun updateSelector(
        selector: JournalTeacherSelector,
        webController: WebController,
        behavior: BottomSheetBehavior<*>
    ) {
        JournalSubjectsNameAdapter(requireContext(), selector.groups.keys.map { it.name }, root.journal_groups_recycler)

        val selectorAdapter = JournalSubjectsAdapter(requireContext(), selector)

        selectorAdapter.onClick = { index, pos ->
            behavior.isHideable = true
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
            webController.webView.evaluateJavascript("document.getElementsByTagName('ul')[${index!!.index + 2}].getElementsByTagName('li')[${pos!!.index}].click()") {}
        }

        root.journal_subjects_selector_recycler.adapter = selectorAdapter
    }
}
