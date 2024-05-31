package com.example.deliveryadmin.data

import com.example.deliveryadmin.models.dishDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class dishDatasource {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun loadItems(callback: (List<dishDataModel>) -> Unit) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dishesRef = database.child("dishes").child(userId)

            dishesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dishList = mutableListOf<dishDataModel>()
                    for (dishSnapshot in snapshot.children) {
                        val dish = dishSnapshot.getValue(dishDataModel::class.java)
                        if (dish != null) {
                            dishList.add(dish)
                        }
                    }
                    callback(dishList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
        }
    }
}
