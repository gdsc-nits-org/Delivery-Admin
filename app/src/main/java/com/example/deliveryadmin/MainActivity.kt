package com.example.deliveryadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.deliveryadmin.HomePageFragments.HomeFragment
import com.example.deliveryadmin.HomePageFragments.MenuFragment
import com.example.deliveryadmin.HomePageFragments.ProfileFragment
import com.example.deliveryadmin.HomePageFragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        }
}
