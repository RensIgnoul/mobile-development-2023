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
class MatchDayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_day)

        val courtItem = intent.getSerializableExtra("COURT_ITEM") as Court
        val courtNameTextView = findViewById<TextView>(R.id.CourtName)
        val locationTextView = findViewById<TextView>(R.id.Location)

        courtNameTextView.text = courtItem.name
        locationTextView.text = courtItem.address

        //val daysListView: ListView = findViewById(R.id.daysListView)

        // Fetch days from Firestore and populate the ListView
        //fetchDaysFromFirestore(courtItem.id, daysListView)

        val buttonGoToMon: Button = findViewById(R.id.buttonMon)
        buttonGoToMon.setOnClickListener {
            val intent = Intent(this, MatchCreateActivity::class.java)
            intent.putExtra("SELECTED_DAY", "monday")
            intent.putExtra("COURT",courtItem.id)
            startActivity(intent)
        }
        val buttonGoToTue: Button = findViewById(R.id.buttonTue)
        buttonGoToTue.setOnClickListener {
            val intent = Intent(this, MatchCreateActivity::class.java)
            intent.putExtra("SELECTED_DAY", "tuesday")
            intent.putExtra("COURT",courtItem.id)
            startActivity(intent)
        }
        val buttonGoToWed: Button = findViewById(R.id.buttonWed)
        buttonGoToWed.setOnClickListener {
            val intent = Intent(this, MatchCreateActivity::class.java)
            intent.putExtra("SELECTED_DAY", "wednesday")
            intent.putExtra("COURT",courtItem.id)
            startActivity(intent)
        }
        val buttonGoToThu: Button = findViewById(R.id.buttonThu)
        buttonGoToThu.setOnClickListener {
            val intent = Intent(this, MatchCreateActivity::class.java)
            intent.putExtra("SELECTED_DAY", "thursday")
            intent.putExtra("COURT",courtItem.id)
            startActivity(intent)
        }
        val buttonGoToFri: Button = findViewById(R.id.buttonFri)
        buttonGoToFri.setOnClickListener {
            val intent = Intent(this, MatchCreateActivity::class.java)
            intent.putExtra("SELECTED_DAY", "friday")
            intent.putExtra("COURT",courtItem.id)
            startActivity(intent)
        }
        val buttonGoToSat: Button = findViewById(R.id.buttonSat)
        buttonGoToSat.setOnClickListener {
            val intent = Intent(this, MatchCreateActivity::class.java)
            intent.putExtra("SELECTED_DAY", "saturday")
            intent.putExtra("COURT",courtItem.id)
            startActivity(intent)
        }
        //val buttonGoToMatch: Button = findViewById(R.id.buttonMatch)
        //buttonGoToMatch.setOnClickListener {
        //    val intent= Intent(this, MatchCreateActivity::class.java)
        //    startActivity(intent)
        }
    }
