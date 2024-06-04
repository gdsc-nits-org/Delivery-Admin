package com.example.deliveryadmin.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deliveryadmin.R
import com.example.deliveryadmin.models.dishDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditItem : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String // Store the user ID

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val dishNameEditText = findViewById<EditText>(R.id.dishNameEditText)
        val priceEditText = findViewById<EditText>(R.id.priceEditText)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredientsEditText)
        val saveButton = findViewById<Button>(R.id.saveChanges)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        // Retrieve the dish ID passed through the intent
        val dishId = intent.getStringExtra("dishId").toString()

        // Retrieve the current user's ID
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Retrieve the data for the selected dish from the database using its ID
        database.child("dishes").child(userId).child(dishId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dish = snapshot.getValue(dishDataModel::class.java)
                dish?.let {
                    // Populate the fields with the retrieved data
                    dishNameEditText.setText(it.dishName)
                    priceEditText.setText(it.price.toString())
                    ingredientsEditText.setText(it.ingredients)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(applicationContext, "Failed to retrieve dish information", Toast.LENGTH_SHORT).show()
            }
        })

        // Update the existing dish in the database
        saveButton.setOnClickListener {
            val updatedDishName = dishNameEditText.text.toString()
            val updatedPrice = priceEditText.text.toString().toIntOrNull() ?: 0 // Handle invalid input

            val updatedIngredients = ingredientsEditText.text.toString()

            // Update the dish details in Firebase
            val updatedDish = mapOf<String, Any>(
                "dishName" to updatedDishName,
                "price" to updatedPrice,
                "ingredients" to updatedIngredients
            )

            // Reference the existing dish in the database using its ID and update its values
            val dishRef = database.child("dishes").child(userId).child(dishId)
            dishRef.updateChildren(updatedDish)
                .addOnSuccessListener {
                    // Successfully updated
                    Toast.makeText(this, "Dish updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    // Handle the error
                    Toast.makeText(this, "Failed to update dish", Toast.LENGTH_SHORT).show()
                }
        }

        // Delete the existing dish from the database
        deleteButton.setOnClickListener {
            // Delete the dish from Firebase
            database.child("dishes").child(userId).child(dishId).removeValue()
                .addOnSuccessListener {
                    // Successfully deleted
                    Toast.makeText(this, "Dish deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    // Handle the error
                    Toast.makeText(this, "Failed to delete dish", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
