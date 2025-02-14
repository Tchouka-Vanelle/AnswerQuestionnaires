package edu.ufp.pam.wellbeing.util.functions

interface OnUserResponseListener {
    fun onUserResponse(questionnaireId: Int, questionId: Int, answerId: Int)
    fun onUserSaveQuestionnaire(questionnaireId: Int)
}