package edu.ufp.pam.wellbeing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.ufp.pam.wellbeing.databases.AppDatabase
import edu.ufp.pam.wellbeing.databases.MainViewModel
import edu.ufp.pam.wellbeing.databases.MainViewModelFactory
import edu.ufp.pam.wellbeing.databases.WellBeingApp
import edu.ufp.pam.wellbeing.databases.entities.User
import edu.ufp.pam.wellbeing.databinding.FragmentSignupBinding
import java.security.MessageDigest

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        binding.signUpButton.setOnClickListener {
            val enteredUsername = binding.username.text.toString()
            val enteredEmail = binding.email.text.toString()
            val enteredPassword = binding.password.text.toString()

            if (enteredUsername.isEmpty() || enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(activity, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = hashPassword(enteredPassword)

            val mainViewModel = ViewModelProvider(
                this,
                MainViewModelFactory((requireActivity().application as WellBeingApp).appRepository)
            )[MainViewModel::class.java]

            mainViewModel.findUserByEmail(enteredEmail).observe(viewLifecycleOwner) { existingUser ->
                if (existingUser != null) {
                    Toast.makeText(activity, "Email is already registered!", Toast.LENGTH_SHORT).show()
                    //it stops further execution of the block inside observe
                    return@observe
                }else {
                    mainViewModel.findUserByUsername(enteredUsername)
                        .observe(viewLifecycleOwner) { existingUsername ->
                            if (existingUsername != null) {
                                Toast.makeText(
                                    activity,
                                    "Username is already registered!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //it stops further execution of the block inside observe
                                return@observe
                            }else {

                                val newUser = User(
                                    id = 0,
                                    username = enteredUsername,
                                    email = enteredEmail,
                                    password = hashedPassword
                                )

                                mainViewModel.insertUser(newUser).observe(viewLifecycleOwner) { isInserted ->
                                    if (isInserted) {
                                        Toast.makeText(activity, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                                        (activity as MainActivity).loadFragment(LoginFragment())
                                    } else {
                                        Toast.makeText(activity, "Sign Up Failed. Try Again!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                }
            }

        }

        binding.loginRedirect.setOnClickListener {
            (activity as MainActivity).loadFragment(LoginFragment())
        }

        return binding.root
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}