package com.ldc.kbp.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.ldc.kbp.models.Homeworks
import com.ldc.kbp.R
import com.ldc.kbp.models.Schedule
import kotlinx.android.synthetic.main.dialog_homework_set.*
/*

class HomeworkSetDialog(
    context: Context,
    private val subject: Schedule.Subject,
    private val homework: Homeworks.Homework?,
    var onHomeworkAccept: (Homeworks.Homework) -> Unit = {}
) : Dialog(context, R.style.dialog_homework_set) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_homework_set)

        window!!.attributes.windowAnimations = R.style.dialog_animation

        dialog_homework_subject_tv.text = subject.subject
        dialog_homework_room_tv.text = subject.room
        dialog_homework_teacher_tv.text = subject.teacher
        dialog_homework_group_tv.text = subject.group

        dialog_homework_text.setText(homework?.homework)

        dialog_homework_accept.setOnClickListener {
            onHomeworkAccept(Homeworks.Homework(dialog_homework_text.text.toString(), subject))

            hide()
        }

        dialog_homework_discard.setOnClickListener { hide() }
    }

}*/
