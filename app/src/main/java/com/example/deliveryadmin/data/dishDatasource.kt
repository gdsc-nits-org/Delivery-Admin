package com.example.deliveryadmin.data

import com.example.deliveryadmin.models.dishDataModel

class dishDatasource {
    fun loadItems(): List<dishDataModel> {
        return listOf<dishDataModel>(
            dishDataModel( dishName = "Paneer", price = 200, ingredients = "Paneer"),
            dishDataModel( dishName = "Paneer", price = 200, ingredients = "Paneer"),
            dishDataModel( dishName = "Paneer", price = 200, ingredients = "Paneer"),
        )
    }
}