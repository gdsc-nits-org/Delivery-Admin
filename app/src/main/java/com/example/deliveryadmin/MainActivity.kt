package com.example.deliveryadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Navigate to Home Fragment
                    true
                }
                R.id.navigation_search -> {
                    // Navigate to Dashboard Fragment
                    true
                }
                R.id.navigation_menu -> {
                    // Navigate to Notifications Fragment
                    true
                }
                R.id.navigation_profile -> {
                    // Navigate to Notifications Fragment
                    true
                }
                else -> false
            }
        }
    }
}