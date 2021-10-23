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
import com.ldc.kbp.*
import com.ldc.kbp.models.Journal
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.views.PinnedScrollView
import com.ldc.kbp.views.adapters.journal.*
import com.ldc.kbp.views.adapters.search.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_journal.view.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.concurrent.thread


class JournalFragment : Fragment() {

    private lateinit var root: View

    private val httpRequests = HttpRequests()

    @Throws(IllegalStateException::class)
    fun loginStudent(surname: String, groupId: String, birthday: String): Document {
        val sCode = httpRequests.get("https://nehai.by/ej/templates/login_parent.php").substringAfter("value=\"")
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
        val sCode = httpRequests.get("https://nehai.by/ej/templates/login_teacher.php").substringAfter("value=\"")
            .substringBefore("\">")

        val result = httpRequests.post(
            "https://nehai.by/ej/ajax.php", "action=login_teather&login=$surname&password=$password&S_Code=$sCode"
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

    private lateinit var info: JournalTeacherSelector.Subject
    private var pairType = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_journal, container, false).apply {
        root = this

        val bottomSheetBehavior = BottomSheetBehavior.from(journal_bottom_sheet)

        val categoryAdapter = CategoryAdapter(requireContext(), listOf("Лекция", "Практика", "Лабораторная"), false)
        categoryAdapter.onItemClickListener = { pos, _ -> pairType = pos }
        categoryAdapter.selectionIndex = 0

        category_recycler.adapter = categoryAdapter

        val datePickerPopup = createDatePicker(requireContext()) { date ->
            journal_add_date_layout.isVisible = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            date_tv.text = date.toString()
        }

        confirm_button.setOnClickListener {
            journal_add_date_layout.isVisible = false
            CoroutineScope(Dispatchers.IO).launch {
                val pairType = categoryAdapter.selectionIndex
                val description = description_edit.text.toString()

                val newHtml = httpRequests.post(
                    "https://nehai.by/ej/ajax.php",
                    "action=add_date&new_date=${date_tv.text}&subject_id=${info.subjectId}&group_id=${info.groupId}&pair_type=$pairType&pair_disc=$description"
                )

                update(Jsoup.parse(newHtml), bottomSheetBehavior, false)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val html: Document
                if (config.isStudent) {
                    html = loginStudent(config.surname.substringBefore(" "), config.groupId, config.password)
                    update(html, bottomSheetBehavior)
                } else {
                    html = loginTeacher(config.groupId, config.password)
                    updateSelector(JournalTeacherSelector.parseTeacherSelector(html), bottomSheetBehavior)
                }
            } catch (ex: IllegalStateException) {
                shortSnackbar(root, ex.message!!.toInt())
            }
        }

        journal_average_img.setOnClickListener {
            journal_average_scroll.isVisible = !journal_average_scroll.isVisible
        }

        journal_add_date_img.setOnClickListener {
            datePickerPopup.show()
        }

        journal_marks_scroll.containers = listOf(
            PinnedScrollView.Container(journal_date_scroll, LinearLayout.HORIZONTAL, false),
            PinnedScrollView.Container(journal_subjects_name_scroll, LinearLayout.VERTICAL, false),
            PinnedScrollView.Container(journal_average_scroll, LinearLayout.VERTICAL, false)
        )
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun update(
        document: Document, behavior: BottomSheetBehavior<*>, isStudent: Boolean = true
    ) = MainScope().launch {
        val journal = if (isStudent) Journal.parseJournal(document) else Journal.parseTeacherJournal(document)

        root.journal_subjects_name_scroll.setup(
            JournalSubjectsNameAdapter(requireContext(), journal.subjects.map { it.name })
        )
        root.journal_date_scroll.setup(JournalDateAdapter(requireContext(), journal.dates))
        root.journal_average_scroll.setup(JournalAverageAdapter(requireContext(), journal.subjects))

        root.journal_marks_recycler.layoutManager =
            GridLayoutManager(requireContext(), journal.subjects[0].cells.size, GridLayoutManager.VERTICAL, false)

        var selectedMark: Journal.Mark? = null

        val viewMarkAdapter = JournalViewMarkAdapter(requireContext()) { selectedMark = it }

        root.journal_view_marks_recycler.adapter = viewMarkAdapter

        if (!isStudent) root.journal_add_date_img.isVisible = !config.isStudent

        val marksAdapter = JournalCellsAdapter(requireContext(), journal)
        marksAdapter.onClick = onClick@{ cell, pos ->
            viewMarkAdapter.items = cell.marks

            if (isStudent) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                return@onClick
            }

            val marks = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "X", "н")

            root.journal_add_mark_recycler.adapter = JournalAddMarksAdapter(requireContext(), marks) { action, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    if (action == "X") {
                        when {
                            selectedMark == null -> cell.marks.removeIf { it.remove(httpRequests, cell) == "0" }
                            selectedMark!!.remove(httpRequests, cell) == "0" -> cell.marks.remove(selectedMark)
                        }
                    } else {
                        val markId = httpRequests.post(
                            "https://nehai.by/ej/ajax.php",
                            "action=set_mark&student_id=${cell.studentId}&pair_id=${cell.pairId}&mark_id=${selectedMark?.markId ?: 0}&value=$action"
                        ).lines().last()

                        if (selectedMark != null)
                            cell.marks.remove(selectedMark)

                        cell.marks.add(Journal.Mark(action, markId))
                    }

                    launch(Dispatchers.Main) {
                        marksAdapter.notifyItemChanged(pos)

                        viewMarkAdapter.items = cell.marks
                    }
                }
            }

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        root.journal_marks_recycler.adapter = marksAdapter
    }

    private fun updateSelector(
        selector: JournalTeacherSelector, behavior: BottomSheetBehavior<*>
    ) {
        MainScope().launch {
            JournalSubjectsNameAdapter(requireContext(), selector.groups.keys.map { it.name })

            val selectorAdapter = JournalSubjectsSelectorAdapter(requireContext(), selector)

            selectorAdapter.onClick = { subject ->
                info = subject

                behavior.isHideable = true
                behavior.state = BottomSheetBehavior.STATE_HIDDEN

                thread {
                    val html = httpRequests.post(
                        "https://nehai.by/ej/ajax.php",
                        "action=show_table&subject_id=${subject.subjectId}&group_id=${subject.groupId}"
                    )

                    root.journal_average_scroll.post {
                        root.journal_subjects_selector_recycler.isVisible = false
                        root.journal_add_mark_recycler.isVisible = true

                        update(Jsoup.parse(html), behavior, false)
                    }
                }
            }

            root.journal_subjects_selector_recycler.adapter = selectorAdapter
        }
    }
}
