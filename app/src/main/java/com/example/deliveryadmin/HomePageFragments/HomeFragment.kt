package com.example.deliveryadmin.HomePageFragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
//        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Check for notification permissions
//        checkNotificationPermission()

        return binding.root
    }
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         checkNotificationPermission()
         binding.ivPending.setOnClickListener {
             Log.d("HomeFragment", "Pending Orders Card Clicked")
             try{
                 findNavController().navigate(R.id.action_homeFragment_to_ordersPendingFragment)
                 }catch (e: Exception){
                 e.printStackTrace()
             }
         }
         binding.ivDelivered.setOnClickListener {
             Log.d("HomeFragment", "Delivered Orders Card Clicked")
             try {
                 findNavController().navigate(R.id.action_homeFragment_to_ordersCompletedFragment)
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }


     }
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context?.let {
                if (ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    showPermissionDialog()
                }
            }
        }
    }

    private fun showPermissionDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Notification Permission")
                .setMessage("This app needs notification permissions to send you updates.")
                .setPositiveButton("Allow") { _, _ ->
                    requestNotificationPermission()
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with notification setup
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
}
