package edu.ufp.pam.wellbeing.databases

import edu.ufp.pam.wellbeing.databases.entities.Question
import edu.ufp.pam.wellbeing.databases.entities.Questionnaire

data class QuestionnaireWithQuestions(
    val questionnaire: Questionnaire,
    val questions: List<Question>
)
