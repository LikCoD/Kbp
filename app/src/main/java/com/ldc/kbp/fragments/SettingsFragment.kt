package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.ldc.kbp.*
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Schedule
import com.ldc.kbp.views.fragments.SearchFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlin.concurrent.thread
import likco.studyum.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false).apply {
        val bottomSheetBehavior = BottomSheetBehavior.from(settings_bottom_sheet)

        if (!config.isStudent) {
            name_layout.isVisible = false
            password_tv.setText(R.string.password)
        }

        val groups =
            Groups.groupsJournal.map { it to "Группа" } + Groups.teachersJournal.map { it to "Предподаватель" }

        val noLoginsSelector = SearchFragment(no_login_groups_selector_fragment, groups, { it.name }) {
            config.groupId = it.id
            Files.saveConfig(requireContext())

            no_login_groups_selector_fragment.isVisible = false
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        val search = SearchFragment(groups_selector_fragment, Groups.timetable.map { it to Groups.getRusType(it) }, { it.name })
        search.onSelected = { info ->
            thread { mainSchedule = Schedule.load(info){} ?: return@thread }

            group_name_tv.text = info.name
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            config.scheduleInfo = info

            fun checkGroup(simpleInfo: Groups.SimpleInfo?) {
                if (simpleInfo == null) {
                    Snackbar.make(confirm_button, R.string.logins_not_match, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.enter) {
                            groups_selector_fragment.isVisible = false

                            noLoginsSelector.show()

                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }.show()
                } else {
                    config.groupId = simpleInfo.id
                }
            }

            when (info.type) {
                "teacher" -> {
                    val id = Groups.teachersJournal.find { it.name.lowercase() == info.name.lowercase() }

                    config.isStudent = false
                    config.group = ""
                    config.surname = info.name

                    name_layout.isVisible = false
                    password_tv.setText(R.string.password)

                    checkGroup(id)
                }
                "group" -> {
                    val id = Groups.groupsJournal.find { it.name.lowercase() == info.name.lowercase() }

                    config.isStudent = true
                    config.group = info.name

                    name_layout.isVisible = true
                    password_tv.setText(R.string.birthday)

                    checkGroup(id)
                }
            }
            Files.saveConfig(requireContext())
        }

        bottomSheetBehavior.onStateChanged { _, newState ->
            if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                search.hide()
        }

        search_image.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            no_login_groups_selector_fragment.isVisible = false

            search.show()
        }

        department_auto.setAdapter(
            ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.departments)
            )
        )

        multi_week_mode_switcher.setOnCheckedChangeListener { _, isChecked ->
            config.multiWeek = isChecked
            Files.saveConfig(requireContext())
        }

        multi_month_mode_switcher.setOnCheckedChangeListener { _, isChecked ->
            config.multiMonth = isChecked
            Files.saveConfig(requireContext())
        }

        name_et.setText(config.surname)
        password_et.setText(config.password)
        department_auto.setText(config.department)
        multi_week_mode_switcher.isChecked = config.multiWeek
        multi_month_mode_switcher.isChecked = config.multiMonth
        sex_switcher.isChecked = config.isFemale

        group_name_tv.text = mainSchedule.info.typeName

        confirm_button.setOnClickListener {
            if (config.isStudent) config.surname = name_et.text.toString()

            config.password = password_et.text.toString()
            config.department = department_auto.text.toString()
            config.isFemale = sex_switcher.isChecked

            Files.saveConfig(requireContext())
        }
    }
}