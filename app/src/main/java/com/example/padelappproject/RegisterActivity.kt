package com.example.padelappproject

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.padelappproject.Model.Match
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class RegisterActivity : ComponentActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextName: EditText
    private lateinit var buttonRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextEmail = findViewById(R.id.editTextEmailRegister)
        editTextPassword = findViewById(R.id.editTextPasswordRegister)
        editTextName = findViewById(R.id.editTextNameRegister)
        buttonRegister = findViewById(R.id.buttonRegister)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val name = editTextName.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please enter email, password, and name", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Get the UID of the registered user
                    val userId = auth.currentUser?.uid

                    // Create a user document in Firestore with name, UID, and an empty list for matches
                    if (userId != null) {
                        val user = hashMapOf(
                            "name" to name,
                            "uid" to userId,
                            "matches" to mutableListOf<String>() // Initialize with an empty list of match IDs
                        )

                        firestore.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                // Add a test match for the newly registered user
                                addTestMatchForUser(userId)

                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Registration failed: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addTestMatchForUser(userId: String) {
        val testMatch = createTestMatch(userId)

        // Add the test match to the "matches" collection and store the reference in the user's document
        firestore.collection("matches")
            .add(testMatch)
            .addOnSuccessListener { matchDocument ->
                // Update the user's document with the reference to the match
                val matchId = matchDocument.id
                firestore.collection("users")
                    .document(userId)
                    .update("matches", FieldValue.arrayUnion(matchId))
                    .addOnSuccessListener {
                        // Log or handle success if needed
                    }
                    .addOnFailureListener { e ->
                        // Handle the failure
                        Toast.makeText(this, "Error updating user document: $e", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                // Handle the failure
                Toast.makeText(this, "Error adding test match: $e", Toast.LENGTH_SHORT).show()
            }
    }



    private fun createTestMatch(userId: String): Match {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val startDate = dateFormat.format(currentTimeMillis)

        // Create a test match with the user as player1 and other default values
        return Match(
            startDateTime = startDate,
            participants = mapOf("position1" to userId),
            location = "Here",
            court = "DefaultCourtId" // Provide the actual default court ID
        )
    }
}
