package com.example.padelappproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Match
import com.google.firebase.firestore.FirebaseFirestore

class MatchListActivity: ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actitivity_match_list)



        val firestore = FirebaseFirestore.getInstance()
        val matchCollection = firestore.collection("matches")

        matchCollection.get().addOnSuccessListener { querySnapshot ->
            val matchList = mutableListOf<Match>()

            for(document in querySnapshot){
                val matchItem = document.toObject(Match::class.java)
                matchList.add(matchItem)
            }

            adapter = MatchAdapter(matchList)
            recyclerView.adapter = adapter
        }
    }
}