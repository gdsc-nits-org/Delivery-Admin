package com.example.deliveryadmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {
    private lateinit var adapter: Adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var orders : ArrayList<String>
    private lateinit var binding: FragmentHomePageBinding
    private lateinit var navController: NavController



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        getData()
        if(orders.isNotEmpty())
        {
            binding.imgBackground.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
        }
    }

    private fun getData() {
        orders = arrayListOf()
    }

    private fun onItemClicked(orderID: String){

    }

}