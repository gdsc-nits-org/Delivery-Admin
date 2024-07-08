package com.example.deliveryadmin.models

data class dishDataModel(
    val id : String = "",
    val dishName : String = "",
    val price : Int = 0,
    val ingredients: String = "",
    var itemStatus: Boolean = true,
    var imageUrl: String = "https://firebasestorage.googleapis.com/v0/b/delivery-app-9324d.appspot.com/o/images%2F1720205422304.jpg?alt=media&token=14b67c89-0454-411f-99f0-c9a3f47e9f52",
    var veg: Boolean = true
) {
    constructor() : this("", "", 0, "", true, "https://firebasestorage.googleapis.com/v0/b/delivery-app-9324d.appspot.com/o/images%2F1720205422304.jpg?alt=media&token=14b67c89-0454-411f-99f0-c9a3f47e9f52", true)
}
