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
import android.widget.Filter
import android.widget.Filterable
import java.util.*
import kotlin.collections.ArrayList

class DishAdapter(
    private val dishList: List<dishDataModel>,
    private val context: Context,
    private val userId: String // Include userId in the adapter constructor
) : RecyclerView.Adapter<DishAdapter.ItemViewHolder>(), Filterable {

    private var reversedDishList: List<dishDataModel> = dishList.asReversed()
    private var filteredDishList: List<dishDataModel> = reversedDishList

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishItemName: TextView = itemView.findViewById(R.id.dishItemName)
        val dishItemPrice: TextView = itemView.findViewById(R.id.dishItemPrice)
        val dishItemIngredients: TextView = itemView.findViewById(R.id.dishItemIngredients)
        val dishItemImage: ImageView = itemView.findViewById(R.id.dishItemImage)
        val dishVegNonVegImage: ImageView = itemView.findViewById(R.id.vegNonVegSymbol)
        val editButton: CardView = itemView.findViewById(R.id.editButton)
        val dishItemAvailability: Switch = itemView.findViewById(R.id.dishItemAvailability)

        init {
            // Handle click on the edit button
            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dish = filteredDishList[position]
                    // Open EditItem activity with dish details
                    val intent = Intent(context, EditItem::class.java).apply {
                        putExtra("dishId", dish.id)
                        putExtra("dishName", dish.dishName)
                        putExtra("price", dish.price)
                        putExtra("ingredients", dish.ingredients)
                        putExtra("veg", dish.veg)
                    }
                    context.startActivity(intent)
                }
            }

            // Initialize switch listener
            dishItemAvailability.setOnCheckedChangeListener(null) // Clear previous listener
            dishItemAvailability.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dish = filteredDishList[position]
                    updateItemStatus(dish, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dish_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val dish = filteredDishList[position]
        holder.bind(dish)
    }

    override fun getItemCount(): Int = filteredDishList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence?.toString()?.toLowerCase(Locale.getDefault()) ?: ""
                filteredDishList = if (charString.isEmpty()) {
                    reversedDishList
                } else {
                    reversedDishList.filter {
                        it.dishName.toLowerCase(Locale.getDefault()).contains(charString)
                    }
                }
                return FilterResults().apply { values = filteredDishList }
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                filteredDishList = filterResults?.values as List<dishDataModel>? ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    private fun ItemViewHolder.bind(dish: dishDataModel) {
        dishItemName.text = dish.dishName
        dishItemPrice.text = dish.price.toString()
        dishItemIngredients.text = dish.ingredients
        dishItemAvailability.isChecked = dish.itemStatus

        // Load the image using Glide
        Glide.with(context)
            .load(dish.imageUrl)
            .placeholder(R.drawable.dummy_food)
            .error(R.drawable.media)
            .into(dishItemImage)

        val vegNonVegResource = if (dish.veg) R.drawable.vegetarian_food_symbol else R.drawable.non_vegetarian_food_symbol
        dishVegNonVegImage.setImageResource(vegNonVegResource)

        // Set tag to identify the current item in the listener
        dishItemAvailability.tag = dish.id

        // Set listener after binding data
        dishItemAvailability.setOnCheckedChangeListener { _, isChecked ->
            updateItemStatus(dish, isChecked)
        }
    }

    private fun updateItemStatus(dish: dishDataModel, isChecked: Boolean) {
        val database = FirebaseDatabase.getInstance()
            .getReference("dishes")
            .child(userId) // Use userId to navigate to the correct user's dishes
            .child(dish.id)

        database.child("itemStatus").setValue(isChecked)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dish.itemStatus = isChecked // Update the local model's status
                    notifyItemChanged(filteredDishList.indexOf(dish)) // Notify adapter of item change
                } else {
                    // Handle the failure, maybe revert the switch state
                    // This depends on your app's logic and user experience
                }
            }
    }
}
