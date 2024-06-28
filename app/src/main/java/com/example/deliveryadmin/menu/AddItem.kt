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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.models.dishDataModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class AddItem : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_item)

        // Making the button go back when pressed
        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()  // Closes the current activity and returns to the previous one
        }

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        val itemNameInput = findViewById<EditText>(R.id.itemNameInput)
        val itemPriceInput = findViewById<EditText>(R.id.itemPriceInput)
        val itemDescriptionInput = findViewById<EditText>(R.id.itemDescriptionInput)
        val addImageCardView = findViewById<CardView>(R.id.addImageCardView)
        val saveButton = findViewById<LinearLayout>(R.id.saveButton)

        addImageCardView.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare() // Crop image to square
                .compress(1024) // Final image size will be less than 1 MB
                .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080
                .start()
        }

        saveButton.setOnClickListener {
            checkAuthentication()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            findViewById<ImageView>(R.id.addImageIcon).visibility = View.GONE // Hide ic_add_image
            findViewById<ImageView>(R.id.selectedImageView).setImageURI(imageUri) // Set selected image
            Log.d("AddItem", "Image selected: $imageUri")
        } else {
            Log.d("AddItem", "Image selection failed or canceled")
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAuthentication() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            // User is signed in, proceed with saving the item
            saveItem()
        } else {
            // No user is signed in, prompt the user to sign in
            Toast.makeText(this, "Please sign in to save the item", Toast.LENGTH_SHORT).show()
            // You can also consider redirecting the user to the sign-in activity or showing a dialog.
        }
    }

    private fun saveItem() {
        val itemNameInput = findViewById<EditText>(R.id.itemNameInput)
        val itemPriceInput = findViewById<EditText>(R.id.itemPriceInput)
        val itemDescriptionInput = findViewById<EditText>(R.id.itemDescriptionInput)
        val itemName = itemNameInput.text.toString()
        val itemPrice = itemPriceInput.text.toString().toIntOrNull()
        val itemDescription = itemDescriptionInput.text.toString()

        if (itemName.isEmpty() || itemPrice == null || itemDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            uploadImageToFirebase(imageUri!!) { imageUrl ->
                saveItemToFirebase(itemName, itemPrice, itemDescription, imageUrl)
            }
        } else {
            saveItemToFirebase(itemName, itemPrice, itemDescription, imageUrl = null)
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
                    Toast.makeText(this@AddItem, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveItemToFirebase(name: String, price: Int, description: String, imageUrl: String? = null) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dishRef = database.child("dishes").child(userId).push()

            val dishId = dishRef.key

            val dish = dishDataModel(
                id = dishId ?: "",
                dishName = name,
                price = price,
                ingredients = description,
                imageUrl = imageUrl ?: ""
            )

            dishRef.setValue(dish)
                .addOnSuccessListener {
                    Log.d("SaveItemToFirebase", "Dish saved successfully")
                    Toast.makeText(this, "Dish saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("SaveItemToFirebase", "Failed to save dish: ${e.message}", e)
                    Toast.makeText(this, "Failed to save dish: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("SaveItemToFirebase", "No user is signed in")
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show()
        }
    }
}
