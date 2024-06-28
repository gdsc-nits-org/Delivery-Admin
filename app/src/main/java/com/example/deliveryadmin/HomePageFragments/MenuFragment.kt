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
            Toast.makeText(activity, "AI clicked", Toast.LENGTH_SHORT).show()
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
                    " item name, item price and item description in one line, fill with null if not" +
                    " present in the menue for each item. Give a null in json if the" +
                    " image given is not a resturant menue")
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = generativeModel.generateContent(inputContent)
                withContext(Dispatchers.Main) {
                    var result = response.text.toString()
                    result = extractJsonFromString(result).toString()
                    Toast.makeText(activity, result, Toast.LENGTH_LONG).show()
                    processMenuItems(result)
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


    private fun extractAmount(input: String): Int? {
        val numberRegex = Regex("\\d+")
        val matchResult = numberRegex.find(input)
        return matchResult?.value?.toIntOrNull()
    }


    fun processMenuItems(jsonString: String) {
        try {
            // Remove any extraneous text if necessary
            val cleanedJsonString = jsonString.trim()

            // Parse the JSON array
            val jsonArray = JSONArray(cleanedJsonString)

            // Iterate through each item in the array
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                // Extract keys dynamically
                val keys = jsonObject.keys()
                val itemName = jsonObject.optString("item_name", "")

                var itemPrice = ""
                var itemDescription = ""

                // Iterate through keys and extract values
                while (keys.hasNext()) {
                    when (val key = keys.next()) {
                        "item_price", "price" -> itemPrice = jsonObject.get(key).toString()
                        "item_description", "description" -> itemDescription = jsonObject.optString(key, "")
                    }
                }

                extractAmount(itemPrice)?.let {
                    upLoadDishToFirebase(itemName,
                        it, itemDescription)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle parsing errors or invalid JSON input
        }
    }

}
