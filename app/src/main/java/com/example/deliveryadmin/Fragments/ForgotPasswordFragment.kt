package com.example.deliveryadmin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnsendemail.setOnClickListener {
            validateData()
        }
        binding.backBtnLoginPage.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack();
        }

    }

    private fun validateData() {
        val email = binding.etEmailForgotpassword.text.toString()
        if(email.isEmpty()){
            binding.etEmailForgotpassword.error = "Required"
        }
        else{
            forgotpassword(email)
        }

    }

    private fun forgotpassword(email:String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { Task ->
                if (Task.isSuccessful) {
                    Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginPage)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

}
