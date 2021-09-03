package com.ldc.kbp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.getAssets
import kotlinx.android.synthetic.main.fragment_journal.view.*

class JournalFragment : Fragment() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_journal, container, false)) {
            journal_web.settings.javaScriptEnabled = true
            journal_web.settings.domStorageEnabled = true
            journal_web.webViewClient = ViewClient(requireContext())

            journal_web.loadUrl(if (config.isStudent) "https://nehai.by/ej/index.php" else "https://nehai.by/ej/t.php")

            return this
        }
    }

    class ViewClient(val context: Context) : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            view?.evaluateJavascript(getAssets(context, "journalScale.js")) {}

            if (config.isStudent) {
                val surname = config.surname.substringBefore(" ")

                view?.evaluateJavascript("document.getElementById('student_name').value = '$surname';") {}
                view?.evaluateJavascript("document.getElementById('group_id').value = '${config.groupId}';") {}
                view?.evaluateJavascript("document.getElementById('birth_day').value = '${config.password}';") {}
                view?.evaluateJavascript("check_login()") {}
            } else {
                view?.evaluateJavascript("document.getElementById('login').value = '${config.groupId}';") {}
                view?.evaluateJavascript("document.getElementById('password').value = '${config.password}';") {}
                view?.evaluateJavascript("check_login()") {}
            }
        }
    }
}
