package com.example.deliveryadmin.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth


class SplashScreen : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sideAnimation=android.view.animation.AnimationUtils.loadAnimation(parentFragment?.context,R.anim.slide)
        binding.ivIcon.startAnimation(sideAnimation)

        auth=FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        Handler(Looper.myLooper()!!).postDelayed(Runnable{
            if (auth.currentUser!=null){
                navController.navigate(R.id.action_splashScreen_to_emptyActivity)
            }else{
                navController.navigate(R.id.action_splashScreen_to_accessLocation)
            }
        },3000)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Log.d("TAG", "Pressed...")
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}