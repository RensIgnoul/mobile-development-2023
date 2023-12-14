package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userNameTextView = findViewById<TextView>(R.id.usernameTextView)
        val positionPreferenceTextView = findViewById<TextView>(R.id.positionpreference)
        val handPreferenceTextView = findViewById<TextView>(R.id.handpreference)
        val genderPreferenceTextView = findViewById<TextView>(R.id.genderpreference)
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

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

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val positionpref = "Preferred Position: " + documentSnapshot.getString("positionpreference")
                    val handpref = "Preferred hand: " + documentSnapshot.getString("handpreference")
                    val genderpref =
                        "Preferred Opponent's Gender: " + documentSnapshot.getString("genderpreference")
                    userNameTextView.text = documentSnapshot.getString("name")
                    positionPreferenceTextView.text = positionpref
                    handPreferenceTextView.text = handpref
                    genderPreferenceTextView.text = genderpref

                    // Load and display the user's profile image using Picasso
                    val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profileImageView)
                    }
                }
        }

        val button: Button = findViewById(R.id.editProfileButton)
        button.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }
}
