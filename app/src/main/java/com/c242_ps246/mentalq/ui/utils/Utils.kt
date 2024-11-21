package com.c242_ps246.mentalq.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())

            if (dateString.contains("T")) {
                val instant = Instant.parse(dateString)
                formatter.format(instant)
            } else {
                val localDate =
                    LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            }
        } catch (e: Exception) {
            dateString
        }
    }
}