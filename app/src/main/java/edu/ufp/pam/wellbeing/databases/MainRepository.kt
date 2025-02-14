package edu.ufp.pam.wellbeing.databases

import edu.ufp.pam.wellbeing.databases.entities.Answer
import edu.ufp.pam.wellbeing.databases.entities.Question
import edu.ufp.pam.wellbeing.databases.entities.Questionnaire
import edu.ufp.pam.wellbeing.databases.entities.User
import edu.ufp.pam.wellbeing.databases.entitiesdao.AnswerDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.QuestionDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.QuestionnaireDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.UserDao
import edu.ufp.pam.wellbeing.util.functions.isSameDay
import java.sql.Date

class MainRepository(
    private val userDao: UserDao,
    private val questionnaireDao: QuestionnaireDao,
    private val questionDao: QuestionDao,
    private val answerDao: AnswerDao
) {
    suspend fun insertUser(user: User): Boolean {
        val result = userDao.insertAll(user)
        return result.isNotEmpty() && result[0] > 0 // Check if the ID is valid
    }

    suspend fun findByUsername(username: String): User? =
        userDao.findByUsername(username)

    suspend fun findByEmail(email: String): User? =
        userDao.findByEmail(email)

    suspend fun insertQuestionnaires(vararg questionnaires: Questionnaire) =
        questionnaireDao.insertQuestionnaires(*questionnaires)

    suspend fun insertQuestions(vararg questions: Question) =
        questionDao.insertQuestions(*questions)

    suspend fun insertAnswer(
        userId: Int, questionId: Int, answer: Int, questionnaireId: Int
    ): Int {
        val currentDate = Date(System.currentTimeMillis())//2024-12-11

        if( !questionnaireDao.isMultipleAnswerInDay(questionnaireId) ) {

            val lastDate: Date? = answerDao.getLastDateAnswer(userId, questionId)

            if (lastDate != null) {
                val isSameDay = isSameDay(lastDate, currentDate)

                if (isSameDay) {
                    return 1
                }
            }
        }

        // Create AnswerQuestionnaire object
        val answerFinal = Answer(
            questionId = questionId,
            userId = userId,
            answer = answer,
            date = currentDate // Store the formatted date-time
        )
        val rowId = answerDao.insertAnswer(answerFinal)

        return if(rowId > 0)
             0 //success
        else
             2

    }


    suspend fun getAllQuestionnaires() =
        questionnaireDao.getAllQuestionnaires()

    suspend fun getAllQuestionsForQuestionnaire(id: Int) =
        questionDao.getAllQuestionsForQuestionnaire(id)

    suspend fun countAllQuestionsForQuestionnaire(id: Int) =
        questionDao.countAllQuestionsForQuestionnaire(id)

    suspend fun isMultipleAnswerInDay(id: Int) =
        questionnaireDao.isMultipleAnswerInDay(id)
}
