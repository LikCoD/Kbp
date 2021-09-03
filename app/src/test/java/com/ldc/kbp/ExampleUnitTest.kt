package com.ldc.kbp

import android.util.Log
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import org.junit.Test
import java.time.LocalDate


class ExampleUnitTest {
    @Test
    fun main() {
        print(LocalDate.now().toString())
    }
}