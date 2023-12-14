package com.example.padelappproject

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
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

        matchDayTextView.text = "There is a match this " + matchItem.startDay.capitalize()
        matchTimeTextView.text = matchItem.startTime

        val buttonJoin = findViewById<Button>(R.id.buttonJoin)
        val currentUserID = auth.currentUser?.uid
        val isCurrentUserParticipant = matchItem.participants.containsValue(currentUserID)

        if (currentUserID != null && !isCurrentUserParticipant) {
            buttonJoin.setOnClickListener {
                val currentUserID = auth.currentUser?.uid

                if (currentUserID != null) {
                    val updatedParticipants = matchItem.participants.toMutableMap()
                    val nextPlayerKey = determineNextPlayerKey(updatedParticipants)
                    updatedParticipants[nextPlayerKey] = currentUserID

                    firestore.collection("matches").document(matchItem.titel)
                        .update("participants", updatedParticipants)
                        .addOnSuccessListener {
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