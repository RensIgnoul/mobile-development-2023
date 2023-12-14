package com.example.padelappproject

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Times
import com.google.firebase.firestore.FirebaseFirestore

class MatchCreateActivity:ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimeListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_create)

        val dayString = intent.getStringExtra("SELECTED_DAY")
        val courtId = intent.getStringExtra("COURT")
        val textView = findViewById<TextView>(R.id.buttonTest)
        textView.text = dayString

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("courts").document(courtId!!)
            .collection("timeslots").document(dayString!!).collection("times")

        collection.get().addOnSuccessListener { querySnapshot ->
            val timeslotList = mutableListOf<Times>()

            for (document in querySnapshot) {
                val time = Times(document.id, document.getBoolean("reserved")!!)
                timeslotList.add(time)
            }

            for (time in timeslotList) {
                Log.d("Firestore", "Document ID: ${time}, Reserved: ${time.reserved}")
            }
            adapter = TimeListAdapter(timeslotList, courtId, dayString,true)

            recyclerView.adapter = adapter
        }
    }
}