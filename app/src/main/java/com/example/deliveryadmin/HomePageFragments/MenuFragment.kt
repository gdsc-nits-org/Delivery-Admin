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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.deliveryadmin.R
import com.example.deliveryadmin.adapters.DishAdapter
import com.example.deliveryadmin.data.dishDatasource
import com.example.deliveryadmin.menu.AddItem
import com.example.deliveryadmin.models.dishDataModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

class MenuFragment : Fragment() {
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var animationView: LottieAnimationView

    private var mydataset: MutableList<dishDataModel> = mutableListOf()
    private lateinit var userId: String

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: DishAdapter

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var generativeModel: GenerativeModel

    private lateinit var fab: FloatingActionButton
    private lateinit var aiButton: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var imageButton: View

    private var currentFilterText: String? = null
    private var savedScrollPosition: Int = 0

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchDishesFromFirebase() //For fetching dishes

        val searchLayout = view.findViewById<LinearLayout>(R.id.search_layout)
        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchView = view.findViewById(R.id.search_view)

        userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

        recyclerView = view.findViewById(R.id.dishes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        layoutManager = recyclerView.layoutManager as LinearLayoutManager

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)

        emptyStateLayout = view.findViewById(R.id.empty_state_layout)
        animationView = view.findViewById(R.id.animationView)

        searchView.visibility = View.GONE

        searchButton.setOnClickListener {
            searchLayout.removeView(searchButton)
            searchView.visibility = View.VISIBLE
            searchView.isIconified = false
        }

        searchView.setOnCloseListener {
            searchView.visibility = View.GONE
            searchLayout.addView(searchButton, 0)
            false
        }

        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, AddItem::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
//        val dishDataSource = dishDatasource()
        shimmerFrameLayout.startShimmer()
        loadItems { data ->
            if (isAdded) {
                mydataset = data as MutableList<dishDataModel>
                adapter = DishAdapter(requireContext(), userId, layoutManager)
                recyclerView.adapter = adapter
                adapter.updateData(data)
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE

                updateEmptyState(data)
                applyCurrentFilter()
            }
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBbA55jn6SXCEwRQVLjWEMFLOAMF1ttlQQ"
        )

        aiButton = view.findViewById(R.id.ai)
        aiButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentFilterText = newText
                adapter.filter.filter(newText)
                return false
            }
        })

        imageButton = view.findViewById(R.id.image)
        imageButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 100)
        }
    }


    @Deprecated("Deprecated in Java")
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

                        uploadDishToFirebase(getName(name), getNumber(price).toInt(), getName(description))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadDishToFirebase(dishName: String, price: Int, ingredients: String) {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dishRef = database.child("dishes").push()
            val dishId = dishRef.key

            val dish = dishDataModel(
                id = dishId ?: "",
                dishName = dishName,
                price = price,
                ingredients = ingredients,
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/delivery-app-9324d.appspot.com/o/images%2F1720205422304.jpg?alt=media&token=14b67c89-0454-411f-99f0-c9a3f47e9f52",
                shopId = userId
            )

            dishRef.setValue(dish)
                .addOnSuccessListener {
                    Log.d("SaveItemToFirebase", "Dish saved successfully")
                    Toast.makeText(activity, "Dish saved successfully", Toast.LENGTH_SHORT).show()
                    // After saving, restore the scroll position
                    recyclerView.layoutManager?.scrollToPosition(savedScrollPosition)
                }
                .addOnFailureListener { e ->
                    Log.e("SaveItemToFirebase", "Failed to save dish: ${e.message}")
                    Toast.makeText(activity, "Failed to save dish: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //For fetching dishes
    private fun fetchDishesFromFirebase() {
        val dishesRef = FirebaseDatabase.getInstance().getReference("dishes")

        // Fetch initial list of dishes once
        dishesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dishesList = mutableListOf<dishDataModel>()
                for (dishSnapshot in snapshot.children) {
                    val dish = dishSnapshot.getValue(dishDataModel::class.java)
                    dish?.let {
                        dishesList.add(it)
                    }
                }

                // Initialize adapter if not already initialized
                if (!::adapter.isInitialized) {
                    adapter = DishAdapter(requireContext(), userId, layoutManager)
                    recyclerView.adapter = adapter
                }

                mydataset = dishesList
                adapter.updateData(dishesList)
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE

                updateEmptyState(dishesList)
                applyCurrentFilter()

                // Add child event listener for incremental updates
                addChildEventListener()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FetchDishesFromFirebase", "Failed to fetch dishes: ${error.message}")
                Toast.makeText(activity, "Failed to fetch dishes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addChildEventListener() {
        val dishesRef = FirebaseDatabase.getInstance().getReference("dishes")

        // Add child event listener to handle incremental updates
        dishesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val dish = snapshot.getValue(dishDataModel::class.java)
                dish?.let {
                    if (!mydataset.contains(it)) {
                        mydataset.add(it)
                        adapter.notifyItemInserted(mydataset.size - 1)
                        updateEmptyState(mydataset)
                        applyCurrentFilter()
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val dish = snapshot.getValue(dishDataModel::class.java)
                dish?.let {
                    val index = mydataset.indexOfFirst { item -> item.id == it.id }
                    if (index != -1) {
                        mydataset[index] = it
                        adapter.notifyItemChanged(index)
                        applyCurrentFilter()
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val dish = snapshot.getValue(dishDataModel::class.java)
                dish?.let {
                    val index = mydataset.indexOfFirst { item -> item.id == it.id }
                    if (index != -1) {
                        mydataset.removeAt(index)
                        adapter.notifyItemRemoved(index)
                        updateEmptyState(mydataset)
                        applyCurrentFilter()
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle moved dishes if necessary
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FetchDishesFromFirebase", "Child event listener cancelled: ${error.message}")
            }
        })
    }

    private fun updateEmptyState(data: List<dishDataModel>) {
        if (data.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            animationView.playAnimation()
        } else {
            emptyStateLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun applyCurrentFilter() {
        currentFilterText?.let {
            adapter.filter.filter(it)
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

    private fun getName(inputText: String): String {
        val pattern = Pattern.compile("\"([^\"]+)\"")
        val matcher = pattern.matcher(inputText)
        return if (matcher.find()) {
            matcher.group(1) ?: "null"
        } else {
            "null"
        }
    }

    private fun getNumber(inputText: String): String {
        val pattern = Pattern.compile("\\b(\\d+)\\b")
        val matcher = pattern.matcher(inputText)
        return if (matcher.find()) {
            matcher.group(1) ?: ""
        } else {
            ""
        }
    }

    private fun loadItems(callback: (List<dishDataModel>) -> Unit) {
        database.child("dishes")
            .orderByChild("shopId")
            .equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dishes = mutableListOf<dishDataModel>()
                    for (dishSnapshot in dataSnapshot.children) {
                        val dish = dishSnapshot.getValue(dishDataModel::class.java)
                        dish?.let { dishes.add(it) }
                    }
                    callback(dishes)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MenuFragment", "Database error: ${databaseError.message}")
                }
            })
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }


}
