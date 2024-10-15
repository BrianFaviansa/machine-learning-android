package com.dicoding.asclepius.utils

import android.content.Context
import android.widget.Toast

object Utils {
    fun formatPercent(value: Float): String {
        val percentage = value * 100
        val roundedPercentage = kotlin.math.round(percentage).toInt()
        return "$roundedPercentage%"
    }
    fun displayToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}