package edu.ufp.pam.wellbeing.databases.entitiesdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ufp.pam.wellbeing.databases.entities.Questionnaire

@Dao
interface QuestionnaireDao {
    @Insert
    suspend fun insertQuestionnaires(vararg questionnaires: Questionnaire): List<Long>

    @Query("SELECT * FROM questionnaire")
    suspend fun getAllQuestionnaires(): List<Questionnaire>

    @Query("SELECT is_multiple_answer_in_day FROM questionnaire WHERE id = :id")
    suspend fun isMultipleAnswerInDay(id: Int): Boolean
}