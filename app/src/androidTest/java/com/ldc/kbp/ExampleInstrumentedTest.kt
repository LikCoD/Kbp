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
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(path, "/hello.pdf")

        val pdfWriter = PdfWriter(file.path)
        val pdfDocument = PdfDocument(pdfWriter).apply { catalog.setLang(PdfString("ru-RU")) }


        // Create document to add new elements

        // Create document to add new elements
        val document = Document(pdfDocument)

        // Create Paragraph


        val fontContents: ByteArray = IOUtils.toByteArray(javaClass.getResourceAsStream("times-new-roman.ttf"))
        val fontProgram = FontProgramFactory.createFont(fontContents)
        document.setFont(PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H))

        // Create Paragraph
        val paragraph = Paragraph("МЕНЯ ЗОВУТ Я")

        // Add Paragraph to document

        // Add Paragraph to document
        document.add(paragraph)

        // Close document

        // Close document

        document.close()
    }
}