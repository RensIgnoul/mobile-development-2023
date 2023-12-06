package com.example.padelappproject

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileActivity:ComponentActivity() {
    private lateinit var positionPreferenceSpinner: Spinner
    private lateinit var handPreferenceSpinner: Spinner
    private lateinit var genderPreferenceSpinner: Spinner
    private lateinit var profileImageView: ImageView
    private lateinit var chooseImageButton: Button

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        profileImageView = findViewById(R.id.profileImageView)
        chooseImageButton = findViewById(R.id.chooseImageButton)

        Picasso.get().load(R.drawable.ic_launcher_foreground).into(profileImageView)

        // Set a default profile image
        profileImageView.setImageResource(R.drawable.ic_launcher_foreground)

        // Set a click listener for the "Choose Image" button
        chooseImageButton.setOnClickListener {
            openImageChooser()
        }

        var userHand =""
        var userPos =""
        var userGender = ""
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profileImageView)
                    }

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
            uploadImage()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
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
                    finish()
                    startActivity(Intent(this,ProfileActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update preferences: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun uploadImage() {

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${currentUser?.uid}")
            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        // Update the user's profile document with the image URL
                        updateUserProfileImage(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload image: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserProfileImage(imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)
            userRef
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update profile image: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}