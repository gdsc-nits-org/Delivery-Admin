package com.example.deliveryadmin.menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.deliveryadmin.R
import com.example.deliveryadmin.models.dishDataModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditItem: AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null
    private lateinit var vegNonVegSpinner: Spinner
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item)

        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        val dishNameEditText = findViewById<EditText>(R.id.dishNameEditText)
        val priceEditText = findViewById<EditText>(R.id.priceEditText)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredientsEditText)
        val editButton = findViewById<CardView>(R.id.editButton)
        val saveChangesButton = findViewById<LinearLayout>(R.id.saveChanges)
        val deleteButton = findViewById<LinearLayout>(R.id.deleteButton)
        val selectedImage = findViewById<ImageView>(R.id.selectedImage)
        vegNonVegSpinner = findViewById(R.id.vegNonVegSpinner)

        val dishId = intent.getStringExtra("dishId")
        val userId = auth.currentUser?.uid

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

                        // Load the image or show placeholder if imageUrl is null or empty
                        if (dish?.imageUrl.isNullOrEmpty()) {
                            selectedImage.setImageResource(R.drawable.dummy_food)
                        } else {
                            Glide.with(this@EditItem)
                                .load(dish?.imageUrl)
                                .into(selectedImage)
                        }
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
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
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
            Log.d("EditItemActivity", "Image selected: $imageUri")
        } else {
            Log.d("EditItemActivity", "Image selection failed or canceled")
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAuthentication(dishId: String?) {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            saveItem(dishId)
        } else {
            Toast.makeText(this, "Please sign in to save the item", Toast.LENGTH_SHORT).show()
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
            val currentUser = auth.currentUser
            if (currentUser != null && dishId != null) {
                val userId = currentUser.uid
                val dishRef = database.child("dishes").child(userId).child(dishId)

                dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val dish = snapshot.getValue(dishDataModel::class.java)
                            val currentImageUrl = dish?.imageUrl ?: ""
                            saveItemToFirebase(dishId, dishName, price, ingredients, currentImageUrl)
                        } else {
                            Log.e("EditItemActivity", "Dish data not found")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("EditItemActivity", "Error retrieving dish data: ${error.message}")
                    }
                })
            } else {
                Log.e("EditItemActivity", "No user is signed in or dishId is null")
                Toast.makeText(this, "No user is signed in or dishId is null", Toast.LENGTH_SHORT).show()
            }
        }

        // Finish the activity immediately after initiating the save operation
        finish()
    }



    private fun uploadImageToFirebase(imageUri: Uri, callback: (String) -> Unit) {
        // Start a coroutine for the upload operation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")
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

            val veg = vegNonVegSpinner.selectedItemPosition == 0

            val dish = dishDataModel(
                id = dishId,
                dishName = name,
                price = price,
                ingredients = description,
                imageUrl = imageUrl ?: "",
                veg = veg
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
                    finish()
                }
        } else {
            Log.e("DeleteItem", "No user is signed in or dishId is null")
            Toast.makeText(this, "No user is signed in or dishId is null", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

}


