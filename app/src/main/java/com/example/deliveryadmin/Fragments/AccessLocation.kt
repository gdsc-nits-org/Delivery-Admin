package com.example.deliveryadmin.Fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentAccessLocationBinding

class AccessLocation : Fragment() {
    private lateinit var binding: FragmentAccessLocationBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccessLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnAccessLocation.setOnClickListener {
            if (checkLocationPermission()) {
                // Permission granted, navigate to the next screen
                navController.navigate(R.id.action_accessLocation_to_animatedScreen)
            }

        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or handle as needed
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Check for permission on fragment start

    }

    private fun checkLocationPermission(): Boolean {
        val context = requireContext()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user if they have denied permission previously
                AlertDialog.Builder(context)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission to function properly. Please allow.")
                    .setPositiveButton("OK") { _, _ ->
                        requestLocationPermission()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, request the permission
                requestLocationPermission()
            }
            return false
        }
        // Permission already granted
        return true
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
