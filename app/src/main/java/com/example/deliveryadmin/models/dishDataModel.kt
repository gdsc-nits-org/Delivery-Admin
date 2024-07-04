package com.example.deliveryadmin.models

data class dishDataModel(
    val id : String = "",
    val dishName : String = "",
    val price : Int = 0,
    val ingredients: String = "",
    var itemStatus: Boolean = true,
    var imageUrl: String = "",
    var veg: Boolean = true
) {
    constructor() : this("", "", 0, "", true, "", true)
}
