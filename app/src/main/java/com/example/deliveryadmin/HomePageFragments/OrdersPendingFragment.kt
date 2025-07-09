package com.example.deliveryadmin.HomePageFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.deliveryadmin.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController

// IMPORTANT: Import your adapter and data class
import com.example.deliveryadmin.adapters.OrdersPendingAdapter
import com.example.deliveryadmin.data.OrderItem // Make sure this path is correct for your OrderItem class
import com.example.deliveryadmin.databinding.FragmentOrdersPendingBinding // Assuming you use ViewBinding

class OrdersPendingFragment : Fragment() {

    // Using View Binding to access views safely
    private var _binding: FragmentOrdersPendingBinding? = null
    private val binding get() = _binding!!

    // Declare your adapter and data lists
    private lateinit var ordersAdapter: OrdersPendingAdapter

    // IMPORTANT: These ArrayLists will hold the data you want to display.
    // They must be populated.
    private val orderIdsList = ArrayList<String>()
    private val timeLeftList = ArrayList<String>()
    private val deliveryStatusList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentOrdersPendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrow.setOnClickListener{
            findNavController().navigateUp()
        }

        // 1. Load your order data into the ArrayLists
        loadOrdersData()

        // 2. Setup the RecyclerView (LayoutManager and Adapter)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with your populated data lists
        ordersAdapter = OrdersPendingAdapter(orderIdsList, timeLeftList, deliveryStatusList){
            position -> val clickedOrderId = orderIdsList[position]
            Toast.makeText(context, "Order ID: $clickedOrderId", Toast.LENGTH_SHORT).show()
        }

        // Set the LayoutManager for the RecyclerView
        // LinearLayoutManager will arrange items in a vertical list (like a scrollable column)
        binding.rvPending.layoutManager = LinearLayoutManager(requireContext())

        // Set the adapter to your RecyclerView
        binding.rvPending.adapter = ordersAdapter
    }

    private fun loadOrdersData() {
        // --- THIS IS WHERE YOU'LL GET YOUR REAL DATA ---
        // For demonstration, let's add some dummy data to your lists:
        orderIdsList.add("ME20002977")
        timeLeftList.add("30 mins left")
        deliveryStatusList.add("Driver Incoming")

        orderIdsList.add("ME20002978")
        timeLeftList.add("15 mins left")
        deliveryStatusList.add("Preparing Order")

        orderIdsList.add("ME20002979")
        timeLeftList.add("5 mins left")
        deliveryStatusList.add("Out for Delivery")

        orderIdsList.add("ME20002980")
        timeLeftList.add("Driver has arrived")
        deliveryStatusList.add("Completed")

        orderIdsList.add("ME20002981")
        timeLeftList.add("1 hour left")
        deliveryStatusList.add("Order Placed")

        // IMPORTANT: If you load data asynchronously (e.g., from Firebase, an API call),
        // you would call ordersAdapter.notifyDataSetChanged() *after* the data
        // has successfully been added to the lists.
        // For this synchronous dummy data, it's fine as is, but it's good practice.
        if (::ordersAdapter.isInitialized) { // Check if adapter is initialized before notifying
            ordersAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding when the view is destroyed
    }
}