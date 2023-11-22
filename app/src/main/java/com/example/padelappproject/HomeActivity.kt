package com.example.padelappproject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Match
import com.example.padelappproject.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Fetch and display user's matches
        fetchUserMatches()
    }

    private fun fetchUserMatches() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            // user.matches contains the list of match IDs
                            // Populate the homeactivity list
                            setupRecyclerView(user.matches)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failures
                }
        }
    }

    private fun setupRecyclerView(matchIds: List<String>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = MatchAdapter(matchIds)
        recyclerView.adapter = adapter
    }
}
