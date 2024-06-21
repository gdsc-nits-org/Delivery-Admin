package com.example.searchfr.rec_Views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.searchfr.R

class RecentlySearchedAdapter(val dataList: ArrayList<RecentlySearchedModel>, val context: Context):
    RecyclerView.Adapter<RecentlySearchedAdapter.ViewHolder>(){

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageDish: ImageView=view.findViewById(R.id.r_s_item)
        val imageClose: ImageView=view.findViewById(R.id.r_s_close_item)
        val name:TextView=view.findViewById(R.id.Dish_Title)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.recently_searced_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val module = dataList[position]

        holder.imageDish.setImageResource(module.imageDish)
        holder.imageClose.setImageResource(module.imageClose)
        holder.name.text=module.name
    }
}