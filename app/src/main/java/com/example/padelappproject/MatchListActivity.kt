package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Match
import com.google.firebase.firestore.FirebaseFirestore

class MatchListActivity: ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actitivity_match_list)

        var firstParticipant: String = ""

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val firestore = FirebaseFirestore.getInstance()
        val matchCollection = firestore.collection("matches")

        matchCollection.get().addOnSuccessListener { querySnapshot ->
            val matchList = mutableListOf<Match>()

            for(document in querySnapshot){
                val matchItem = document.toObject(Match::class.java)
                matchItem.titel = document.id
                matchList.add(matchItem)

                val participantsValues = matchItem.participants.values
                if (participantsValues.isNotEmpty()) {
                    // Set the first participant value to a property in your Match object
                   firstParticipant  = participantsValues.first().toString()
                }
            }

            adapter = MatchAdapter(matchList, firstParticipant){
                match -> navigateToDetailPage(match)
            }
            recyclerView.adapter = adapter
        }

        val buttonActivity1: Button = findViewById(R.id.buttonActivity1)
        val buttonActivity2: Button = findViewById(R.id.buttonActivity2)
        val buttonMatches: Button = findViewById(R.id.buttonMatches);
        buttonActivity1.setOnClickListener {
            finish()
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        buttonActivity2.setOnClickListener {
            finish()
            startActivity(Intent(this, CourtListActivity::class.java))
        }
        buttonMatches.setOnClickListener{
            finish()
            startActivity(Intent(this,MatchListActivity::class.java))
        }
    }
    private fun navigateToDetailPage(match:Match){
        val intent = Intent(this, MatchDetailActivity::class.java)
        intent.putExtra("MATCH_ITEM",match)
        startActivity(intent)
    }
}