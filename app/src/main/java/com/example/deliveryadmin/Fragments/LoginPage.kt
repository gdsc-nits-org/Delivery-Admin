package com.example.deliveryadmin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginPage : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.backBtnLoginPage.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack();
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginPageBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.tvSignupPage.setOnClickListener {
            navController.navigate(R.id.action_loginPage_to_signUpPage)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLoginPage.text.toString()
            val password = binding.etPassLoginPage.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() )
                login(email, password)
            else
            {
                Toast.makeText(requireContext(), "Enter Your Details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvForgotPass.setOnClickListener {
            navController.navigate(R.id.action_loginPage_to_forgotPasswordFragment)
        }
        return binding.root
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                Toast.makeText(context,"Login Successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_loginPage_to_locationFragment)
            }
        }.addOnFailureListener {exception->
            Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}