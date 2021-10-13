package com.ldc.kbp

import org.jsoup.Jsoup
import org.junit.Test
import kotlin.concurrent.thread


class ExampleUnitTest {
    @Test
    fun main() {
        thread {
            val connection = Jsoup.connect("https://nehai.by/ej/templates/login_parent.php")

            val sCode = connection.get()
                .toString()
                .substringAfter("value=\"")
                .substringBefore("\">")


            val cookiesStore = connection.cookieStore()

            val result = Jsoup.connect("https://nehai.by/ej/ajax.php")
                .data("action", "login_parent")
                .data("student_name", "Сергеюк")
                .data("group_id", "435")
                .data("birth_day", "24.04.2005")
                .data("S_Code", sCode)
                .cookieStore(cookiesStore)
                .post()
                .text()

            Jsoup.connect("https://nehai.by/ej/templates/parent_journal.php").cookieStore(cookiesStore).get()

            println(result)
        }

        Thread.sleep(100000)
    }
}