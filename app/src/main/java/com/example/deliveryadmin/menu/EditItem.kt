package com.example.deliveryadmin.menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.deliveryadmin.R
import com.example.deliveryadmin.models.dishDataModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class EditItem : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item)

        // Making the button go back when pressed
        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()  // Closes the current activity and returns to the previous one
        }

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        val dishNameEditText = findViewById<EditText>(R.id.dishNameEditText)
        val priceEditText = findViewById<EditText>(R.id.priceEditText)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredientsEditText)
        val editButton = findViewById<CardView>(R.id.editButton)
        val saveChangesButton = findViewById<Button>(R.id.saveChanges)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        // Extract dish details from intent extras
        val dishId = intent.getStringExtra("dishId")
        val userId = auth.currentUser?.uid

        // Check if both dishId and userId are not null
        if (!dishId.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            val dishRef = database.child("dishes").child(userId).child(dishId)

            // Read dish details from the database
            dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val dish = snapshot.getValue(dishDataModel::class.java)
                        // Update EditText fields
                        dishNameEditText.setText(dish?.dishName)
                        priceEditText.setText(dish?.price.toString())
                        ingredientsEditText.setText(dish?.ingredients)

                        // Load the existing image from Firebase
                        Glide.with(this@EditItem)
                            .load(dish?.imageUrl)
                            .into(findViewById<ImageView>(R.id.selectedImage))
                    } else {
                        Log.e("EditItem", "Dish data not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("EditItem", "Error retrieving dish data: ${error.message}")
                }
            })
        } else {
            Log.e("EditItem", "Missing dishId or userId")
        }

        editButton.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare() // Crop image to square
                .compress(1024) // Final image size will be less than 1 MB
                .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080
                .start()
        }

        saveChangesButton.setOnClickListener {
            checkAuthentication(dishId)
        }

        deleteButton.setOnClickListener {
            deleteItem(dishId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            findViewById<ImageView>(R.id.selectedImage).visibility = View.VISIBLE // Show selected image
            findViewById<ImageView>(R.id.selectedImage).setImageURI(imageUri) // Set selected image
            Log.d("EditItem", "Image selected: $imageUri")
        } else {
            Log.d("EditItem", "Image selection failed or canceled")
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAuthentication(dishId: String?) {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            // User is signed in, proceed with saving the item
            saveItem(dishId)
        } else {
            // No user is signed in, prompt the user to sign in
            Toast.makeText(this, "Please sign in to save the item", Toast.LENGTH_SHORT).show()
            // You can also consider redirecting the user to the sign-in activity or showing a dialog.
        }
    }

    private fun saveItem(dishId: String?) {
        val dishNameEditText = findViewById<EditText>(R.id.dishNameEditText)
        val priceEditText = findViewById<EditText>(R.id.priceEditText)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredientsEditText)
        val dishName = dishNameEditText.text.toString()
        val price = priceEditText.text.toString().toIntOrNull()
        val ingredients = ingredientsEditText.text.toString()

        if (dishName.isEmpty() || price == null || ingredients.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            uploadImageToFirebase(imageUri!!) { imageUrl ->
                saveItemToFirebase(dishId, dishName, price, ingredients, imageUrl)
            }
        } else {
            saveItemToFirebase(dishId, dishName, price, ingredients, imageUrl = null)
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, callback: (String) -> Unit) {
        // Start a coroutine for the upload operation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
                val uploadTask = imageRef.putFile(imageUri).await()
                val imageUrl = uploadTask.storage.downloadUrl.await().toString()
                withContext(Dispatchers.Main) {
                    // Callback with the image URL on the main thread
                    callback(imageUrl)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("UploadImage", "Failed to upload image: ${e.message}", e)
                    Toast.makeText(
                        this@EditItem,
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveItemToFirebase(
        dishId: String?,
        name: String,
        price: Int,
        description: String,
        imageUrl: String? = null
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null && dishId != null) {
            val userId = currentUser.uid
            val dishRef = database.child("dishes").child(userId).child(dishId)

            val dish = dishDataModel(
                id = dishId,
                dishName = name,
                price = price,
                ingredients = description,
                imageUrl = imageUrl ?: ""
            )

            dishRef.setValue(dish)
                .addOnSuccessListener {
                    Log.d("SaveItemToFirebase", "Dish updated successfully")
                    Toast.makeText(this, "Dish updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("SaveItemToFirebase", "Failed to update dish: ${e.message}", e)
                    Toast.makeText(this, "Failed to update dish: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Log.e("SaveItemToFirebase", "No user is signed in or dishId is null")
            Toast.makeText(this, "No user is signed in or dishId is null", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun deleteItem(dishId: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null && dishId != null) {
            val userId = currentUser.uid
            val dishRef = database.child("dishes").child(userId).child(dishId)

            dishRef.removeValue()
                .addOnSuccessListener {
                    Log.d("DeleteItem", "Dish deleted successfully")
                    Toast.makeText(this, "Dish deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("DeleteItem", "Failed to delete dish: ${e.message}", e)
                    Toast.makeText(this, "Failed to delete dish: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Log.e("DeleteItem", "No user is signed in or dishId is null")
            Toast.makeText(this, "No user is signed in or dishId is null", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

