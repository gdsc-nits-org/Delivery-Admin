//package com.example.deliveryadmin
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        }
//}
package com.example.deliveryadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View // Import View class
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // Listen for changes in the current navigation destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashScreen,          // Hide for splash screen
                R.id.accessLocation,        // Hide for location permission screen
                R.id.signIn,                // Hide for sign in screen
                R.id.loginPage,             // Hide for login screen
                R.id.signUpPage,            // Hide for sign up screen
                R.id.forgotPasswordFragment, // Hide for forgot password
                R.id.addressFragment,       // Hide for address screen (if not a bottom nav tab)
                R.id.emptyActivity -> {     // Hide for emptyActivity (SecondActivity)
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.homeFragment,       // Show for bottom nav fragments
                R.id.searchFragment,
                R.id.menuFragment,
                R.id.profileFragment,
                R.id.ordersPendingFragment -> { // Show for other main app screens that should have bottom nav
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    // Default behavior for any other fragment not explicitly listed
                    // Decide if you want bottom nav to be visible or gone by default for others
                    bottomNavigationView.visibility = View.GONE // Or View.VISIBLE, depending on your app's needs
                }
            }
        }
    }
}