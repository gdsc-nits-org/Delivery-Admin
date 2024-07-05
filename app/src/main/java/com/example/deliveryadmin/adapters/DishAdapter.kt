package com.example.deliveryadmin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliveryadmin.R
import com.example.deliveryadmin.menu.EditItem
import com.example.deliveryadmin.models.dishDataModel
import com.google.firebase.database.FirebaseDatabase

class DishAdapter(
    private val dishList: List<dishDataModel>,
    private val context: Context,
    private val userId: String // Include userId in the adapter constructor
) : RecyclerView.Adapter<DishAdapter.ItemViewHolder>() {

    // Reversed dish list
    private val reversedDishList = dishList.asReversed()

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishItemName: TextView = itemView.findViewById(R.id.dishItemName)
        val dishItemPrice: TextView = itemView.findViewById(R.id.dishItemPrice)
        val dishItemIngredients: TextView = itemView.findViewById(R.id.dishItemIngredients)
        val dishItemImage: ImageView = itemView.findViewById(R.id.dishItemImage)
        val dishVegNonVegImage:ImageView = itemView.findViewById(R.id.vegNonVegSymbol)
        val editButton: CardView = itemView.findViewById(R.id.editButton)
        val dishItemAvailability: Switch = itemView.findViewById(R.id.dishItemAvailability)

        init {
            // Handle click on the edit button
            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dish = reversedDishList[position]
                    // Open EditItem activity with dish details
                    val intent = Intent(context, EditItem::class.java).apply {
                        putExtra("dishId", dish.id)
                        putExtra("dishName", dish.dishName)
                        putExtra("price", dish.price)
                        putExtra("ingredients", dish.ingredients)
                        putExtra("veg",dish.veg)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dish_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val dish = reversedDishList[position]
        holder.dishItemName.text = dish.dishName
        holder.dishItemPrice.text = dish.price.toString()
        holder.dishItemIngredients.text = dish.ingredients
        holder.dishItemAvailability.isChecked = dish.itemStatus

        // Load the image using Glide
        Glide.with(context)
            .load(dish.imageUrl)
            .placeholder(R.drawable.dummy_food) // Optional placeholder image
            .error(R.drawable.dummy_food) // Optional error image
            .into(holder.dishItemImage)

        val vegNonVegResource = if (dish.veg) R.drawable.vegetarian_food_symbol else R.drawable.non_vegetarian_food_symbol
        holder.dishVegNonVegImage.setImageResource(vegNonVegResource)

        holder.dishItemAvailability.setOnCheckedChangeListener(null) // Reset the listener to avoid unwanted triggers
        holder.dishItemAvailability.isChecked = dish.itemStatus

        holder.dishItemAvailability.setOnCheckedChangeListener { _, isChecked ->
            // Update the itemStatus in the database
            val database = FirebaseDatabase.getInstance()
                .getReference("dishes")
                .child(userId) // Use userId to navigate to the correct user's dishes
                .child(dish.id)

            database.child("itemStatus").setValue(isChecked).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dish.itemStatus = isChecked // Update the local model's status
                } else {
                    // Handle the failure, maybe revert the switch state
                    holder.dishItemAvailability.isChecked = !isChecked
                }
            }
        }
    }

    override fun getItemCount(): Int = reversedDishList.size
}