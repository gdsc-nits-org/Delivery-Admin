package com.example.deliveryadmin.HomePageFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.deliveryadmin.R
import com.example.deliveryadmin.databinding.FragmentOrdersCompletedBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrdersCompletedFragment : Fragment() {
    private var _binding:
            FragmentOrdersCompletedBinding? = null
    private val binding get()= _binding!!
    private val TAG = "OrdersCompletedFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentOrdersCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backArrow.setOnClickListener{
           Log.d(TAG, "backArrow clicked")
            try {
               val success = findNavController().navigateUp()
                Log.d(TAG, "Navigated to HomeFragment")
            }catch(e: Exception){
                Log.e(TAG, "Error navigating to HomeFragment: ${e.message}",e)

            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}