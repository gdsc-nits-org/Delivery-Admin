package com.example.deliveryadmin.HomePageFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.adapters.dishAdapter
import com.example.deliveryadmin.data.dishDatasource


class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        val mydataset = dishDatasource().loadItems()
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.dishes_recycler_view)
        recyclerView.adapter = dishAdapter(mydataset)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        return rootView
    }


}