package com.example.searchfr.rec_Views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.searchfr.R
import com.example.searchfr.rec_Views.RecentlySearchedAdapter.ViewHolder

class PopularDishAdapter(val dataList: ArrayList<PopularDishesModel>, val context: Context):
    RecyclerView.Adapter<PopularDishAdapter.ViewHolder>(){

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imagePDish: ImageView =view.findViewById(R.id.P_s_item)
        val Pname: TextView =view.findViewById(R.id.PDish_Title)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.popular_dishes_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val module = dataList[position]

        holder.imagePDish.setImageResource(module.imagePDish)
        holder.Pname.text=module.Pname
    }
    }

