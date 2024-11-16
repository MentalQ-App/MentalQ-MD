package com.c242_ps246.mentalq.ui.utils

import java.text.SimpleDateFormat
import java.util.Calendar

object Utils {
    fun getTodayDate(): String {
        val date = Calendar.getInstance().time
        return date.toString()
    }
}