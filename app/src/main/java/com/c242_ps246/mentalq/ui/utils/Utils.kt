package com.c242_ps246.mentalq.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale

object Utils {
    fun getTodayDate(): String {
        val date = Calendar.getInstance().time
        return date.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        return try {
            val instant = Instant.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            dateString
        }
    }
}