package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userNameTextView = findViewById<TextView>(R.id.usernameTextView)
        val positionPreferenceTextView = findViewById<TextView>(R.id.positionpreference)
        val handPreferenceTextView = findViewById<TextView>(R.id.handpreference)
        val genderPreferenceTextView = findViewById<TextView>(R.id.genderpreference)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val positionpref = "Preferred Position: " + documentSnapshot.getString("positionpreference")
                    val handpref = "Preferred hand: "+documentSnapshot.getString("handpreference")
                    val genderpref = "Preferred Opponent's Gender: "+ documentSnapshot.getString("genderpreference")
                    userNameTextView.text = documentSnapshot.getString("name")
                    positionPreferenceTextView.text = positionpref
                    handPreferenceTextView.text = handpref
                    genderPreferenceTextView.text = genderpref
                }
        }

        val button : Button = findViewById(R.id.editProfileButton)
        button.setOnClickListener{
            finish()
            startActivity(Intent(this,EditProfileActivity::class.java))
        }
    }

}
