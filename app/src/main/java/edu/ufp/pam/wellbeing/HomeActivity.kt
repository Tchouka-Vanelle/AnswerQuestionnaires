package edu.ufp.pam.wellbeing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ufp.pam.wellbeing.controllers.QuestionnairesAdapter
import edu.ufp.pam.wellbeing.data.SessionManager
import edu.ufp.pam.wellbeing.databases.MainViewModel
import edu.ufp.pam.wellbeing.databases.MainViewModelFactory
import edu.ufp.pam.wellbeing.databases.WellBeingApp
import edu.ufp.pam.wellbeing.databinding.ActivityHomeBinding
import edu.ufp.pam.wellbeing.util.functions.OnUserResponseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), OnUserResponseListener  {

    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityHomeBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionnairesAdapter
    private lateinit var mainViewModel: MainViewModel


    private val responses = mutableMapOf<Int, MutableMap<Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        lifecycleScope.launch {
            if (sessionManager.isSessionValid()) {
                    val username = sessionManager.getUsernameFromSession()


                    if (username != null) {
                        binding.welcomeText.text = "Welcome, $username!"

                        recyclerView = findViewById(R.id.recyclerViewQuestions)
                        recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)

                        mainViewModel = ViewModelProvider(
                            this@HomeActivity,
                            MainViewModelFactory((applicationContext as WellBeingApp).appRepository)
                        )[MainViewModel::class.java]

                        mainViewModel.questionnaireWithQuestions.observe(
                            this@HomeActivity,
                            Observer { questionnaireWithQuestions ->
                                if (questionnaireWithQuestions != null && questionnaireWithQuestions.isNotEmpty()) {
                                    Log.d(
                                        "HomeActivity",
                                        "questionnaireWithQuestions = $questionnaireWithQuestions"
                                    )
                                    //set up adapter
                                    adapter = QuestionnairesAdapter(
                                        questionnaireWithQuestions,
                                        this@HomeActivity
                                    )
                                    recyclerView.adapter = adapter
                                } else {
                                    Log.d("HomeActivity", "No questionnaires found.")
                                }
                            })

                        mainViewModel.fetchQuestionnaires()

                    } else {
                        redirectToLogin(false)
                    }
            } else {
                redirectToLogin(false)
            }

            binding.logoutButton.setOnClickListener {
                sessionManager.clearSession()
                redirectToLogin(true)
            }
        }
    }


    override fun onUserResponse(questionnaireId: Int, questionId: Int, answerId: Int) {

        responses.getOrPut(questionnaireId) {
            mutableMapOf()
        }[questionId] = answerId

    }

    override fun onUserSaveQuestionnaire(questionnaireId: Int) {

        mainViewModel.countAllQuestionsForQuestionnaire(questionnaireId) {

                totalQuestions ->
            val allQuestionsAnswered = responses[questionnaireId]?.size == totalQuestions

            if(allQuestionsAnswered) {
                lifecycleScope.launch {
                    responses[questionnaireId]?.forEach { (qId, aId) ->
                        mainViewModel.saveAnswer(
                            sessionManager.getUserIdFromSession(),
                            qId,
                            aId,
                            questionnaireId
                        ) { isSuccess ->
                            when (isSuccess) {
                                0 -> runOnUiThread {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "Your answer has been saved",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                1 -> runOnUiThread {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "You cannot answer the same question twice on the same day.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                2 -> runOnUiThread {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "An error occurred while saving your answer",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> runOnUiThread {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "An error occurred.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                    }
                }
            }else {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "You can't save without respond to all the questions!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun redirectToLogin(logout: Boolean) {
        if (logout)
            Toast.makeText(this, "Successfully logged out!", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Session expired, please log in again.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
