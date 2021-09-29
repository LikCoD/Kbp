package com.ldc.kbp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.disableActions
import com.ldc.kbp.getHtmlBodyFromWebView
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.journal.JournalAverageAdapter
import com.ldc.kbp.views.adapters.journal.JournalDateAdapter
import com.ldc.kbp.views.adapters.journal.JournalSubjectsAdapter
import com.ldc.kbp.views.adapters.journal.JournalSubjectsNameAdapter
import kotlinx.android.synthetic.main.fragment_journal.view.*

class JournalFragment : Fragment() {

    private lateinit var root: View

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_journal, container, false)) {
            root = this

            val webView = WebView(context)
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    if (config.isStudent) {
                        val surname = config.surname.substringBefore(" ")

                        view?.evaluateJavascript("document.getElementById('student_name').value = '$surname';") {}
                        view?.evaluateJavascript("document.getElementById('group_id').value = '${config.groupId}';") {}
                        view?.evaluateJavascript("document.getElementById('birth_day').value = '${config.password}';") {}
                    } else {
                        view?.evaluateJavascript("document.getElementById('login').value = '${config.groupId}';") {}
                        view?.evaluateJavascript("document.getElementById('password').value = '${config.password}';") {}
                    }

                    view?.evaluateJavascript("check_login()") {}

                    getHtmlBodyFromWebView(context, "https://nehai.by/ej/parent_journal.php") {
                        val journal = Journal.parseJournal(it)
                        update(journal)
                    }
                }
            }

            webView.loadUrl(if (config.isStudent) "https://nehai.by/ej/index.php" else "https://nehai.by/ej/t.php")

            journal_marks_scroll.setOnScrollChangeListener { _, x, y, _, _ ->
                journal_date_scroll.scrollX = x
                journal_subjects_name_scroll.scrollY = y
                journal_average_scroll.scrollY = y
            }

            journal_average_img.setOnClickListener {
                journal_average_scroll.isVisible = !journal_average_scroll.isVisible
            }

            journal_date_scroll.disableActions()
            journal_subjects_name_scroll.disableActions()
            journal_average_scroll.disableActions()

            return this
        }
    }


    fun update(journal: Journal){
        JournalDateAdapter(requireContext(), journal.months, root.journal_date_recycler)
        JournalSubjectsNameAdapter(requireContext(), journal.months[0].subjects.map { it.name }, root.journal_subjects_recycler)
        JournalAverageAdapter(requireContext(), journal.months.flatMap { it.subjects }, root.journal_average_recycler)
        root.journal_marks_recycler.adapter = JournalSubjectsAdapter(requireContext(), journal)
    }
}
