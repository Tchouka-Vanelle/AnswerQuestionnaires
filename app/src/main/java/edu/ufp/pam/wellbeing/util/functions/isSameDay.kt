package edu.ufp.pam.wellbeing.util.functions

import java.util.Date

fun isSameDay(date1: Date, date2: Date): Boolean {
    val calendar1 = java.util.Calendar.getInstance().apply { time = date1 }
    val calendar2 = java.util.Calendar.getInstance().apply { time = date2 }

    return calendar1.get(java.util.Calendar.YEAR) == calendar2.get(java.util.Calendar.YEAR) &&
            calendar1.get(java.util.Calendar.DAY_OF_YEAR) == calendar2.get(java.util.Calendar.DAY_OF_YEAR)
}