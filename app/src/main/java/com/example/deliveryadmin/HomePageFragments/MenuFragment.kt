package com.example.deliveryadmin.HomePageFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.adapters.DishAdapter
import com.example.deliveryadmin.data.dishDatasource
import com.example.deliveryadmin.menu.AddItem
import com.example.deliveryadmin.models.dishDataModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MenuFragment : Fragment() {

    private lateinit var mydataset: List<dishDataModel>
    private lateinit var userId: String // Add userId variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty() // Get the current user's ID

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.dishes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val dishDataSource = dishDatasource()
        dishDataSource.loadItems { data ->
            if (isAdded) {
                mydataset = data // Store data in the property
                val adapter = DishAdapter(data, requireContext(), userId) // Pass userId to the adapter
                recyclerView.adapter = adapter
            }
        }

        // Find the FAB and set an OnClickListener
        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Inside your RecyclerView adapter's click listener
            val intent = Intent(activity, AddItem::class.java)
            startActivity(intent)
        }

        return rootView
    }
}
