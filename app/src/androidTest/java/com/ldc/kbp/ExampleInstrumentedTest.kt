package com.ldc.kbp

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.tom_roush.pdfbox.io.IOUtils
import org.jsoup.Jsoup
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.concurrent.thread


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
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

            println(result)
        }

        Thread.sleep(10000)

        //Jsoup.connect("https://nehai.by/ej/templates/parent_journal.php").cookieStore(cookiesStore).get()
    }
}