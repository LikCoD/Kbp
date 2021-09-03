package com.ldc.kbp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ldc.kbp.*
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import com.ldc.kbp.views.fragments.GroupSelectorFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        with(inflater.inflate(R.layout.fragment_settings, container, false)) {
            val bottomSheetBehavior = BottomSheetBehavior.from(settings_bottom_sheet)

            val groupSelectorFragment = GroupSelectorFragment { timetable ->
                group_name_tv.text = timetable.group
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                shortToast(requireContext(), R.string.loading)

                config.link = timetable.link

                when (Groups.categories.toList()[timetable.categoryIndex]) {
                    "преподаватель" -> {
                        getUrlFromGroup(
                            requireContext(),
                            "https://nehai.by/ej/t.php",
                            timetable.group
                        ) {
                            config.groupId = it
                            Files.saveConfig(requireContext())
                        }

                        config.isStudent = false
                        config.group = ""
                        config.surname = timetable.group
                        Files.saveConfig(requireContext())

                        name_layout.isVisible = false
                        password_tv.setText(R.string.password)
                    }
                    "группа" -> {
                        getUrlFromGroup(
                            requireContext(),
                            "https://nehai.by/ej",
                            timetable.group
                        ) {
                            config.groupId = it
                            Files.saveConfig(requireContext())
                        }

                        config.isStudent = true
                        config.group = timetable.group
                        Files.saveConfig(requireContext())

                        name_layout.isVisible = true
                        password_tv.setText(R.string.birthday)
                    }
                }
            }

            bottomSheetBehavior.addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                            groupSelectorFragment.hideKeyboard()
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                }
            )

            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.groups_selector_fragment, groupSelectorFragment).commit()

            search_image.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                groupSelectorFragment.showKeyboard()
            }

            department_auto.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.departments)
                )
            )

            name_et.setText(config.surname)
            password_et.setText(config.password)
            department_auto.setText(config.department)
            sex_swither.isChecked = config.isFemale

            group_name_tv.text = timetable.info?.group

            confirm_button.setOnClickListener {
                if (config.isStudent) config.surname = name_et.text.toString()

                config.password = password_et.text.toString()
                config.department = department_auto.text.toString()
                config.isFemale = sex_swither.isChecked

                Files.saveConfig(requireContext())
            }
            return this
        }


    @SuppressLint("SetJavaScriptEnabled")
    private fun getUrlFromGroup(
        context: Context,
        link: String,
        group: String,
        afterLoad: (String) -> Unit
    ) {
        val webView = WebView(context)
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript(getAssets(requireContext(), "journalLogins.js")) {
                    afterLoad(
                        it.substring(1, it.length - 1).split("|").find { line ->
                            line.substringBefore("-") == group
                        }?.substringAfter("-") ?: ""
                    )
                }
            }
        }
        webView.loadUrl(link)
    }
}