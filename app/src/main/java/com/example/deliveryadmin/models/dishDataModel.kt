package com.example.deliveryadmin.models

data class dishDataModel(
    val id : String = "",
    val dishName : String = "",
    val price : Int = 0,
    val ingredients: String = "",
    var itemStatus: Boolean = true,
    var imageUrl: String = "" // Add a field for storing the image URL
) {
    constructor() : this("", "", 0, "", true, "")
}
