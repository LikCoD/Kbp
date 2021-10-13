package com.ldc.kbp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.HttpRequests
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.models.Journal
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.shortSnackbar
import com.ldc.kbp.views.PinnedScrollView
import com.ldc.kbp.views.adapters.journal.*
import kotlinx.android.synthetic.main.fragment_journal.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.concurrent.thread


class JournalFragment : Fragment() {

    private lateinit var root: View

    private val httpRequests = HttpRequests()

    @Throws(IllegalStateException::class)
    fun loginStudent(surname: String, groupId: String, birthday: String): Document {
        val sCode = httpRequests.get("https://nehai.by/ej/templates/login_parent.php")
            .substringAfter("value=\"")
            .substringBefore("\">")

        val result = httpRequests.post(
            "https://nehai.by/ej/ajax.php",
            "action=login_parent&student_name=$surname&group_id=$groupId&birth_day=$birthday&S_Code=$sCode"
        )

        when (result) {
            "Попытка подмены токена, повторите попытку отправки формы!" -> error(R.string.token_error)
            "Неверные данные!" -> error(R.string.incorrect_data)
            "good" -> {
            }
            else -> error(R.string.unknown_response)
        }

        return Jsoup.connect("https://nehai.by/ej/templates/parent_journal.php").get()
    }

    @Throws(IllegalStateException::class)
    fun loginTeacher(surname: String, password: String): Document {
        val sCode = httpRequests.get("https://nehai.by/ej/templates/login_teacher.php")
            .substringAfter("value=\"")
            .substringBefore("\">")

        val result =
            httpRequests.post(
                "https://nehai.by/ej/ajax.php",
                "action=login_teather&login=$surname&password=$password&S_Code=$sCode"
            )

        when (result) {
            "Попытка подмены токена, повторите попытку отправки формы!" -> error(R.string.token_error)
            "Неверный данные!" -> error(R.string.incorrect_data)
            "good" -> {
            }
            else -> error(R.string.unknown_response)
        }

        return Jsoup.connect("https://nehai.by/ej/templates/teacher_journal.php").get()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        with(inflater.inflate(R.layout.fragment_journal, container, false)) {
            root = this
            val bottomSheetBehavior = BottomSheetBehavior.from(journal_bottom_sheet)

            thread {
                val html: Document
                try {
                    if (config.isStudent) {
                        html = loginStudent(config.surname.substringBefore(" "), config.groupId, config.password)
                        journal_subjects_recycler.post {
                            update(html)
                        }
                    } else {
                        html = loginTeacher(config.groupId, config.password)
                        journal_subjects_recycler.post {
                            updateSelector(JournalTeacherSelector.parseTeacherSelector(html), bottomSheetBehavior)
                        }
                    }
                } catch (ex: IllegalStateException) {
                    shortSnackbar(root, ex.message!!.toInt())
                    return@thread
                }
            }

            journal_average_img.setOnClickListener {
                journal_average_scroll.isVisible = !journal_average_scroll.isVisible
            }

            journal_marks_scroll.containers = listOf(
                PinnedScrollView.Container(journal_date_scroll, LinearLayout.HORIZONTAL, false),
                PinnedScrollView.Container(journal_groups_name_scroll, LinearLayout.VERTICAL, false),
                PinnedScrollView.Container(journal_average_scroll, LinearLayout.VERTICAL, false)
            )

            return this
        }
    }


    private fun update(
        document: Document,
        isStudent: Boolean = true,
        behavior: BottomSheetBehavior<*>? = null
    ) {
        val journal = if (isStudent) Journal.parseJournal(document) else Journal.parseTeacherJournal(document)

        JournalSubjectsNameAdapter(requireContext(), journal.subjects.map { it.name }, root.journal_subjects_recycler)
        JournalDateAdapter(requireContext(), journal.dates, root.journal_date_recycler)
        JournalAverageAdapter(requireContext(), journal.subjects, root.journal_average_recycler)

        root.journal_marks_recycler.layoutManager =
            GridLayoutManager(requireContext(), journal.subjects[0].cells.size, GridLayoutManager.VERTICAL, false)

        var selectedMark: Journal.Mark? = null

        val viewMarkAdapter = JournalViewMarkAdapter(requireContext()) { selectedMark = it }

        root.journal_view_marks_recycler.adapter = viewMarkAdapter

        val marksAdapter = JournalCellsAdapter(requireContext(), journal)
        marksAdapter.onClick = onClick@{ cell, pos ->
            behavior ?: return@onClick

            viewMarkAdapter.items = cell.marks

            if (isStudent) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                return@onClick
            }

            val marks = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "X", "н")

            root.journal_add_mark_recycler.adapter = JournalAddMarksAdapter(requireContext(), marks) { action, _ ->
                runBlocking {
                    withContext(Dispatchers.IO) {
                        if (action == "X") {
                            when {
                                selectedMark == null -> cell.marks.removeIf { it.remove(httpRequests, cell) == "0" }
                                selectedMark!!.remove(httpRequests, cell) == "0" -> cell.marks.remove(selectedMark)
                            }

                            root.journal_marks_recycler.post {
                                marksAdapter.notifyItemChanged(pos)

                                viewMarkAdapter.items = cell.marks
                            }
                            return@withContext
                        }

                        val markId = httpRequests.post(
                            "https://nehai.by/ej/ajax.php",
                            "action=set_mark&student_id=${cell.studentId}&pair_id=${cell.pairId}&mark_id=${selectedMark?.markId ?: 0}&value=$action"
                        ).lines().last()

                        if (selectedMark != null) {
                            cell.marks.remove(selectedMark)
                        }

                        cell.marks.add(Journal.Mark(action, markId))

                        root.journal_marks_recycler.post {
                            marksAdapter.notifyItemChanged(pos)

                            viewMarkAdapter.items = cell.marks
                        }
                    }
                }
            }

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        root.journal_marks_recycler.adapter = marksAdapter
    }

    private fun updateSelector(
        selector: JournalTeacherSelector,
        behavior: BottomSheetBehavior<*>
    ) {
        JournalSubjectsNameAdapter(requireContext(), selector.groups.keys.map { it.name }, root.journal_groups_recycler)

        val selectorAdapter = JournalSubjectsAdapter(requireContext(), selector)

        selectorAdapter.onClick = { subject ->
            thread {
                behavior.isHideable = true
                behavior.state = BottomSheetBehavior.STATE_HIDDEN

                val html = httpRequests.post(
                    "https://nehai.by/ej/ajax.php",
                    "action=show_table&subject_id=${subject.subjectId}&group_id=${subject.groupId}"
                )

                root.journal_average_recycler.post {
                    root.journal_groups_recycler.isVisible = false
                    root.journal_subjects_selector_recycler.isVisible = false
                    root.journal_add_mark_recycler.isVisible = true

                    update(Jsoup.parse(html), false, behavior)
                }
            }
        }

        root.journal_subjects_selector_recycler.adapter = selectorAdapter
    }
}
