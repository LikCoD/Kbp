package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import likco.studyum.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.*
import com.ldc.kbp.models.Files
import com.ldc.kbp.views.adapters.RoundButtonsAdapter
import com.ldc.kbp.views.adapters.diary.DiaryDayAdapter
import com.ldc.kbp.views.adapters.diary.PhotosAdapter
import com.ldc.kbp.views.itemdecoritions.SpaceDecoration
import kotlinx.android.synthetic.main.fragment_diary.view.*
import org.threeten.bp.LocalDate


class DiaryFragment : Fragment() {
    private var dayOfWeek: Int = -1
        set(value) {
            field =
                if (value == 7) {
                    diaryDayAdapter.plusWeek()
                    1
                } else value
        }
    private lateinit var diaryDayAdapter: DiaryDayAdapter

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_diary, container, false).apply {
       /* root = this

        diaryDayAdapter =
            DiaryDayAdapter(
                requireActivity(),
                homeworkList,
                mainSchedule,
                LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong())
            )
        val daysOfWeekSelectorAdapter = RoundButtonsAdapter(
            requireContext(),
            false,
            resources.getStringArray(R.array.days_of_weeks).toList().subList(0, mainSchedule.info.daysCount)
        )

        val photosAdapter = PhotosAdapter(requireActivity())

        dayOfWeek = LocalDate.now().dayOfWeek.value

        val datePickerPopup = createDatePicker(requireContext()) { date ->
            dayOfWeek = date.dayOfWeek.value

            diaryDayAdapter.setDate(date)

            diary_day_recycler.smoothScrollToPosition(dayOfWeek - 1)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(diary_bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        diary_day_recycler.adapter = diaryDayAdapter
        days_of_week_selector_recycler.adapter = daysOfWeekSelectorAdapter
        diary_photos_recycler.adapter = photosAdapter

        days_of_week_selector_recycler.addItemDecoration(SpaceDecoration(20))

        daysOfWeekSelectorAdapter.onItemClickListener = { pos, _ ->
            dayOfWeek = diaryDayAdapter.startWeekDate.plusDays(pos.toLong()).dayOfWeek.value

            diary_day_recycler.smoothScrollToPosition(pos)
        }

        diary_days_of_week_prev.setOnClickListener {
            diaryDayAdapter.minusWeek()

            daysOfWeekSelectorAdapter.selectionIndex = mainSchedule.info.daysCount - 1

            days_of_week_selector_recycler.scrollToPosition(mainSchedule.info.daysCount - 1)
            diary_day_recycler.scrollToPosition(mainSchedule.info.daysCount - 1)
        }

        diary_days_of_week_next.setOnClickListener {
            diaryDayAdapter.plusWeek()

            daysOfWeekSelectorAdapter.selectionIndex = 0

            days_of_week_selector_recycler.scrollToPosition(0)
            diary_day_recycler.scrollToPosition(0)
        }

        diary_day_recycler.addOnItemChangedListener { _, adapterPosition ->
            if (daysOfWeekSelectorAdapter.selectionIndex != adapterPosition) {
                daysOfWeekSelectorAdapter.selectionIndex = adapterPosition

                days_of_week_selector_recycler.smoothScrollToPosition(adapterPosition)
            }
        }

        diaryDayAdapter.onImageAddListener = { i, photo, file ->
            diary_photos_recycler.post { photosAdapter.addPhoto(photo, file) }

            if (i == 0) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        diaryDayAdapter.onHomeworkChanged = {
            homeworkList = it

            Files.saveHomeworkList(requireContext())
        }

        photosAdapter.onAllItemDeleted = { bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN }

        select_date_img.setOnClickListener { datePickerPopup.show() }

        post { diary_day_recycler.scrollToPosition(dayOfWeek - 1) }*/
    }
}