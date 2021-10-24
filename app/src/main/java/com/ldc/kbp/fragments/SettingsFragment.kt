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
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.mainTimetable
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.onStateChanged
import com.ldc.kbp.views.fragments.SearchFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.concurrent.thread

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

        val searchFragment = SearchFragment(groups_selector_fragment, Groups.timetable, { it.group to it.category })
        searchFragment.onSelected = { timetableInfo ->
            thread { mainTimetable = Timetable.loadTimetable(timetableInfo) }

            group_name_tv.text = timetableInfo.group
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            config.link = timetableInfo.link
            config.timetableInfo = timetableInfo

            fun checkGroup(id: String?) {
                if (id == null) {
                    journal_id.setText(timetableInfo.group)
                    Snackbar.make(confirm_button, R.string.logins_not_match, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.enter) {
                            journal_id_layout.isVisible = true
                        }.show()
                } else {
                    config.groupId = id
                    journal_id.setText(id)
                }
            }

            when (timetableInfo.category) {
                "преподаватель" -> {
                    val id =
                        getUrlFromGroup("https://nehai.by/ej/templates/login_teacher.php", timetableInfo.group)

                    config.isStudent = false
                    config.group = ""
                    config.surname = timetableInfo.group

                    name_layout.isVisible = false
                    password_tv.setText(R.string.password)

                    checkGroup(id)
                }
                "группа" -> {
                    val id =
                        getUrlFromGroup(
                            "https://nehai.by/ej/templates/login_parent.php", timetableInfo.group.lowercase()
                        )

                    config.isStudent = true
                    config.group = timetableInfo.group

                    name_layout.isVisible = true
                    password_tv.setText(R.string.birthday)

                    checkGroup(id)
                }
            }
            Files.saveConfig(requireContext())
        }

        bottomSheetBehavior.onStateChanged { _, newState ->
            if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                searchFragment.hide()
        }

        search_image.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            searchFragment.show()
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

        name_et.setText(config.surname)
        journal_id.setText(config.groupId)
        password_et.setText(config.password)
        department_auto.setText(config.department)
        multi_week_mode_switcher.isChecked = config.multiWeek
        sex_switcher.isChecked = config.isFemale

        group_name_tv.text = mainTimetable.info?.group

        confirm_button.setOnClickListener {
            if (config.isStudent) config.surname = name_et.text.toString()

            config.groupId = journal_id.text.toString()
            config.password = password_et.text.toString()
            config.department = department_auto.text.toString()
            config.isFemale = sex_switcher.isChecked

            Files.saveConfig(requireContext())
        }
    }

    private fun getUrlFromGroup(link: String, group: String): String? {
        var id: String

        runBlocking {
            withContext(Dispatchers.IO) {
                id = URL(link)
                    .openConnection()
                    .getInputStream()
                    .bufferedReader()
                    .readText()
                    .substringBefore("\">$group")
                    .substringAfterLast("value=\"")
            }
        }

        return if (id.contains("</select>")) null else id
    }
}