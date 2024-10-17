package com.dicoding.asclepius.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun formatPercent(value: Float): String {
        val percentage = value * 100
        val roundedPercentage = kotlin.math.round(percentage).toInt()
        return "$roundedPercentage%"
    }
    fun displayToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun formatCardDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return date?.let { outputFormat.format(it) } ?: ""
    }

    fun formatHistoryCardDate(timestamp: String): String {
        return try {
            val milliseconds = timestamp.toLong()
            val date = Date(milliseconds)
            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            sdf.format(date)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}