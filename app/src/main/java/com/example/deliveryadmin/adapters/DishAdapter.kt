package com.example.deliveryadmin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliveryadmin.R
import com.example.deliveryadmin.menu.EditItem
import com.example.deliveryadmin.models.dishDataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class DishAdapter(
    private val context: Context,
    private val userId: String,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.Adapter<DishAdapter.ItemViewHolder>(), Filterable {

    private var reversedDishList: List<dishDataModel> = emptyList()
    private var filteredDishList: List<dishDataModel> = emptyList()

    init {
        // Initialize empty lists
        reversedDishList = emptyList()
        filteredDishList = reversedDishList
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
                filteredDishList = filterResults?.values as? List<dishDataModel> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    fun updateData(newDishList: List<dishDataModel>) {
        val diffCallback = DishDiffCallback(reversedDishList, newDishList.asReversed())
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        reversedDishList = newDishList.asReversed()
        filteredDishList = reversedDishList

        diffResult.dispatchUpdatesTo(this)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dishItemName: TextView = itemView.findViewById(R.id.dishItemName)
        private val dishItemPrice: TextView = itemView.findViewById(R.id.dishItemPrice)
        private val dishItemIngredients: TextView = itemView.findViewById(R.id.dishItemIngredients)
        private val dishItemImage: ImageView = itemView.findViewById(R.id.dishItemImage)
        private val dishVegNonVegImage: ImageView = itemView.findViewById(R.id.vegNonVegSymbol)
        private val editButton: CardView = itemView.findViewById(R.id.editButton)
        private val dishItemAvailability: Switch = itemView.findViewById(R.id.dishItemAvailability)

        init {
            // Handle click on the edit button
            editButton.setOnClickListener {
                val position = bindingAdapterPosition
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
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dish = filteredDishList[position]
                    updateItemStatus(dish, isChecked)
                }
            }
        }

        fun bind(dish: dishDataModel) {
            dishItemName.text = dish.dishName
            dishItemPrice.text = dish.price.toString()
            dishItemIngredients.text = dish.ingredients
            dishItemAvailability.isChecked = dish.itemStatus

            Glide.with(context)
                .load(dish.imageUrl)
                .placeholder(R.drawable.dummy_food)
                .error(R.drawable.media)
                .into(dishItemImage)

            val vegNonVegResource = if (dish.veg) R.drawable.vegetarian_food_symbol else R.drawable.non_vegetarian_food_symbol
            dishVegNonVegImage.setImageResource(vegNonVegResource)

            dishItemAvailability.tag = dish.id
        }
    }

    private fun updateItemStatus(dish: dishDataModel, isChecked: Boolean) {
        val database = FirebaseDatabase.getInstance().getReference("dishes").child(dish.id)

        database.child("itemStatus").setValue(isChecked)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val index = filteredDishList.indexOf(dish)
                    if (index != -1) {
                        filteredDishList[index].itemStatus = isChecked
                        notifyItemChanged(index)
                    }
                } else {
                    // Handle failure, maybe revert the switch state
                }
            }
    }

    private val itemStatusListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // This listener now updates the entire list when there are changes
            if (snapshot.exists()) {
                val updatedDishList = mutableListOf<dishDataModel>()
                for (dishSnapshot in snapshot.children) {
                    val dish = dishSnapshot.getValue(dishDataModel::class.java)
                    dish?.let { updatedDishList.add(it) }
                }
                updateData(updatedDishList)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle database errors
        }
    }

    init {
        val database = FirebaseDatabase.getInstance()
        .getReference("dishes")
            .child(userId)
        database.addValueEventListener(itemStatusListener)
    }

    class DishDiffCallback(
        private val oldList: List<dishDataModel>,
        private val newList: List<dishDataModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
