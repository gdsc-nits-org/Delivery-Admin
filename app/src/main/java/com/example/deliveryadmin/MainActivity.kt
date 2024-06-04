package com.example.deliveryadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deliveryadmin.HomePageFragments.HomeFragment
import com.example.deliveryadmin.HomePageFragments.MenuFragment
import com.example.deliveryadmin.HomePageFragments.ProfileFragment
import com.example.deliveryadmin.HomePageFragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {

//    private lateinit var bottomNavigationView: BottomNavigationView
//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        auth = FirebaseAuth.getInstance()
//
//        // Check if the user is already signed in
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            // If signed in, proceed with the app's functionality
//            initializeApp(currentUser)
//        } else {
//            // If not signed in, anonymously authenticate the user
//            signInAnonymously()
//        }
    }

//    private fun signInAnonymously() {
//        auth.signInAnonymously()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, get the signed-in user
//                    val user = auth.currentUser
//                    user?.let {
//                        // Proceed with the app's functionality
//                        initializeApp(it)
//                    }
//                } else {
//                    // If sign in fails, display a message to the user.
//                    // You may handle this case differently based on your app's requirements.
//                    // For example, you can show an error dialog or retry the sign-in process.
//                    // Here, we'll simply display a toast message.
//                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun initializeApp(user: FirebaseUser) {
//        bottomNavigationView = findViewById(R.id.bottom_navigation)
//        bottomNavigationView.setOnItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.navigation_home -> {
//                    // Replace fragment with HomeFragment
//                    replaceFragment(HomeFragment())
//                    true
//                }
//                R.id.navigation_search -> {
//                    // Replace fragment with CartFragment
//                    replaceFragment(SearchFragment())
//                    true
//                }
//                R.id.navigation_menu -> {
//                    // Replace fragment with SearchFragment
//                    replaceFragment(MenuFragment())
//                    true
//                }
//                R.id.navigation_profile -> {
//                    // Replace fragment with ProfileFragment
//                    replaceFragment(ProfileFragment())
//                    true
//                }
//                else -> false
//            }
//        }
//        replaceFragment(HomeFragment())
//    }
//
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
//    }
}
