package com.example.deliveryadmin.menu

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deliveryadmin.R
import com.example.deliveryadmin.models.dishDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddItem : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_item)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val itemNameInput = findViewById<EditText>(R.id.itemNameInput)
        val itemPriceInput = findViewById<EditText>(R.id.itemPriceInput)
        val itemDescriptionInput = findViewById<EditText>(R.id.itemDescriptionInput)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val itemName = itemNameInput.text.toString()
            val itemPrice = itemPriceInput.text.toString().toIntOrNull()
            val itemDescription = itemDescriptionInput.text.toString()

            if (itemName.isEmpty() || itemPrice == null || itemDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveItemToFirebase(itemName, itemPrice, itemDescription)
        }
    }

    private fun saveItemToFirebase(name: String, price: Int, description: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dishId = database.child("dishes").child(userId).push().key
            if (dishId != null) {
                val dish = dishDataModel(dishId, name, price, description)
                database.child("dishes").child(userId).child(dishId).setValue(dish)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Dish added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to add dish: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Failed to generate dish ID", Toast.LENGTH_SHORT).show()
            }
        } else {
            // User not authenticated, prompt the user to sign in
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            // Implement sign-in flow or redirect to sign-in activity
            // Example: startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}

