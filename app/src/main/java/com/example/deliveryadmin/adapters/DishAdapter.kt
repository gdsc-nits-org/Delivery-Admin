package com.example.deliveryadmin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.menu.EditItem
import com.example.deliveryadmin.models.dishDataModel

class DishAdapter(
    private val dishList: List<dishDataModel>,
    private val context: Context
) : RecyclerView.Adapter<DishAdapter.ItemViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishItemName: TextView = itemView.findViewById(R.id.dishItemName)
        val dishItemPrice: TextView = itemView.findViewById(R.id.dishItemPrice)
        val dishItemIngredients: TextView = itemView.findViewById(R.id.dishItemIngredients)
        val dishItemImage: ImageView = itemView.findViewById(R.id.dishItemImage)
        val editButton: CardView = itemView.findViewById(R.id.editButton)

        init {
            // Add click listener to the entire item view
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dish_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val dish = dishList[position]
        holder.dishItemName.text = dish.dishName
        holder.dishItemPrice.text = dish.price.toString()
        holder.dishItemIngredients.text = dish.ingredients

        // Set the image resource (assuming you have a method to load images)
        // For example, if you're using Glide or Picasso:
        // Glide.with(context).load(dish.imageUrl).into(holder.dishItemImage)

        holder.editButton.setOnClickListener {
            // Handle edit button click separately
            val intent = Intent(context, EditItem::class.java)
            intent.putExtra("dishId", dish.id)
            intent.putExtra("dishName", dish.dishName)
            intent.putExtra("price", dish.price)
            intent.putExtra("ingredients", dish.ingredients)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dishList.size
}
