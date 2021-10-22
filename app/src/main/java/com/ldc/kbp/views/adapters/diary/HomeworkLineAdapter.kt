package com.ldc.kbp.views.adapters.diary

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.ldc.kbp.*
import com.ldc.kbp.models.Homeworks
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.Adapter
import com.ldc.kbp.views.dialogs.HomeworkSetDialog
import kotlinx.android.synthetic.main.item_homework_line.view.*
import org.threeten.bp.LocalDate
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import kotlin.concurrent.thread

class HomeworkLineAdapter(
    private val activity: Activity,
    private val homeworksDay: Homeworks.Day,
    items: List<Timetable.Lesson?>? = null,
    private val date: LocalDate,
    var imageAddListener: ((Int, Bitmap, File) -> Unit) = { _, _, _ -> },
    var onHomeworkChangeListener: (Timetable.Subject, Homeworks.Homework) -> Unit = { _, _ -> }
) : Adapter<Timetable.Lesson?>(activity, items?.filterNotNull(), R.layout.item_homework_line) {
    override fun onBindViewHolder(view: View, item: Timetable.Lesson?, position: Int) {
        val subject = item?.subjects?.get(0) ?: return

        view.item_homework_line_index.text = item.index.toString()
        view.item_homework_line_subject.text = subject.subject
        view.item_homework_line_text.text = homeworksDay.subjects[subject.subject]?.homework

        view.item_homework_line_text.setOnClickListener {
            HomeworkSetDialog(context, subject, homeworksDay.subjects[subject.subject]) {
                onHomeworkChangeListener(subject, it)
            }.show()
        }

        view.item_homework_line_take_photo.setOnClickListener {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, activity) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val file = getFile(
                    Environment.DIRECTORY_PICTURES, "${item.index}. $date ${subject.subject} ${getFilesInMedia(item)?.size ?: 0}"
                )

                intent.putExtra(
                    MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, "com.ldc.kbp.file.provider", file)
                )

                startActivityForResult(activity, intent, 0, null)
            }
        }
        view.item_homework_line_show_photo.setOnClickListener {
            val photos = getFilesInMedia(item)
            if (!photos.isNullOrEmpty()) {
                shortSnackbar(view, R.string.loading_photo)
                photos.forEachIndexed { index, file ->
                    thread {
                        imageAddListener(index, FileInputStream(file).getBitmap(), file)
                    }
                }
            } else shortSnackbar(view, R.string.load_photo)
        }
    }

    private fun getFilesInMedia(lesson: Timetable.Lesson): Array<File>? =
        getDir(Environment.DIRECTORY_PICTURES).listFiles { _, s ->
            s.contains("${lesson.index}. $date ${lesson.subjects?.get(0)?.subject}")
        }


    private fun InputStream.getBitmap(): Bitmap = BitmapFactory.decodeStream(this).run {
        Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(90F) }, true)
    }

}