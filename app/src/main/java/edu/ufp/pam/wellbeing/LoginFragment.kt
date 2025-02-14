package edu.ufp.pam.wellbeing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.ufp.pam.wellbeing.databases.AppDatabase
import edu.ufp.pam.wellbeing.data.SessionManager // Import SessionManager
import edu.ufp.pam.wellbeing.databases.MainViewModel
import edu.ufp.pam.wellbeing.databases.MainViewModelFactory
import edu.ufp.pam.wellbeing.databases.WellBeingApp
import edu.ufp.pam.wellbeing.databinding.FragmentLoginBinding
import java.security.MessageDigest

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            val enteredUsername = binding.username.text.toString()
            val enteredPassword = binding.password.text.toString()

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(activity, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = hashPassword(enteredPassword)

            val mainViewModel = ViewModelProvider(
                this,
                MainViewModelFactory((requireActivity().application as WellBeingApp).appRepository)
            )[MainViewModel::class.java]

            val userLiveData = mainViewModel.findUserByUsername(enteredUsername)
            userLiveData.observe(viewLifecycleOwner) { user ->

                if (user != null && user.password == hashedPassword) {
                    val sessionManager = SessionManager(requireContext())
                    val keepLoggedIn = binding.keepLoggedInCheckbox.isChecked

                    if (keepLoggedIn) {
                        sessionManager.saveLoginSession(
                            enteredUsername,
                            user.id,
                            keepLoggedIn = true
                        )
                    } else {
                        sessionManager.saveLoginSession(
                            enteredUsername,
                            user.id,
                            keepLoggedIn = false
                        )
                    }

                    Toast.makeText(activity, "Login Successful! Redirecting...", Toast.LENGTH_SHORT)
                        .show()

                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    intent.putExtra("username", user.username)
                    intent.putExtra("userId", user.id)
                    startActivity(intent)

                    requireActivity().finish()
                } else {
                    Toast.makeText(activity, "Invalid email or password!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.signUpRedirect.setOnClickListener {
            (activity as MainActivity).loadFragment(SignupFragment())
        }

        return binding.root
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
