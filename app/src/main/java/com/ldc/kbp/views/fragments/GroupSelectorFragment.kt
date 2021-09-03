package com.ldc.kbp.views.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.ldc.kbp.models.Groups
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.groupselector.CategoryAdapter
import com.ldc.kbp.views.adapters.groupselector.GroupSelectionAdapter
import kotlinx.android.synthetic.main.fragment_group_selector.*
import kotlinx.android.synthetic.main.fragment_group_selector.view.*

class GroupSelectorFragment(var onGroupSelected: (Groups.Timetable) -> Unit = {}) : Fragment() {
    private lateinit var groupSelectorAdapter: GroupSelectionAdapter
    private lateinit var groupSelectorCategoryAdapter: CategoryAdapter

    private lateinit var keyboard: InputMethodManager

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        with(inflater.inflate(R.layout.fragment_group_selector, container, false)) {
            root = this

            keyboard =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            updateGroups()

            search_edit.doOnTextChanged { text, _, _, _ -> updateSearch(text.toString()) }

            groups_recycler.setOnTouchListener { view, _ ->
                view.performClick()
                view.parent.requestDisallowInterceptTouchEvent(true)

                false
            }
            
            return this
        }

    fun updateGroups() {
        if (Groups.timetable.isEmpty()) return

        groupSelectorAdapter = GroupSelectionAdapter(requireContext(), Groups.timetable)
        groupSelectorCategoryAdapter = CategoryAdapter(requireContext(), Groups.categories)

        root.category_recycler.adapter = groupSelectorCategoryAdapter
        root.groups_recycler.adapter = groupSelectorAdapter

        groupSelectorAdapter.onItemClickListener = { _, item ->
            keyboard.hideSoftInputFromWindow(requireView().windowToken, 0)

            onGroupSelected(item)
        }

        groupSelectorCategoryAdapter.onItemClickListener = { _, _ -> updateSearch() }

    }

    private fun updateSearch(text: String = search_edit.text.toString()) {
        groupSelectorAdapter.items =
            Groups.timetable.filter {
                (it.group.lowercase().contains(text.lowercase()) || it.link.lowercase()
                    .contains(text.lowercase())) &&
                        (it.categoryIndex == groupSelectorCategoryAdapter.selectionIndex ||
                                groupSelectorCategoryAdapter.selectionIndex == null)
            }
    }

    fun showKeyboard() {
        search_edit.setText("")
        search_edit.requestFocus()

        Thread.sleep(250)

        search_edit.requestFocus()
        keyboard.showSoftInput(search_edit, 0)
    }

    fun hideKeyboard() =
        keyboard.hideSoftInputFromWindow(search_edit.windowToken, 0)

}