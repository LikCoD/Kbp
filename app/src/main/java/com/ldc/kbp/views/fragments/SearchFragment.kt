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
import com.ldc.kbp.views.adapters.search.CategoryAdapter
import com.ldc.kbp.views.adapters.search.SearchAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment(var onSelected: (Groups.Timetable) -> Unit = {}) : Fragment() {
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var keyboard: InputMethodManager

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        with(inflater.inflate(R.layout.fragment_search, container, false)) {
            root = this

            keyboard = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

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

        searchAdapter = SearchAdapter(requireContext(), Groups.timetable)
        categoryAdapter = CategoryAdapter(requireContext(), Groups.categories)

        root.category_recycler.adapter = categoryAdapter
        root.groups_recycler.adapter = searchAdapter

        searchAdapter.onItemClickListener = { _, item ->
            keyboard.hideSoftInputFromWindow(requireView().windowToken, 0)

            onSelected(item)
        }

        categoryAdapter.onItemClickListener = { _, _ -> updateSearch() }

    }

    private fun updateSearch(text: String = search_edit.text.toString()) {
        searchAdapter.items =
            Groups.timetable.filter {
                (it.group.lowercase().contains(text.lowercase()) || it.link.lowercase()
                    .contains(text.lowercase())) && (it.categoryIndex == categoryAdapter.selectionIndex ||
                        categoryAdapter.selectionIndex == null)
            }
    }

    fun show() {
        search_edit.setText("")
        search_edit.requestFocus()

        Thread.sleep(250)

        search_edit.requestFocus()
        keyboard.showSoftInput(search_edit, 0)
    }

    fun hide() =
        keyboard.hideSoftInputFromWindow(search_edit.windowToken, 0)

}