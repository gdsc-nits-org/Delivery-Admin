package com.example.deliveryadmin.Fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentAnimatedScreenBinding
import kotlin.concurrent.thread

class AnimatedScreen : Fragment() {

    private lateinit var binding: FragmentAnimatedScreenBinding
    private lateinit var navControl: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAnimatedScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControl= Navigation.findNavController(view)
        binding.ltAnimation.playAnimation()

        binding.myButton.visibility=View.GONE
        var isAnimating = true // Set it to true initially

        val handler = Handler(Looper.getMainLooper())

        thread {
            while (isAnimating) {
                if (binding.ltAnimation.progress >= 0.5f) {
                    handler.post {
                        binding.ltAnimation.pauseAnimation()
                        binding.myButton.visibility=View.VISIBLE
                    }

                    isAnimating = false
                }
                if (binding.ltAnimation.progress >= 1f) {
                    handler.post {
                        binding.ltAnimation.pauseAnimation()
                    }
                    isAnimating = false
                }
            }
        }


        // Set up the ObjectAnimator for translationY
        val translateY = ObjectAnimator.ofFloat(binding.myButton, "translationY", 0f, 0f)
        translateY.duration = 1000 // Adjust the duration as needed
        translateY.interpolator = AccelerateInterpolator()

        // Set up the visibility listener to make the button visible when the animation starts
        translateY.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {
                binding.myButton.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Animation ended, you can perform any additional actions here
            }

            override fun onAnimationCancel(animation: android.animation.Animator) {}

            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })

        // Start the animation
        translateY.start()

        // Set up a click listener for the button
        binding.myButton.setOnClickListener {
            binding.ltAnimation.resumeAnimation()
            binding.myButton.visibility=View.GONE
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                navControl.navigate(R.id.action_animatedScreen_to_signIn)
            },1000)
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Log.d("TAG", "Pressed...")
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}