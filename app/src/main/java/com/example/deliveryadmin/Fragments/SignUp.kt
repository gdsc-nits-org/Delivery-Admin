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
import com.example.deliveryadmin.Utils.Client
import com.example.deliveryadmin.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navController: NavController
    private lateinit var databaseRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()

    }

    private fun init(view: View) {
        navController= Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance()
            .reference.child("Clients")
    }
    private fun registerEvents() {
        binding.backBtnSignUpPage.setOnClickListener {
            navController.navigate(R.id.action_signUpPage_to_signIn)
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.etNameSignUpPage.text.toString().trim()
            val email = binding.etEmailSignUpPage.text.toString().trim()
            val pass = binding.etPassSignUpPage.text.toString().trim()
            val repass = binding.etRePassSignUpPage.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty() ) {
                if (pass == repass) {

                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid

                            val clientModel = Client(name, email)
                            userId?.let {
                                databaseRef.child(it).setValue(clientModel)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                            navController.navigate(R.id.action_signUpPage_to_locationFragment)
                                        } else {
                                            Toast.makeText(context, "User could not be added", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(context, authTask.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill up all the necessary details", Toast.LENGTH_SHORT).show()
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