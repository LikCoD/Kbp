package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.*
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.PinnedScrollView
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import kotlin.properties.Delegates

class TimetableFragment(private val info: Groups.Timetable? = null) : Fragment() {
    private lateinit var root: View
    private lateinit var timetable: Timetable

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
            SearchFragment(groups_selector_fragment, Groups.timetable, { it.group to it.category }) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                update(it)
            }

        timetableAdapter = TimetableAdapter(requireContext(), mainTimetable)
        weekSelectorAdapter = RoundButtonsAdapter(requireContext(), false)

        val expandAdapter = TimetableExpandAdapter(requireContext())
        weekIndexAdapter =
            WeekIndexAdapter(requireContext(), mainTimetable.weeksCount, mainTimetable.daysInWeek)
        lessonIndexAdapter = LessonIndexAdapter(requireContext(), mainTimetable.lessonsInDay)

        week_selector_recycler.adapter = weekSelectorAdapter
        week_selector_recycler.addItemDecoration(SpaceDecoration(20))
        subject_expand_recycler.adapter = expandAdapter

        days_of_week_scroll.setup(weekIndexAdapter)
        timetable_scroll.setup(timetableAdapter, mainTimetable.lessonsInDay)
        lessons_index_scroll.setup(lessonIndexAdapter)

        update(info)

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

        timetable_multi_week.isSelected = config.multiWeek

        timetableAdapter.onLessonExpand = { lesson ->
            expandAdapter.items = lesson.subjects

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

            subject_expand_recycler.isVisible = true
        }

        weekSelectorAdapter.onItemClickListener = { pos, _ ->
            if (timetable_multi_week.isSelected)
                timetable_scroll.smoothScrollTo(itemWidth * timetable.daysInWeek * pos, 0)
            else {
                timetableAdapter.shownWeek = pos
                weekIndexAdapter.shownWeek = pos
            }
        }

        search_image.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            searchFragment.show()
        }

        update_image.setOnClickListener { update(timetable.info) }

        bell_image.setOnClickListener { lessonIndexAdapter.updateBells() }

        change_replacement_mode_tv.setOnClickListener {
            timetableAdapter.isReplacementShown = !timetableAdapter.isReplacementShown

            change_replacement_mode_tv.setText(if (timetableAdapter.isReplacementShown) R.string.hide_replacement else R.string.show_replacement)
        }

        timetable_multi_week.onSelectionChanged = {
            weekSelectorAdapter.updateItemOnClick = it

            if (it) {
                timetableAdapter.shownWeek = weekSelectorAdapter.selectionIndex
                weekIndexAdapter.shownWeek = weekSelectorAdapter.selectionIndex
            } else {
                timetableAdapter.shownWeek = null
                weekIndexAdapter.shownWeek = null
            }
        }

        timetable_scroll.setOnScrollChangeListener { _, x, _, _, _ ->
            val selectedWeek = x / itemWidth / timetable.daysInWeek
            if (selectedWeek != weekSelectorAdapter.selectionIndex && timetable_multi_week.isSelected) {
                weekSelectorAdapter.selectionIndex = selectedWeek

                week_selector_recycler.scrollToPosition(selectedWeek)
            }
        }

        timetable_scroll.containers = listOf(
            PinnedScrollView.Container(days_of_week_scroll, LinearLayout.HORIZONTAL, false),
            PinnedScrollView.Container(lessons_index_scroll, LinearLayout.VERTICAL, false)
        )
    }

    private fun update(info: Groups.Timetable? = null) {
        MainScope().launch {
            root.loading_tv.isVisible = true

            launch(Dispatchers.IO) {
                timetable = if (info == null) mainTimetable else Timetable.loadTimetable(info)

                if (info == mainTimetable.info) mainTimetable = timetable

                launch(Dispatchers.Main) {
                    requireActivity().toolbar.title = timetable.info?.group

                    if (info != null) timetableAdapter.timetable = timetable

                    weekSelectorAdapter.items = timetable.weeks.indices.map { (it + 1).toString() }

                    weekIndexAdapter.shownWeek = if (timetable_multi_week.isSelected) null else getCurrentWeek()

                    var sX = LocalDate.now().dayOfWeek.ordinal
                    val sY = timetable.firstLessonIndex

                    if (timetable_multi_week.isSelected) {
                        sX += timetable.daysInWeek * getCurrentWeek()
                    } else {
                        weekSelectorAdapter.selectionIndex = getCurrentWeek()
                        timetableAdapter.shownWeek = weekSelectorAdapter.selectionIndex
                    }

                    root.timetable_scroll.smoothScrollTo(sX * itemWidth, sY * itemHeight)

                    root.loading_tv.isVisible = false
                }
            }
        }
    }
}