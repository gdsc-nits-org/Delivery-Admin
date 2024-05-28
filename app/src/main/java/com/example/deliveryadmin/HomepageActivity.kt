package com.example.deliveryadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomepageActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(
            bottomNavigationView, navController
        )
        bottomNavigationView.setOnItemSelectedListener { item->
            when(item.itemId) {
                R.id.home->{
                    navController.navigate(R.id.homePageFragment)
                    true
                }
                else -> false
            }
        }
    }
}