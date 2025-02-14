package edu.ufp.pam.wellbeing.databases.entitiesdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ufp.pam.wellbeing.databases.entities.Question

@Dao
interface QuestionDao {
    @Insert
    suspend fun insertQuestions(vararg  questions: Question): List<Long>

    @Query("SELECT * FROM question WHERE questionnaire_id = :questionnaireId")
    suspend fun getAllQuestionsForQuestionnaire(questionnaireId: Int): List<Question>

    @Query("SELECT count(*) FROM question WHERE questionnaire_id = :questionnaireId")
    suspend fun countAllQuestionsForQuestionnaire(questionnaireId: Int): Int
}