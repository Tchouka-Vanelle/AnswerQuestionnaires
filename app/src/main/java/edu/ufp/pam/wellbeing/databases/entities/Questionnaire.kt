package edu.ufp.pam.wellbeing.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questionnaire")
data class Questionnaire(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "is_multiple_answer_in_day") val isMultipleAnswerInDay: Boolean
)
