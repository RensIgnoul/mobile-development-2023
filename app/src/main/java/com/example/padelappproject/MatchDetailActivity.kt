package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.text.capitalize
import com.example.padelappproject.Model.Match
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MatchDetailActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val matchItem = intent.getSerializableExtra("MATCH_ITEM") as Match
        val matchDayTextView = findViewById<TextView>(R.id.MatchStartDay)
        val matchTimeTextView = findViewById<TextView>(R.id.MatchStartTime)
        val courtTextView = findViewById<TextView>(R.id.MatchCourt)
        val player1TextView = findViewById<TextView>(R.id.PlayerName1)
        val player2TextView = findViewById<TextView>(R.id.PlayerName2)
        val player3TextView = findViewById<TextView>(R.id.PlayerName3)
        val player4TextView = findViewById<TextView>(R.id.PlayerName4)

        matchDayTextView.text = "There is a match this " + matchItem.startDay.capitalize()
        matchTimeTextView.text = "Match start time: "+matchItem.startTime

        val buttonJoin = findViewById<Button>(R.id.buttonJoin)
        val buttonRemove = findViewById<Button>(R.id.buttonRemove)
        val currentUserID = auth.currentUser?.uid
        val isCurrentUserParticipant = matchItem.participants.containsValue(currentUserID)
        var updatedParticipants = matchItem.participants.toMutableMap()

        if(currentUserID == updatedParticipants.get(updatedParticipants.keys.first())){
            buttonRemove.setOnClickListener {
                if(updatedParticipants.keys.last()!=updatedParticipants.keys.first()) {
                        updatedParticipants.remove(updatedParticipants.keys.last())
                        firestore.collection("matches").document(matchItem.titel)
                            .update("participants", updatedParticipants)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                            }
                        } else{
                       Toast.makeText(this,"Cannot remove the last person from a match", Toast.LENGTH_SHORT).show()
                }
                }
            } else {
            buttonRemove.visibility = View.GONE
        }


        if (currentUserID != null && !isCurrentUserParticipant ) {
            buttonJoin.setOnClickListener {
                val currentUserID = auth.currentUser?.uid

                if (currentUserID != null) {
                    val updatedParticipants = matchItem.participants.toMutableMap()
                    val nextPlayerKey = determineNextPlayerKey(updatedParticipants)
                    updatedParticipants[nextPlayerKey] = currentUserID

                    firestore.collection("matches").document(matchItem.titel)
                        .update("participants", updatedParticipants)
                        .addOnSuccessListener {
                            finish()
                            startActivity(Intent(this,MatchListActivity::class.java))

                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                    }
                }
            }
        } else {
            buttonJoin.visibility = View.GONE
        }

        val courtname = ""
        firestore.collection("courts").document(matchItem.court)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                courtTextView.text = documentSnapshot.getString("name")
            }

        val playersMap = matchItem.participants
        val player1 = playersMap["player1"] ?: ""
        val player2 = playersMap["player2"] ?: ""
        val player3 = playersMap["player3"] ?: ""
        val player4 = playersMap["player4"] ?: ""

        val playerlist = listOf(player1,player2,player3,player3,player4)
        val playerNameList: MutableList<String> = mutableListOf()
        for(player in playerlist){
            if (player.isNotEmpty()) { // Check if player is not empty
                firestore.collection("users").document(player)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val name = documentSnapshot.getString("name")
                        if (name != null) {
                            playerNameList.add(name)
                            player1TextView.text = playerNameList.firstOrNull()
                            if (playerNameList.size >= 2) {
                                player2TextView.text = playerNameList.getOrNull(1)
                            }
                            if (playerNameList.size >= 3) {
                                player3TextView.text = playerNameList.getOrNull(2)
                            }

                            // Check if playerNameList contains at least four names and set player4TextView
                            if (playerNameList.size >= 4) {
                                player4TextView.text = playerNameList.getOrNull(3)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle error
                    }
            }
        }
    }
    private fun determineNextPlayerKey(participants: Map<String, String?>): String {
        val existingPlayerKeys = participants.keys.filter { it.startsWith("player") }
        val highestNumber = existingPlayerKeys.mapNotNull { it.substringAfter("player").toIntOrNull() }.maxOrNull() ?: 1
        return "player${highestNumber + 1}"
    }
}