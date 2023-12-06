package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Court
import com.google.firebase.firestore.FirebaseFirestore

class CourtListActivity:ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourtListAdapter
    private lateinit var courtId : String;

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_court_list)
            //setContentView(R.layout.activity_court_list)

            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)


            // Set up button click listeners
            val buttonActivity1: Button = findViewById(R.id.buttonActivity1)
            val buttonActivity2: Button = findViewById(R.id.buttonActivity2)

            buttonActivity1.setOnClickListener {
                finish()
                startActivity(Intent(this, ProfileActivity::class.java))
            }

            buttonActivity2.setOnClickListener {
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            // Initialize Firestore
            val firestore = FirebaseFirestore.getInstance()
            val courtCollection = firestore.collection("courts")

            // Query Firestore for all documents in the 'courts' collection
            courtCollection.get().addOnSuccessListener { querySnapshot ->
                val courtList = mutableListOf<Court>()

                for (document in querySnapshot) {
                    // Convert each document to a CourtItem object
                    val courtItem = document.toObject(Court::class.java)
                    courtItem.id = document.id
                    courtList.add(courtItem)
                }

                // Create and set up the RecyclerView adapter
                adapter = CourtListAdapter(courtList) { selectedCourt ->
                    // Handle item click

                    navigateToDetailPage(selectedCourt)
                }
                recyclerView.adapter = adapter
            }
        }

        private fun navigateToDetailPage(selectedCourt: Court) {
            val intent = Intent(this, CourtDetailActivity::class.java)
            intent.putExtra("COURT_ITEM", selectedCourt)
            startActivity(intent)
        }
}

