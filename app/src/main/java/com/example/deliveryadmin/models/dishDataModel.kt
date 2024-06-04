package com.example.deliveryadmin.models

data class dishDataModel(
//    val veg : Boolean,
    val id : String = "",
    val dishName : String = "",
    val price : Int = 0,
    val ingredients: String = "",
//    val itemStatus: Boolean,
//    val imageUrl: String,
){
    // No-argument constructor required for Firebase
    constructor() : this("", "", 0, "")
}