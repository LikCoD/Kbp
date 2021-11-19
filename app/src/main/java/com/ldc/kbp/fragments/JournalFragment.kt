package com.ldc.kbp.fragments

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
import com.ldc.kbp.views.adapters.RoundButtonsAdapter
import com.ldc.kbp.views.adapters.journal.*
import com.ldc.kbp.views.adapters.search.CategoryAdapter
import com.ldc.kbp.views.itemdecoritions.SpaceDecoration
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
        val sCode = httpRequests.get("https://nehai.by/ej/templates/login_parent.php")
            .substringAfter("value=\"")
            .substringBefore("\">")

        val result = httpRequests.post(
            "https://nehai.by/ej/ajax.php",
            "action" to "login_parent",
            "student_name" to surname,
            "group_id" to groupId,
            "birth_day" to birthday,
            "S_Code" to sCode
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

        val result = httpRequests.post(
            "https://nehai.by/ej/ajax.php",
            "action" to "login_teather",
            "login" to surname,
            "password" to password,
            "S_Code" to sCode
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

    private lateinit var journal: Journal

    private lateinit var cellsManager: GridLayoutManager

    private lateinit var weekSelectorAdapter: RoundButtonsAdapter
    private lateinit var dateAdapter: JournalDateAdapter
    private lateinit var cellsAdapter: JournalCellsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_journal, container, false).apply {
        root = this

        val bottomSheetBehavior = BottomSheetBehavior.from(journal_bottom_sheet)

        val categoryAdapter =
            CategoryAdapter(requireContext(), listOf("Лекция", "Практика", "Лабораторная"), false)
        categoryAdapter.onItemClickListener = { pos, _ -> pairType = pos }
        categoryAdapter.selectionIndex = 0

        category_recycler.adapter = categoryAdapter

        val datePickerPopup = createDatePicker(requireContext()) { date ->
            journal_add_date_layout.isVisible = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            date_tv.text = date.toString()
        }

        weekSelectorAdapter = RoundButtonsAdapter(requireContext())
        month_selector_recycler.adapter = weekSelectorAdapter
        month_selector_recycler.addItemDecoration(SpaceDecoration(20))

        weekSelectorAdapter.onItemClickListener = { i, month ->
            dateAdapter.items = journal.dates[i].dates

            cellsManager.spanCount = journal.dates[i].dates.size

            cellsAdapter.items = journal.subjects.flatMap { it.months[i].cells }

            root.month_text.text = requireContext().resources.getStringArray(R.array.months)[month.toInt() - 1]
        }

        confirm_button.setOnClickListener {
            journal_add_date_layout.isVisible = false
            CoroutineScope(Dispatchers.IO).launch {
                val pairType = categoryAdapter.selectionIndex
                val description = description_edit.text.toString()

                val newHtml = httpRequests.post(
                    "https://nehai.by/ej/ajax.php",
                    "action" to "add_date",
                    "new_date" to "${date_tv.text}",
                    "subject_id" to info.subjectId,
                    "group_id" to info.groupId,
                    "pair_type" to "$pairType",
                    "pair_disc" to description
                )

                update(Jsoup.parse(newHtml), bottomSheetBehavior, false)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val html: Document
                if (config.isStudent) {
                    html = loginStudent(
                        config.surname.substringBefore(" "),
                        config.groupId,
                        config.password
                    )
                    update(html, bottomSheetBehavior)
                } else {
                    html = loginTeacher(config.groupId, config.password)
                    updateSelector(
                        JournalTeacherSelector.parseTeacherSelector(html),
                        bottomSheetBehavior
                    )
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


    private fun update(document: Document, behavior: BottomSheetBehavior<*>, isStudent: Boolean = true)
    = MainScope().launch {
        journal = if (isStudent) Journal.parseJournal(document) else Journal.parseTeacherJournal(document)

        root.journal_subjects_name_scroll.setup(JournalSubjectsNameAdapter(requireContext(), journal.subjects.map { it.name }))

        root.month_text.text = requireContext().resources.getStringArray(R.array.months)[journal.dates.last().month - 1]

        dateAdapter = JournalDateAdapter(requireContext(), journal.dates.last())

        root.journal_date_scroll.setup(dateAdapter)
        root.journal_average_scroll.setup(JournalAverageAdapter(requireContext(), journal.subjects))

        weekSelectorAdapter.items = journal.dates.map { it.month.toString() }
        weekSelectorAdapter.selectionIndex = weekSelectorAdapter.items!!.lastIndex

        cellsManager = GridLayoutManager(
            requireContext(),
            journal.dates.last().dates.size,
            GridLayoutManager.VERTICAL,
            false
        )

        root.journal_marks_recycler.layoutManager = cellsManager

        var selectedMark: Journal.Mark? = null

        val viewMarkAdapter = JournalViewMarkAdapter(requireContext()) { selectedMark = it }

        root.journal_view_marks_recycler.adapter = viewMarkAdapter

        if (!isStudent) root.journal_add_date_img.isVisible = !config.isStudent

        cellsAdapter = JournalCellsAdapter(requireContext(), journal)
        cellsAdapter.onClick = onClick@{ cell, pos ->
            viewMarkAdapter.items = cell.marks

            if (isStudent) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                return@onClick
            }

            val marks = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "X", "н")

            root.journal_add_mark_recycler.adapter =
                JournalAddMarksAdapter(requireContext(), marks) { action, _ ->
                    MainScope().launch(Dispatchers.IO) {
                        if (action == "X") {
                            when {
                                selectedMark == null -> cell.marks.removeIf {
                                    it.remove(httpRequests, cell) == "0"
                                }
                                selectedMark!!.remove(
                                    httpRequests,
                                    cell
                                ) == "0" -> cell.marks.remove(selectedMark)
                            }
                        } else {
                            val markId = httpRequests.post(
                                "https://nehai.by/ej/ajax.php",
                                "action" to "set_mark",
                                "student_id" to cell.studentId,
                                "pair_id" to cell.pairId,
                                "mark_id" to "${selectedMark?.markId ?: 0}",
                                "value" to action
                            ).lines().last()

                            if (selectedMark != null)
                                cell.marks.remove(selectedMark)

                            cell.marks.add(Journal.Mark(action, markId))
                        }

                        launch(Dispatchers.Main) {
                            cellsAdapter.notifyItemChanged(pos)

                            viewMarkAdapter.items = cell.marks
                        }
                    }
                }

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        root.journal_marks_recycler.adapter = cellsAdapter
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
                        "action" to "show_table",
                        "subject_id" to subject.subjectId,
                        "group_id" to subject.groupId
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
