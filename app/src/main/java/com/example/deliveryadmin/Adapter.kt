package com.example.deliveryadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class Adapter(val onItemClicked: (String)->Unit, private val context : HomePageFragment, private val orderID: ArrayList<String>): RecyclerView
.Adapter<Adapter.OrdersViewHolder>(){

    private val orders : ArrayList<String> = orderID
    inner class OrdersViewHolder(itemView: View):ViewHolder(itemView)
    {
        var order: TextView = itemView.findViewById(R.id.orderID)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orders_list,parent,false)
        return OrdersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val currentItem = orders[position]
        holder.order.text = currentItem
        holder.itemView.setOnClickListener{
            onItemClicked(currentItem)
        }
    }

}