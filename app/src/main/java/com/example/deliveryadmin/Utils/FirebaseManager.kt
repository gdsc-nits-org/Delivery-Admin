package com.example.deliveryadmin.Utils

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

object FirebaseManager {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val firebaseStorage: FirebaseStorage by lazy { Firebase.storage}
    private val firebaseFirestore : FirebaseFirestore by lazy{FirebaseFirestore.getInstance()}
    @JvmName("functionOfKotlin")
    fun getFirebaseAuth(): FirebaseAuth {
        return firebaseAuth
    }
    @JvmName("functionOfKotlin")
    fun getFirebaseDatabase(): FirebaseDatabase {
        return firebaseDatabase
    }
    @JvmName("functionOfKotlin")
    fun getFirebaseStorage(): FirebaseStorage {
        return firebaseStorage
    }
    @JvmName("functionOfKotlin")
    fun getFirebaseFirestore() : FirebaseFirestore{
        return firebaseFirestore
    }
}

