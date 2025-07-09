package com.example.deliveryadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.R
// Assuming you're using View Binding for orders_item.xml, which generates OrdersItemBinding
import com.example.deliveryadmin.databinding.OrdersItemBinding


class OrdersPendingAdapter(
    private val orderIds: ArrayList<String>,
    private val timelefts: ArrayList<String>,
    private val deliveryStatuses: ArrayList<String>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<OrdersPendingAdapter.OrdersPendingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersPendingViewHolder {
        // Correct way to inflate the layout using View Binding
        val binding = OrdersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersPendingViewHolder(binding, onItemClick)
    }


    override fun onBindViewHolder(holder: OrdersPendingViewHolder, position: Int) {

        val currentOrderId = orderIds[position]
        val currentTimeLeft = timelefts[position]
        val currentDeliveryStatus = deliveryStatuses[position]
        holder.tvTimeLeft.text = currentTimeLeft
        holder.orderID.text = currentOrderId
    }

    override fun getItemCount(): Int = orderIds.size

    inner class OrdersPendingViewHolder(private val binding: OrdersItemBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        // Directly access views using the binding object
        val tvTimeLeft: TextView = binding.tvTimeLeft
        val orderID: TextView = binding.orderID

        init {
            // Set a click listener on the entire item's root view (the CardView in your case)
            binding.root.setOnClickListener {
                // Get the position of the clicked item
                val position = adapterPosition
                // Check for valid position to avoid crashes if item is removed during click
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position) // Invoke the lambda function with the clicked position
                }
            }
            // Add references to any other TextViews or views from orders_item.xml that you want to populate.
            // For example:
            // val tvAllDetails: TextView = binding.tvAllDetails
            // val tvDeliveryStatus: TextView = binding.yourDeliveryStatusTextViewId // Replace with actual ID
        }
    }

    // You might want a way to update the data if it changes
    fun updateData(newOrderIds: ArrayList<String>, newTimeLefts: ArrayList<String>, newDeliveryStatuses: ArrayList<String>) {
        this.orderIds.clear()
        this.orderIds.addAll(newOrderIds)

        this.timelefts.clear()
        this.timelefts.addAll(newTimeLefts)

        this.deliveryStatuses.clear()
        this.deliveryStatuses.addAll(newDeliveryStatuses)

        notifyDataSetChanged() // Notify the adapter that the entire dataset has changed
    }
}