package edu.ufp.pam.wellbeing.databases.entitiesdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.ufp.pam.wellbeing.databases.entities.Answer
import java.sql.Date

@Dao
interface AnswerDao {
    @Insert
    suspend fun insertAnswer(answer: Answer):Long

    @Query("SELECT * FROM answer WHERE user_id = :userId")
    suspend fun getAnswersForUser(userId: Int): List<Answer>

    @Query("SELECT date FROM answer " +
            "WHERE user_id = :userId AND question_id = :questionId " +
            "ORDER BY date DESC LIMIT 1")
    suspend fun getLastDateAnswer(userId: Int, questionId: Int): Date?
}