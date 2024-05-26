package com.example.deliveryadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.models.dishDataModel

class dishAdapter (private val dataset: List<dishDataModel>) : RecyclerView.Adapter<dishAdapter.ItemViewHolder>(){
    class ItemViewHolder (private val view: View): RecyclerView.ViewHolder(view) {
        val dishItemName: TextView = view.findViewById(R.id.dishItemName)
        val dishItemPrice: TextView = view.findViewById(R.id.dishItemPrice)
        val dishItemIngredients: TextView = view.findViewById(R.id.dishItemIngredients)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dish_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val dish = dataset[position]

        holder.dishItemName.text = dish.dishName
        holder.dishItemPrice.text = dish.price.toString()
        holder.dishItemIngredients.text = dish.ingredients
    }
}