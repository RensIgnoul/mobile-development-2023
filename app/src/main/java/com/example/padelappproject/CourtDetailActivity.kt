package com.example.padelappproject

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.padelappproject.Model.Court
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
class CourtDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_court_detail)

        val courtItem = intent.getSerializableExtra("COURT_ITEM") as Court
        val courtNameTextView = findViewById<TextView>(R.id.CourtName)
        val locationTextView = findViewById<TextView>(R.id.Location)

        courtNameTextView.text = courtItem.name
        locationTextView.text = courtItem.address

        val daysListView: ListView = findViewById(R.id.daysListView)

        // Fetch days from Firestore and populate the ListView
        fetchDaysFromFirestore(courtItem.id, daysListView)

        val buttonGoToOtherActivity: Button = findViewById(R.id.buttonGoToOtherActivity)
        buttonGoToOtherActivity.setOnClickListener {
            val intent = Intent(this, DayDetailActivity::class.java)
            intent.putExtra("EXTRA_STRING", "ABC")
            startActivity(intent)
        }
    }
    private fun fetchDaysFromFirestore(courtId: String, listView: ListView) {
        val firestore = FirebaseFirestore.getInstance()
        val daysCollection = firestore.collection("courts").document(courtId).collection("timeslots")

        daysCollection.orderBy("nr").get()
            .addOnSuccessListener { documents ->
                val daysList = ArrayList<String>()

                for (document in documents) {
                    // Assuming the document ID is still the day name
                    val day = document.id
                    daysList.add(day)
                }

                // Create an ArrayAdapter and set it to the ListView
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, daysList)
                listView.adapter = arrayAdapter
            }
            .addOnFailureListener { e ->
                // Handle failures
                // You might want to display an error message or log the error
            }
    }
}