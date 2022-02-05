package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.*
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Schedule
import com.ldc.kbp.views.PinnedScrollView
import com.ldc.kbp.views.adapters.RoundButtonsAdapter
import com.ldc.kbp.views.adapters.timetable.LessonIndexAdapter
import com.ldc.kbp.views.adapters.timetable.TimetableAdapter
import com.ldc.kbp.views.adapters.timetable.TimetableExpandAdapter
import com.ldc.kbp.views.adapters.timetable.WeekIndexAdapter
import com.ldc.kbp.views.fragments.SearchFragment
import com.ldc.kbp.views.itemdecoritions.BottomOffsetDecoration
import com.ldc.kbp.views.itemdecoritions.SpaceDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.fragment_timetable.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import kotlin.properties.Delegates


class TimetableFragment(private var info: Groups.Schedule? = null) : Fragment() {
    private lateinit var root: View

    private lateinit var schedule: Schedule

    private lateinit var timetableAdapter: TimetableAdapter
    private lateinit var weekSelectorAdapter: RoundButtonsAdapter
    private lateinit var lessonIndexAdapter: LessonIndexAdapter
    private lateinit var weekIndexAdapter: WeekIndexAdapter

    private var itemWidth by Delegates.notNull<Int>()
    private var itemHeight by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timetable, container, false).apply {
        root = this

        val bottomSheetBehavior = BottomSheetBehavior.from(timetable_bottom_sheet)

        val searchFragment =
            SearchFragment(
                groups_selector_fragment,
                Groups.timetable.map { it to Groups.getRusType(it) },
                { it.name }) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                update(it)
            }

        timetableAdapter = TimetableAdapter(
            requireContext(),
            mainSchedule,
            if (config.multiWeek) null else getCurrentWeek()
        ) {
            weekSelectorAdapter.selectionIndex = it
            week_selector_recycler.scrollToPosition(it)
        }
        weekSelectorAdapter =
            RoundButtonsAdapter(requireContext(), true, firstSelectionIndex = getCurrentWeek())

        val expandAdapter = TimetableExpandAdapter(requireContext())
        weekIndexAdapter =
            WeekIndexAdapter(
                requireContext(),
                mainSchedule.info.weeksCount,
                mainSchedule.info.daysCount
            )
        lessonIndexAdapter = LessonIndexAdapter(
            requireContext(),
            mainSchedule.info.subjectsCount,
            0
        )
        val bottomOffsetDecoration = BottomOffsetDecoration(resources.getDimension(R.dimen.bottomSpace).toInt())
        lessons_index_scroll.addItemDecoration(bottomOffsetDecoration)

        week_selector_recycler.adapter = weekSelectorAdapter
        week_selector_recycler.addItemDecoration(SpaceDecoration(20))
        subject_expand_recycler.adapter = expandAdapter

        update(info)

        timetable_scroll.recyclerView.adapter = timetableAdapter
        timetable_scroll.scrollContainers = listOf(
            PinnedScrollView.Container(days_of_week_scroll, LinearLayout.HORIZONTAL, false),
            PinnedScrollView.Container(lessons_index_scroll, LinearLayout.VERTICAL, false)
        )

        lessons_index_scroll.adapter = lessonIndexAdapter
        lessons_index_scroll.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        days_of_week_scroll.adapter = weekIndexAdapter
        days_of_week_scroll.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        timetable_multi_week.isSelected = config.multiWeek

        itemWidth = dimen(resources, R.dimen.item_subject_width) +
                dimen(resources, R.dimen.item_subject_margin) * 2
        itemHeight = dimen(resources, R.dimen.item_subject_height) +
                dimen(resources, R.dimen.item_subject_margin) * 2

        bottomSheetBehavior.halfExpandedRatio = (
                dimen(resources, R.dimen.bottom_bar_pick_height) +
                        dimen(resources, R.dimen.bottom_bar_subjects_recycler) +
                        dimen(resources, R.dimen.bottom_bar_margin_v) * 2) /
                Deprecates.getScreenSize(requireActivity()).y.toFloat()

        bottomSheetBehavior.onStateChanged { _, newState ->
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                if (expandAdapter.items != null) {
                    expandAdapter.items = null
                    subject_expand_recycler.isVisible = false
                }

                searchFragment.hide()
            }
        }

        timetableAdapter.onExpand = { subjects ->
            expandAdapter.items = subjects

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

            subject_expand_recycler.isVisible = true
        }

        weekSelectorAdapter.onItemClickListener = { pos, _ ->
            if (timetable_multi_week.isSelected)
                timetable_scroll.scrollToX(itemWidth * schedule.info.daysCount * pos)
            else {
                timetableAdapter.shownWeek = pos
                weekIndexAdapter.shownWeek = pos
            }
        }

        search_image.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            searchFragment.show()
        }

        update_image.setOnClickListener { update(info) }

        bell_image.setOnClickListener { lessonIndexAdapter.updateBells() }

        change_replacement_mode_tv.setOnClickListener {
            timetableAdapter.isReplacementShown = !timetableAdapter.isReplacementShown

            change_replacement_mode_tv.setText(if (timetableAdapter.isReplacementShown) R.string.hide_replacement else R.string.show_replacement)
        }

        timetable_multi_week.onSelectionChanged = {
            if (it) {
                timetableAdapter.shownWeek = weekSelectorAdapter.selectionIndex
                weekIndexAdapter.shownWeek = weekSelectorAdapter.selectionIndex
            } else {
                timetableAdapter.shownWeek = null
                weekIndexAdapter.shownWeek = null
            }
        }
    }

    private fun update(i: Groups.Schedule? = null) {
        MainScope().launch {
            root.loading_tv.isVisible = true

            launch(Dispatchers.IO) {
                schedule = if (i == null) mainSchedule else Schedule.load(i.type, i.name)

                if (info?.name == mainSchedule.info.name && info?.type == mainSchedule.info.type)
                    mainSchedule = schedule

                launch(Dispatchers.Main) {
                    requireActivity().toolbar.title = schedule.info.name

                    if (info != null) timetableAdapter.schedule = schedule

                    info = Groups.Schedule(schedule.info.name, schedule.info.type)

                    weekSelectorAdapter.items = (1..schedule.info.weeksCount).map { it.toString() }

                    var sX = LocalDate.now().dayOfWeek.ordinal

                    weekSelectorAdapter.selectionIndex = getCurrentWeek()

                    if (timetable_multi_week.isSelected) {
                        sX += schedule.info.daysCount * schedule.info.subjectsCount * getCurrentWeek()
                    } else {
                        timetableAdapter.shownWeek = weekSelectorAdapter.selectionIndex
                    }

                    root.timetable_scroll.recyclerView.scrollToPosition(sX * schedule.info.subjectsCount)
                    root.days_of_week_scroll.scrollToPosition(sX)

                    root.loading_tv.isVisible = false
                }
            }
        }
    }
}