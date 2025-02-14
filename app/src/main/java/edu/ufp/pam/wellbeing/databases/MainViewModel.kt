package edu.ufp.pam.wellbeing.databases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.ufp.pam.wellbeing.databases.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    val questionnaireWithQuestions = MutableLiveData<List<QuestionnaireWithQuestions>>()

    fun findUserByUsername(username: String): LiveData<User?> {
        val user = MutableLiveData<User?>()
        viewModelScope.launch {
            user.postValue(repository.findByUsername(username))
        }
        return user
    }

    fun findUserByEmail(email: String): LiveData<User?> {
        val user = MutableLiveData<User?>()
        viewModelScope.launch {
            user.postValue(repository.findByEmail(email))
        }
        return user
    }

    fun insertUser(user: User): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val isSuccess = repository.insertUser(user)
            result.postValue(isSuccess)
        }
        return result
    }


    fun fetchQuestionnaires() {
        viewModelScope.launch(Dispatchers.IO) {
            val questionnaires = repository.getAllQuestionnaires()
            Log.d("MainViewModel", "Fetched questionnaires: $questionnaires")

            val questionnairesWithQuestions = mutableListOf<QuestionnaireWithQuestions>()
            for (questionnaire in questionnaires) {
                val questions = repository.getAllQuestionsForQuestionnaire(questionnaire.id)
                Log.d("MainViewModel", "Questions for questionnaire ${questionnaire.id}: $questions")
                questionnairesWithQuestions.add(QuestionnaireWithQuestions(questionnaire, questions))
            }

            if (questionnairesWithQuestions.isEmpty()) {
                Log.d("MainViewModel", "No questionnaires with questions found")
            }

            questionnaireWithQuestions.postValue(questionnairesWithQuestions)
        }
    }


    fun saveAnswer(userId: Int, questionId: Int, typeAnswerId: Int, questionnaireId: Int, onResult: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            onResult(repository.insertAnswer(userId, questionId, typeAnswerId, questionnaireId))
        }
    }


    fun countAllQuestionsForQuestionnaire(questionnaireId: Int, onResult: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.countAllQuestionsForQuestionnaire(questionnaireId)
            onResult(result)
        }
    }

}

class MainViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}