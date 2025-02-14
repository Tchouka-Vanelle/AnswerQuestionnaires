package edu.ufp.pam.wellbeing.databases

import edu.ufp.pam.wellbeing.databases.entities.Question
import edu.ufp.pam.wellbeing.databases.entities.Questionnaire
import edu.ufp.pam.wellbeing.databases.entitiesdao.QuestionDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.QuestionnaireDao
import kotlinx.coroutines.runBlocking

object DatabaseSeeder {

    fun preFillDatabase(
        questionnaireDao: QuestionnaireDao,
        questionDao: QuestionDao,
    ) {
        try {
            runBlocking {
                questionnaireDao.insertQuestionnaires(
                    Questionnaire(1, "SLEEP", false),
                    Questionnaire(2, "WELLBEING1", false),
                    Questionnaire(3, "WELLBEING2", true)
                )

                questionDao.insertQuestions(
                    Question(1, 1, "I slept very well and feel that my sleep was totally restorative."),
                    Question(2, 1, "I feel totally rested after this night's sleep."),
                    Question(3, 2, "I related easily to the people around me."),
                    Question(4, 2, "I was able to face difficult situations in a positive way."),
                    Question(5, 2, "I felt that others liked me and appreciated me."),
                    Question(6, 2, "I felt satisfied with what I was able to achieve, I felt proud of myself."),
                    Question(7, 2, "My life was well balanced between my family, personal and academic activities."),
                    Question(8, 3, "I felt emotionally balanced."),
                    Question(9, 3, "I felt good, at peace with myself"),
                    Question(10, 3, "I felt confident.")
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
