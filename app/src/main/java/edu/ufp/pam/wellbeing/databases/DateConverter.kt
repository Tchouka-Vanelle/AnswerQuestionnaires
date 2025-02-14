package edu.ufp.pam.wellbeing.databases

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.sql.Date

@TypeConverters
class DateConverter {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
