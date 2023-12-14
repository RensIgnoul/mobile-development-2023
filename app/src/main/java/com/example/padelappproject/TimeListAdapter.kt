package com.example.padelappproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Match
import com.example.padelappproject.Model.Times
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class TimeListAdapter (
    private val timesList : List<Times>,
    private val courtId: String,
    private val dayString: String,
    private val isMatch: Boolean
    ):RecyclerView.Adapter<TimeListAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeValue: TextView = itemView.findViewById(R.id.TimeValue)
        val reserved: TextView = itemView.findViewById(R.id.Reserved)
        val resButton: Button = itemView.findViewById(R.id.resButton) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.timeslot_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:
                                  ViewHolder, position: Int) {
        val timeslotItem = timesList[position]
        holder.timeValue.text = timeslotItem.time
        holder.reserved.text = if (timeslotItem.reserved) "Cannot be reserved" else "Can be reserved"

        holder.resButton.visibility = if (!timeslotItem.canInitiateReservation || timeslotItem.reserved || position >= itemCount - 2) View.GONE else View.VISIBLE

        holder.resButton.setOnClickListener {
            // When the button is clicked, update the reserved value in the Firestore document

            val firestore = FirebaseFirestore.getInstance()
            val collection = firestore.collection("courts")
                .document(courtId)
                .collection("timeslots")
                .document(dayString)
                .collection("times")

            // Update the reserved field to true
            collection.document(timeslotItem.time)
                .update(mapOf("reserved" to true,
                    "reservedBy" to FirebaseAuth.getInstance().currentUser?.uid,
                    "canInitiateReservation" to false,
                    "isMatch" to isMatch))
                .addOnSuccessListener {
                    timeslotItem.reserved = true
                    notifyItemChanged(position)

                    if(isMatch){
                        val match = createMatch(position)
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("matches").add(match)
                    }
                    //reservePreviousTwoSlots(collection,position)
                    reserveNextTwoSlots(collection, position + 1)
                    reserveNextTwoSlots(collection, position + 2)
                }
                .addOnFailureListener { e ->
                }
        }
    }

    override fun getItemCount(): Int {
        return timesList.size
    }

    private fun reserveNextTwoSlots(collection: CollectionReference, position: Int) {
        // Ensure position is within bounds
        if (position in 0 until timesList.size) {
            val nextTimeslot = timesList[position]

            collection.document(nextTimeslot.time)
                .update(mapOf("reserved" to true,
                    "reservedBy" to FirebaseAuth.getInstance().currentUser?.uid))
                .addOnSuccessListener {
                    nextTimeslot.reserved = true
                    notifyItemChanged(position)
                }
                .addOnFailureListener { e ->
                }
        }
    }
    private fun reservePreviousTwoSlots(collection: CollectionReference, position: Int) {
        // Ensure position is within bounds
        if (position in 2 until timesList.size) {
            val previousTimeslot = timesList[position - 2]

            // Update the reserved and reservationInitiator fields to true and false, respectively
            collection.document(previousTimeslot.time)
                .update(
                    mapOf(
                        "canInitiateReservation" to false
                    )
                )
                .addOnSuccessListener {
                    // Update successful
                    previousTimeslot.canInitiateReservation = false

                    // Notify the adapter that the item has changed
                    notifyItemChanged(position - 2)
                }
                .addOnFailureListener { e ->
                    // Handle failures
                }

            val secondPreviousTimeslot = timesList[position - 1]

            // Update the reserved and reservationInitiator fields to true and false, respectively
            collection.document(secondPreviousTimeslot.time)
                .update(
                    mapOf(
                        "canInitiateReservation" to false
                    )
                )
                .addOnSuccessListener {
                    secondPreviousTimeslot.canInitiateReservation = false

                    // Notify the adapter that the item has changed
                    notifyItemChanged(position - 1)
                }
                .addOnFailureListener { e ->
                    // Handle failures
                }
        }
    }
    private fun createMatch(position: Int):Match {
        val startDay: String = dayString
        val startTime: String = timesList[position].time
        val auth = FirebaseAuth.getInstance()
        val court = courtId
        return Match(startDay,
            startTime,
            mapOf("player1" to (auth.currentUser?.uid ?: "")),
            courtId
        )
    }
}