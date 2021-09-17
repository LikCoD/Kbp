package com.ldc.kbp.models.statements

import android.app.Activity
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.ldc.kbp.getFile
import com.ldc.kbp.models.Deprecates
import java.io.File

object Statement {
    var type = StatementsType.ABSENCE
    var department = ""
    var date = ""
    var name = ""
    var group: String? = null
    var fullName: String? = null
    var regAddress: String? = null
    var liveAddress: String? = null
    var phone: String? = null

    fun createStatement(activity: Activity, paragraphs: List<Paragraph>): File {
        val file = getFile(Environment.DIRECTORY_DOCUMENTS, "/Заявление от $date.$type.pdf")

        val document =
            Document(PdfDocument(PdfWriter(file.path)).apply { catalog.lang = PdfString("ru-RU") })

        document.setFont(
            PdfFontFactory.createFont(
                FontProgramFactory.createFont(
                    activity.assets.open("times-new-roman.ttf").readBytes()
                ),
                PdfEncodings.IDENTITY_H
            )
        )

        fun createCell(text: String, alignment: TextAlignment): Cell {
            val cell = Cell()
            cell.add(Paragraph(text))
            cell.setTextAlignment(alignment)
            cell.setBorder(Border.NO_BORDER)
            return cell
        }

        val departmentWords = department.split(" ")

        val headerCols = Deprecates.pdfTable(2)
        headerCols.addCell(createCell(departmentWords.getOrNull(0) ?: "", TextAlignment.LEFT))
        headerCols.addCell(createCell("Директору", TextAlignment.RIGHT))
        headerCols.addCell(createCell(departmentWords.getOrNull(1) ?: "", TextAlignment.LEFT))
        headerCols.addCell(createCell("Колледжа бизнеса и права", TextAlignment.RIGHT))
        headerCols.addCell(createCell(departmentWords.getOrNull(2) ?: "", TextAlignment.LEFT))
        headerCols.addCell(createCell("Суворова М. Э.", TextAlignment.RIGHT))

        document.add(headerCols)

        if (fullName != null) {
            document.add(Paragraph(fullName).apply {
                setMarginTop(25F)
                setTextAlignment(TextAlignment.RIGHT)
            })
            document.add(Paragraph("Зарегистрированного по адресу:").apply {
                setTextAlignment(
                    TextAlignment.RIGHT
                )
            })
            document.add(Paragraph(regAddress).apply { setTextAlignment(TextAlignment.RIGHT) })
            document.add(Paragraph("Проживающего по адрусу:").apply {
                setTextAlignment(
                    TextAlignment.RIGHT
                )
            })
            document.add(Paragraph(liveAddress).apply { setTextAlignment(TextAlignment.RIGHT) })
            document.add(Paragraph("Тел.: $phone").apply { setTextAlignment(TextAlignment.RIGHT) })
        }

        document.add(Paragraph("ЗАЯВЛЕНИЕ").apply { setMarginTop(50F) })
        document.add(Paragraph(date).apply {
            setMarginTop(5F)
            setMarginBottom(5F)
        })

        paragraphs.forEach {
            document.add(it.apply {
                setFirstLineIndent(25F)
                setTextAlignment(TextAlignment.JUSTIFIED)
            })
        }

        document.add(Paragraph("Учащий(ая)ся").apply { setMarginTop(30F) })

        if (group != "") {
            val footerCols = Deprecates.pdfTable(2)
            footerCols.addCell(createCell("учебной группы $group", TextAlignment.LEFT))
            footerCols.addCell(createCell(name, TextAlignment.RIGHT))

            document.add(footerCols)
        } else document.add(Paragraph(name).apply { setTextAlignment(TextAlignment.RIGHT) })

        document.close()

        val uri = FileProvider.getUriForFile(activity, "com.ldc.kbp.fragments.provider", file)

        val intent = Deprecates.shareIntentBuilder(activity)
            .setType("application/pdf")
            .setStream(uri)
            .setChooserTitle("Заявление")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        activity.startActivity(intent)

        return file
    }
}