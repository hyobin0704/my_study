package com.ex.firebase

import androidx.multidex.MultiDexApplication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage
        var email: String? = null
        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser
            return currentUser?.let {
                email = currentUser.email
                if (currentUser.isEmailVerified) {
                    true
                } else {
                    false
                }
            } ?: let {
                false
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }
}