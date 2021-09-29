package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.*
import com.ldc.kbp.models.Bells
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.RoundButtonsAdapter
import com.ldc.kbp.views.adapters.timetable.LessonIndexAdapter
import com.ldc.kbp.views.adapters.timetable.TimetableAdapter
import com.ldc.kbp.views.adapters.timetable.TimetableExpandAdapter
import com.ldc.kbp.views.adapters.timetable.WeekIndexAdapter
import com.ldc.kbp.views.fragments.SearchFragment
import com.ldc.kbp.views.itemdecoritions.SpaceDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.fragment_timetable.view.*
import java.time.LocalDate
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class TimetableFragment(val link: String = config.link) : Fragment() {
    private lateinit var root: View
    private lateinit var timetable: Timetable

    private lateinit var timetableAdapter: TimetableAdapter
    private lateinit var weekSelectorAdapter: RoundButtonsAdapter
    private lateinit var lessonIndexAdapter: LessonIndexAdapter
    private lateinit var weekIndexAdapter: WeekIndexAdapter

    private var itemWidth by Delegates.notNull<Float>()
    private var itemHeight by Delegates.notNull<Float>()

    private var multiWeekMode = config.multiWeek

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

        val searchFragment = SearchFragment {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            thread { update(it) }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.groups_selector_fragment, searchFragment).commit()

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

                        searchFragment.hide()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            }
        )

        if (multiWeekMode) timetable_change_week_mode.setBackgroundResource(R.drawable.ic_circle_selected)
        else {
            weekSelectorAdapter.updateItemOnClick = true
            timetable_change_week_mode.setBackgroundResource(R.drawable.ic_circle)
        }

        thread {
            if (link == config.link) update(lTimetable = mainTimetable)
            else update(Groups.timetable.find { it.link == link } ?: Groups.timetable[0])

            timetable_bottom_sheet.post {
                searchFragment.updateGroups()
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
            if (multiWeekMode) timetable_scroll.smoothScrollTo((itemWidth * timetable.daysInWeek * pos).toInt(), 0)
            else {
                timetableAdapter.changeWeekMode(pos)
                weekIndexAdapter.changeWeekMode(pos)
            }
        }

        search_image.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            searchFragment.show()
        }

        update_image.setOnClickListener { thread { update() } }

        bell_image.setOnClickListener { lessonIndexAdapter.isBellShown = !lessonIndexAdapter.isBellShown }

        change_replacement_mode_tv.setOnClickListener {
            change_replacement_mode_tv.setText(
                if (timetableAdapter.changeReplacementMode()) R.string.hide_replacement else R.string.show_replacement
            )
        }

        timetable_change_week_mode.setOnClickListener {
            multiWeekMode = !multiWeekMode

            timetable_scroll.smoothScrollTo(0, 0)

            if (multiWeekMode) {
                timetable_change_week_mode.setBackgroundResource(R.drawable.ic_circle_selected)
                weekSelectorAdapter.updateItemOnClick = false
                timetableAdapter.changeWeekMode(null)
                weekIndexAdapter.changeWeekMode(null)
            } else {
                timetable_change_week_mode.setBackgroundResource(R.drawable.ic_circle)
                weekSelectorAdapter.updateItemOnClick = true
                timetableAdapter.changeWeekMode(weekSelectorAdapter.selectionIndex)
                weekIndexAdapter.changeWeekMode(weekSelectorAdapter.selectionIndex)
            }
        }

        timetable_scroll.setOnScrollChangeListener { _, x, y, _, _ ->
            val selectedWeek = (x / itemWidth / timetable.daysInWeek).toInt()
            if (selectedWeek != weekSelectorAdapter.selectionIndex && multiWeekMode) {
                weekSelectorAdapter.selectionIndex = selectedWeek

                week_selector_recycler.scrollToPosition(selectedWeek)
            }

            days_of_week_scroll.scrollX = x
            lessons_index_scroll.scrollY = y
        }

        days_of_week_scroll.disableActions()
        lessons_index_scroll.disableActions()
    }

    private fun update(info: Groups.Timetable? = null, lTimetable: Timetable? = null) {
        root.loading_tv.post { root.loading_tv.isVisible = true }

        timetable = lTimetable ?: Timetable.loadTimetable(info ?: timetable.info!!)

        root.timetable_recycler.post {
            requireActivity().toolbar.title = timetable.info?.group

            timetableAdapter.changeData(timetable)

            weekSelectorAdapter.items = timetable.weeks.indices.map { (it + 1).toString() }

            weekIndexAdapter =
                WeekIndexAdapter(requireContext(), timetable.weeks.size, timetable.daysInWeek, days_of_week_layout)

            weekIndexAdapter.changeWeekMode(if (multiWeekMode) null else getCurrentWeek(timetable.weeks.size))

            val bells = Bells(mutableListOf())
            bells.load()

            lessonIndexAdapter =
                LessonIndexAdapter(requireContext(), timetable.lessonsInDay, root.lessons_index_recycler)

            root.timetable_scroll.post {
                val days = timetable.weeks[getCurrentWeek(timetable.weeks.size)].days

                val scrollX = if (multiWeekMode) {
                    (LocalDate.now().dayOfWeek.ordinal + timetable.daysInWeek * getCurrentWeek(timetable.weeks.size)) * itemWidth.toInt()
                } else {
                    weekSelectorAdapter.selectionIndex = getCurrentWeek(timetable.weeks.size)
                    timetableAdapter.changeWeekMode(weekSelectorAdapter.selectionIndex)
                    (LocalDate.now().dayOfWeek.ordinal * itemWidth).toInt()
                }

                root.timetable_scroll.smoothScrollTo(
                    scrollX,
                    (days.getOrElse(LocalDate.now().dayOfWeek.ordinal) { days[0] }.replacementLessons.indexOfFirst { it != null } * itemHeight).toInt()
                )

                root.loading_tv.isVisible = false
            }
        }
    }
}