package com.example.deliveryadmin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth

class FragmentLogIn : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.tvSignupPage.setOnClickListener {
            navController.navigate(R.id.action_fragmentSignUp_to_fragmentLogIn)
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
            navController.navigate(R.id.action_fragmentLogIn_to_forgotPasswordFragment)
        }
        return binding.root
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                val intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }.addOnFailureListener {exception->
            Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}

