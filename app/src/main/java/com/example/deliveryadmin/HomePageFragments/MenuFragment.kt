package com.example.deliveryadmin.HomePageFragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.adapters.DishAdapter
import com.example.deliveryadmin.data.dishDatasource
import com.example.deliveryadmin.menu.AddItem
import com.example.deliveryadmin.models.dishDataModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

class MenuFragment : Fragment() {

    private lateinit var mydataset: List<dishDataModel>
    private lateinit var userId: String // Add userId variable

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var generativeModel: GenerativeModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty() // Get the current user's ID

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.dishes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val dishDataSource = dishDatasource()
        dishDataSource.loadItems { data ->
            if (isAdded) {
                mydataset = data // Store data in the property
                val adapter = DishAdapter(data, requireContext(), userId) // Pass userId to the adapter
                recyclerView.adapter = adapter
            }
        }

        // Find the FAB and set an OnClickListener
        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Inside your RecyclerView adapter's click listener
            val intent = Intent(activity, AddItem::class.java)
            startActivity(intent)
        }

        // Firebase Part
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference



        // Gemini AI part
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBbA55jn6SXCEwRQVLjWEMFLOAMF1ttlQQ" // Hard coding for now; will use BuildConfig later on
        )

        val aiButton = rootView.findViewById<LinearLayout>(R.id.ai)

        aiButton.setOnClickListener {
//            Toast.makeText(activity, "AI clicked", Toast.LENGTH_SHORT).show()
            // Image selector
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri? = data.data
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                // Use the imageBitmap as needed, e.g., feed it to the Gemini API
                processImage(imageBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun processImage(imageBitmap: Bitmap) {
        // Example code to use the image with the Gemini API
        val image1: Bitmap = imageBitmap

        val inputContent = content {
            image(image1)
            text("Give a JSON output of the items present here including fields" +
                    " item name (string), item price (integer) and item description(string) in one line, fill with null if not" +
                    " present in the menue for each item. Give a null in json if the" +
                    " image given is not a resturant menue")
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = generativeModel.generateContent(inputContent)
                withContext(Dispatchers.Main) {
                    val jsonString = response.text.toString()
                    val extractedJsonObjects = extractJsonFromString(jsonString)
//                    Toast.makeText(activity, jsonString, Toast.LENGTH_LONG).show()
                    extractedJsonObjects.forEach { s ->
                        val temp = s.split('\n')
                        val nameLine = temp[1].trim()
                        val priceLine = temp[2].trim()
                        val descriptionLine = temp[3].trim()

                        val name = nameLine.split(':')[1].trim()
                        val price = priceLine.split(':')[1].trim()
                        val description = descriptionLine.split(':')[1].trim()

//                        println(getName(name))
//                        println(getNumber(price))
//                        println(getName(description))
                        upLoadDishToFirebase(getName(name), getNumber(price).toInt(), getName(description))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun upLoadDishToFirebase(dishName: String, price: Int, ingredients: String) {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dishRef = database.child("dishes").child(userId).push()

            val dishId = dishRef.key

            val dish = dishDataModel(
                id = dishId ?: "",
                dishName = dishName,
                price = price,
                ingredients = ingredients,
                imageUrl = ""
            )

            dishRef.setValue(dish)
                .addOnSuccessListener {
                    Log.d("SaveItemToFirebase", "Dish saved successfully")
                    Toast.makeText(activity, "Dish saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("SaveItemToFirebase", "Failed to save dish: ${e.message}", e)
                    Toast.makeText(activity, "Failed to save dish: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("SaveItemToFirebase", "No user is signed in")
            Toast.makeText(activity, "No user is signed in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractJsonFromString(input: String): List<String> {
        val jsonObjects = mutableListOf<String>()
        var depth = 0
        var startIndex = -1
        var endIndex = -1

        for (i in input.indices) {
            when (input[i]) {
                '{' -> {
                    if (depth == 0) {
                        startIndex = i
                    }
                    depth++
                }
                '}' -> {
                    depth--
                    if (depth == 0) {
                        endIndex = i
                        jsonObjects.add(input.substring(startIndex, endIndex + 1))
                    }
                }
            }
        }

        return jsonObjects
    }


    fun getName(inputText: String): String {
        val pattern = Pattern.compile("\"([^\"]+)\"")
        val matcher = pattern.matcher(inputText)
        return if (matcher.find()) {
            matcher.group(1) ?: "null"
        } else {
            "null"
        }
    }

    fun getNumber(inputText: String): String {
        val pattern = Pattern.compile("\\b(\\d+)\\b")
        val matcher = pattern.matcher(inputText)
        return if (matcher.find()) {
            matcher.group(1) ?: ""
        } else {
            ""
        }
    }

}
