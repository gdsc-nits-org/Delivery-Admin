package com.example.deliveryadmin.Fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.R
import com.example.deliveryadmin.Utils.FirebaseManager
import com.example.deliveryadmin.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUp : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.backBtnSignUpPage.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener {
            if (checkAllFields()) {
                auth = FirebaseManager.getFirebaseAuth()
                database = FirebaseManager.getFirebaseDatabase()

                val email = binding.etEmailSignUpPage.text.toString().trim()
                val password = binding.etPassSignUpPage.text.toString().trim()

                signUp(email, password)
            }
        }

        binding.tvLoginPage.setOnClickListener {
            navController.navigate(R.id.action_signUpPage_to_loginPage)
        }

        return binding.root
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_signUpPage_to_locationFragment)
            } else {
                Toast.makeText(
                    context,
                    "Your account does not exist!\n SignUp first!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkAllFields(): Boolean {
        val email = binding.etEmailSignUpPage.text.toString()
        if (email == "") {
            binding.etEmailSignUpPage.error = "This is required field"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailSignUpPage.error = "Please enter a valid Email"
            return false
        }

        if (binding.etPassSignUpPage.text.toString() == "") {
            binding.etPassSignUpPage.error = "This is required field"
            return false
        }

        if (binding.etRePassSignUpPage.text.toString() == "") {
            binding.etRePassSignUpPage.error = "This is required field"
            return false
        }

        if (binding.etPassSignUpPage.text.toString() != binding.etRePassSignUpPage.text.toString()) {
            binding.etPassSignUpPage.error = "Password do not match"
            return false
        }
        return true
    }

}