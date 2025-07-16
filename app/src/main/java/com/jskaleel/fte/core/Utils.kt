package com.jskaleel.fte.core

import android.content.Context
import android.content.Intent
import com.jskaleel.epub.reader.ReaderActivityContract
import java.io.File
import java.util.Calendar
import java.util.Date

typealias CallBack = () -> Unit
typealias StringCallBack = (String) -> Unit
typealias LongCallBack = (Long) -> Unit
typealias BooleanCallBack = (Boolean) -> Unit

fun getDetailedRelativeDateInTamil(timestamp: Long): String {
    val now = System.currentTimeMillis() / 1000
    val givenTime = timestamp

    val nowCalendar = Calendar.getInstance()
    val givenCalendar = Calendar.getInstance()
    givenCalendar.timeInMillis = timestamp * 1000

    val isSameDay = nowCalendar.get(Calendar.YEAR) == givenCalendar.get(Calendar.YEAR) &&
            nowCalendar.get(Calendar.DAY_OF_YEAR) == givenCalendar.get(Calendar.DAY_OF_YEAR)

    val yesterdayCalendar = Calendar.getInstance()
    yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
    val isYesterday = yesterdayCalendar.get(Calendar.YEAR) == givenCalendar.get(Calendar.YEAR) &&
            yesterdayCalendar.get(Calendar.DAY_OF_YEAR) == givenCalendar.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameDay -> "இன்று"
        isYesterday -> "நேற்று"
        else -> {
            val diffInSeconds = now - givenTime
            val diffInDays = diffInSeconds / (24 * 60 * 60)

            when (diffInDays) {
                in 2..6 -> "$diffInDays நாள் முன்பு"
                in 7..13 -> "1 வாரம் முன்பு"
                in 14..20 -> "2 வாரம் முன்பு"
                in 21..29 -> "3 வாரம் முன்பு"
                in 30..59 -> "1 மாதம் முன்பு"
                in 60..89 -> "2 மாதம் முன்பு"
                in 90..119 -> "3 மாதம் முன்பு"
                in 120..149 -> "4 மாதம் முன்பு"
                in 150..179 -> "5 மாதம் முன்பு"
                in 180..209 -> "6 மாதம் முன்பு"
                in 210..239 -> "7 மாதம் முன்பு"
                in 240..269 -> "8 மாதம் முன்பு"
                in 270..299 -> "9 மாதம் முன்பு"
                in 300..329 -> "10 மாதம் முன்பு"
                in 330..364 -> "11 மாதம் முன்பு"
                else -> formatAbsoluteDate(timestamp)
            }
        }
    }
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

fun Context.launchReaderActivity(bookId: Long) {
    val intent = ReaderActivityContract().createIntent(
        this,
        ReaderActivityContract.Arguments(
            bookId = bookId
        )
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}