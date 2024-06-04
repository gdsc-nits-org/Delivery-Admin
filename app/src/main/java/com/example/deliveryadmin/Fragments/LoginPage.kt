package com.example.deliveryadmin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentLoginPageBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginPage : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Log.d("TAG", "Pressed...")
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun init(view: View) {

        navController= Navigation.findNavController(view)
        auth= FirebaseAuth.getInstance()
    }

    private fun registerEvents() {
        binding.tvLoginPage.setOnClickListener {
            navController.navigate(R.id.action_loginPage_to_signUpPage)
        }
        binding.backBtnLoginPage.setOnClickListener {
            navController.navigate(R.id.action_loginPage_to_signIn)
        }
        binding.btnLogin.setOnClickListener {
            val email=binding.etEmailLoginPage.text.toString().trim()
            val pass=binding.etPassLoginPage.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()){

                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(context,"Login Successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_loginPage_to_locationFragment)

                        }else{
                            Toast.makeText(context,"Your account does not exist!\n SignUp first!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

            }

        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Log.d("TAG", "Pressed...")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}