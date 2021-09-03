package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.dimen
import com.ldc.kbp.getCurrentWeek
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.RoundButtonsAdapter
import com.ldc.kbp.views.adapters.timetable.TimetableAdapter
import com.ldc.kbp.views.adapters.timetable.TimetableExpandAdapter
import com.ldc.kbp.views.fragments.GroupSelectorFragment
import com.ldc.kbp.views.itemdecoritions.SpaceDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.fragment_timetable.view.*
import kotlinx.android.synthetic.main.item_day_of_week.view.*
import kotlinx.android.synthetic.main.item_lesson_index.view.*
import java.time.LocalDate
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class TimetableFragment : Fragment() {
    private lateinit var root: View
    private lateinit var timetable: Timetable

    private lateinit var timetableAdapter: TimetableAdapter
    private lateinit var weekSelectorAdapter: RoundButtonsAdapter

    private var itemWidth by Delegates.notNull<Float>()
    private var itemHeight by Delegates.notNull<Float>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timetable, container, false).apply {
        root = this

        val bottomSheetBehavior = BottomSheetBehavior.from(timetable_bottom_sheet)

        timetableAdapter = TimetableAdapter(requireContext())
        weekSelectorAdapter = RoundButtonsAdapter(requireContext(), false)
        val expandAdapter = TimetableExpandAdapter(requireContext())

        val groupSelectorFragment = GroupSelectorFragment {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            thread { update(it) }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.groups_selector_fragment, groupSelectorFragment).commit()

        itemWidth =
            dimen(resources, R.dimen.item_subject_width) +
                    dimen(resources, R.dimen.item_subject_margin) * 2

        itemHeight =
            dimen(resources, R.dimen.item_subject_height) +
                    dimen(resources, R.dimen.item_subject_margin) * 2

        subject_expand_recycler.adapter = expandAdapter
        timetable_recycler.adapter = timetableAdapter
        week_selector_recycler.adapter = weekSelectorAdapter

        week_selector_recycler.addItemDecoration(SpaceDecoration(20))

        bottomSheetBehavior.halfExpandedRatio = (
                dimen(resources, R.dimen.bottom_bar_pick_height) +
                        dimen(resources, R.dimen.bottom_bar_subjects_recycler) +
                        dimen(resources, R.dimen.bottom_bar_margin_v) * 2) /
                Deprecates.getScreenSize(requireActivity()).y.toFloat()

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        if (expandAdapter.items != null) {
                            expandAdapter.items = null
                            subject_expand_recycler.isVisible = false
                        }

                        groupSelectorFragment.hideKeyboard()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            }
        )

        thread {
            Groups.loadTimetable()

            com.ldc.kbp.timetable = Timetable.loadTimetable(
                Groups.timetable.find { it.link == config.link } ?: Groups.timetable.toList()[0]
            )

            update(lTimetable = com.ldc.kbp.timetable)

            timetable_bottom_sheet.post {
                groupSelectorFragment.updateGroups()
            }
        }

        timetableAdapter.onLessonExpand = { lesson, i ->
            var s = lesson.subjects
            s = (s.subList(i, s.size) + s.subList(0, i)).toMutableList()
            expandAdapter.items = s

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

            subject_expand_recycler.isVisible = true
        }

        weekSelectorAdapter.onItemClickListener = { pos, _ ->
            timetable_scroll.smoothScrollTo((itemWidth * timetable.daysInWeek * pos).toInt(), 0)
        }

        search_image.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            groupSelectorFragment.showKeyboard()
        }

        update_image.setOnClickListener { thread { update() } }

        change_replacement_mode_tv.setOnClickListener {
            change_replacement_mode_tv.setText(
                if (timetableAdapter.changeMode()) R.string.hide_replacement else R.string.show_replacement
            )
        }

        timetable_scroll.setOnScrollChangeListener { _, x, y, _, _ ->
            val selectedWeek = (x / itemWidth / timetable.daysInWeek).toInt()
            if (selectedWeek != weekSelectorAdapter.selectionIndex) {
                weekSelectorAdapter.selectionIndex = selectedWeek

                week_selector_recycler.scrollToPosition(selectedWeek)
            }

            days_of_week_scroll.scrollX = x
            lessons_index_scroll.scrollY = y
        }

        days_of_week_scroll.disableActions()
        lessons_index_scroll.disableActions()
    }

    override fun onResume() {
        super.onResume()
        root.timetable_scroll.scrollX = (LocalDate.now().dayOfWeek.ordinal * itemWidth).toInt()
    }

    private fun View.disableActions() = setOnTouchListener { view, _ ->
        view.performClick()

        true
    }

    private fun update(info: Groups.Timetable? = null, lTimetable: Timetable? = null) {
        root.loading_tv.post { root.loading_tv.isVisible = true }

        timetable = lTimetable ?: Timetable.loadTimetable(info ?: timetable.info!!)

        root.timetable_recycler.post {
            requireActivity().toolbar.title = timetable.info?.group

            timetableAdapter.changeData(timetable)

            weekSelectorAdapter.items = timetable.weeks.indices.map { (it + 1).toString() }

            root.days_of_week_layout.removeAllViews()
            root.lessons_index_layout.removeAllViews()

            timetable.weeks.map { it.days }.flatten().forEachIndexed { index, _ ->
                val dayOfWeek =
                    requireActivity().layoutInflater.inflate(
                        R.layout.item_day_of_week,
                        days_of_week_layout,
                        false
                    )

                dayOfWeek.week_number_tv.text = ((index / timetable.daysInWeek) + 1).toString()
                dayOfWeek.day_of_week_tv.text =
                    resources.getStringArray(R.array.days_of_weeks)[index % timetable.daysInWeek]

                days_of_week_layout.addView(dayOfWeek)
            }

            (1..timetable.lessonsInDay).forEach { index ->
                val lessonIndex =
                    requireActivity().layoutInflater.inflate(
                        R.layout.item_lesson_index,
                        days_of_week_layout,
                        false
                    )
                lessonIndex.lesson_index_tv.text = index.toString()

                root.lessons_index_layout.addView(lessonIndex)
            }


            root.timetable_scroll.post {
                root.timetable_scroll.smoothScrollTo(
                    (LocalDate.now().dayOfWeek.ordinal * itemWidth).toInt(),
                    (timetable.weeks[getCurrentWeek(timetable.weeks.size)].days[LocalDate.now().dayOfWeek.ordinal]
                        .replacementLessons.indexOfFirst { it != null } * itemHeight).toInt()
                )
            }

            root.loading_tv.isVisible = false
        }
    }
}