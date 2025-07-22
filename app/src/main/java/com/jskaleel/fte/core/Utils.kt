@file:Suppress("detekt:MagicNumber")

package com.jskaleel.fte.core

import android.content.Context
import android.content.Intent
import com.jskaleel.epub.reader.ReaderActivityContract
import java.io.File
import java.util.Calendar
import java.util.Date

typealias CallBack = () -> Unit
typealias StringCallBack = (String) -> Unit
typealias BooleanCallBack = (Boolean) -> Unit

fun getRelativeDateInTamil(timestamp: Long): String {
    val givenCalendar = Calendar.getInstance()
    givenCalendar.timeInMillis = timestamp
    val isSameDay = checkIsSameDay(givenCalendar)
    val isYesterday = checkIsYesterday(givenCalendar)
    return when {
        isSameDay -> "இன்று"
        isYesterday -> "நேற்று"
        else -> getRelativeDate(timestamp)
    }
}

private fun getRelativeDate(timestamp: Long): String {
    val diffInDays = getDaysDifference(timestamp)

    return when {
        diffInDays <= 0 -> "இன்று"
        diffInDays == 1L -> "நேற்று"
        diffInDays < 7 -> "$diffInDays நாள் முன்பு"
        diffInDays < 30 -> "${diffInDays / 7} வாரம் முன்பு"
        diffInDays < 365 -> "${diffInDays / 30} மாதம் முன்பு"
        else -> formatAbsoluteDate(timestamp)
    }
}

private fun getDaysDifference(timestamp: Long): Long {
    val now = System.currentTimeMillis()
    val diffInMillis = now - timestamp
    return diffInMillis / (24 * 60 * 60 * 1000)
}

fun checkIsSameDay(givenCalendar: Calendar): Boolean {
    val nowCalendar = Calendar.getInstance()
    val year = nowCalendar.get(Calendar.YEAR) == givenCalendar.get(Calendar.YEAR)
    val dayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR) == givenCalendar.get(Calendar.DAY_OF_YEAR)
    return year && dayOfYear
}

private fun checkIsYesterday(givenCalendar: Calendar): Boolean {
    val yesterday = Calendar.getInstance()
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    val year = yesterday.get(Calendar.YEAR) == givenCalendar.get(Calendar.YEAR)
    val dayOfYear = yesterday.get(Calendar.DAY_OF_YEAR) == givenCalendar.get(Calendar.DAY_OF_YEAR)
    return year && dayOfYear
}

private fun formatAbsoluteDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = date

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)

    val tamilMonth = getTamilMonth(month)
    return "$day $tamilMonth $year"
}

private fun getTamilMonth(month: Int): String {
    return when (month) {
        1 -> "ஜனவரி"
        2 -> "பிப்ரவரி"
        3 -> "மார்ச்"
        4 -> "ஏப்ரல்"
        5 -> "மே"
        6 -> "ஜூன்"
        7 -> "ஜூலை"
        8 -> "ஆகஸ்ட்"
        9 -> "செப்டம்பர்"
        10 -> "அக்டோபர்"
        11 -> "நவம்பர்"
        12 -> "டிசம்பர்"
        else -> ""
    }
}

fun Context.getDownloadDir(): File {
    return File(this.filesDir, "downloads").apply {
        if (!exists()) mkdirs()
    }
}

fun Context.launchReaderActivity(readerId: Long) {
    val intent = ReaderActivityContract().createIntent(
        context = this,
        input = ReaderActivityContract.Arguments(
            bookId = readerId
        )
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}
