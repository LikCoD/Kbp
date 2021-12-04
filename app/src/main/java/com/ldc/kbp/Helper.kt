package com.ldc.kbp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.ldc.kbp.models.*
import com.ozcanalasalvar.library.view.datePicker.DatePicker
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import org.joda.time.DateTime
import org.joda.time.Weeks
import org.threeten.bp.LocalDate
import java.io.File
import kotlin.math.abs

var config = Config()
var homeworkList = Homeworks()
lateinit var mainSchedule: Schedule

fun dimen(resources: Resources, dimen: Int) = resources.getDimension(dimen).toInt()

fun checkPermission(permission: String, activity: Activity, func: () -> Unit) =
    if (
        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
    ) {
        shortToast(activity, R.string.allow_storage)

        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0
        )
    } else func()

fun getFile(dir: String, name: String, extension: String) = File(getDir(dir), "/$name.$extension")

fun getDir(dir: String) =
    File(Deprecates.getStorageDir(dir), "/Kbp").apply { if (!exists()) mkdirs() }

fun getAssetsBytes(context: Context, name: String) =
    context.assets.open(name).readBytes()

fun getUrlViaProvider(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "com.ldc.kbp.file.provider", file)

fun shortToast(context: Context, text: Int) =
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

fun shortSnackbar(view: View, text: Int) = Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()

fun normalizeDate(date: Int) = if (date < 10) "0$date" else date

fun LocalDate.getString() =
    "${normalizeDate(dayOfMonth)}.${normalizeDate(monthValue)}.${normalizeDate(year)}"

fun getCurrentWeek(
    weekCount: Int = mainSchedule.info.weeksCount - 1 ,
    date: LocalDate = LocalDate.now()
): Int {
    val nowDate = LocalDate.now()
    var septemberStartDate = LocalDate.of(
        if (nowDate.monthValue in 0 until 9) nowDate.year - 1 else nowDate.year, 9, 1
    )

    septemberStartDate = septemberStartDate.minusDays(septemberStartDate.dayOfWeek.value.toLong())

    val septemberStartDateTime = DateTime.parse(septemberStartDate.toString())
    val dateTime = DateTime.parse(date.toString())

    val weekBetween = Weeks.weeksBetween(septemberStartDateTime, dateTime).weeks

    return abs(weekBetween) % weekCount
}

fun createDatePicker(context: Context, listener: (LocalDate) -> Unit): DatePickerPopup =
    DatePickerPopup.Builder()
        .from(context)
        .pickerMode(DatePicker.MONTH_ON_FIRST)
        .listener { _, _, day, month, year ->
            listener(LocalDate.of(year, month + 1, day))
        }.build()

fun ifFemale(t: String = "", f: String = "") = if (config.isFemale) t else f

fun View.disableActions() = setOnTouchListener { view, _ ->
    view.performClick()

    true
}

fun BottomSheetBehavior<*>.onStateChanged(onStateChange: (View, Int) -> Unit) {
    addBottomSheetCallback(
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) =
                onStateChange(bottomSheet, newState)

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
    )
}