package com.example.cah_cinema.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    fun formatDateTime(isoString: String?): String {
        if (isoString == null) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(isoString)
            if (date != null) outputFormat.format(date) else isoString
        } catch (e: Exception) {
            isoString
        }
    }

    fun formatApiDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getTodayDay(): String = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
    fun getTodayMonth(): String = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
    fun getTodayYear(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
}
