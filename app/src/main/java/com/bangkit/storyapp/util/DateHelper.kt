package com.bangkit.storyapp.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateHelper {
    fun convertDateFormat(inputDate: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val date = LocalDate.parse(inputDate, inputFormat)
        val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        return date.format(outputFormat)
    }
}