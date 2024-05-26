package com.example.deliveryadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.deliveryadmin.HomePageFragments.HomeFragment
import com.example.deliveryadmin.HomePageFragments.MenuFragment
import com.example.deliveryadmin.HomePageFragments.ProfileFragment
import com.example.deliveryadmin.HomePageFragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Replace fragment with HomeFragment
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_search -> {
                    // Replace fragment with CartFragment
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.navigation_menu -> {
                    // Replace fragment with SearchFragment
                    replaceFragment(MenuFragment())
                    true
                }
                R.id.navigation_profile -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}
