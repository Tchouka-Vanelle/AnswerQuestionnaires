package edu.ufp.pam.wellbeing.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "question",
    foreignKeys = [ForeignKey(
        entity = Questionnaire::class,
        parentColumns = ["id"],
        childColumns = ["questionnaire_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["questionnaire_id"])]
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "questionnaire_id") val questionnaireId: Int,
    val title: String
)
