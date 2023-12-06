package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity:ComponentActivity() {
    private lateinit var positionPreferenceSpinner: Spinner
    private lateinit var handPreferenceSpinner: Spinner
    private lateinit var genderPreferenceSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        var userHand =""
        var userPos =""
        var userGender = ""
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    userHand = documentSnapshot.getString("handpreference").toString()
                    userPos = documentSnapshot.getString("positionpreference").toString()
                    userGender = documentSnapshot.getString("genderpreference").toString()

                    positionPreferenceSpinner = findViewById(R.id.positionPreferenceSpinner)
                    handPreferenceSpinner = findViewById(R.id.handPreferenceSpinner)
                    genderPreferenceSpinner = findViewById(R.id.genderPreferenceSpinner)


                    populateSpinner(positionPreferenceSpinner, R.array.position_options, userPos)
                    populateSpinner(handPreferenceSpinner, R.array.hand_options, userHand)
                    populateSpinner(genderPreferenceSpinner, R.array.gender_options, userGender)

                }
        }

        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            savePreferences()
        }
    }

    private fun populateSpinner(spinner: Spinner, optionsArrayResourceId: Int, defaultSelection: String) {
        val options = resources.getStringArray(optionsArrayResourceId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set default selection based on the value from Firestore
        val defaultIndex = options.indexOf(defaultSelection)
        if (defaultIndex != -1) {
            spinner.setSelection(defaultIndex)
        }
    }
    private fun savePreferences() {
        val selectedPosition = positionPreferenceSpinner.selectedItem.toString()
        val selectedHand = handPreferenceSpinner.selectedItem.toString()
        val selectedGender = genderPreferenceSpinner.selectedItem.toString()

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Update the user preferences in the Firestore database
            val userRef = db.collection("users").document(currentUser.uid)
            userRef
                .update(
                    "positionpreference", selectedPosition,
                    "handpreference", selectedHand,
                    "genderpreference", selectedGender
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Preferences updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,ProfileActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update preferences: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}