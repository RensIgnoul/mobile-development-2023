package com.example.padelappproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Match
import com.google.firebase.firestore.FirebaseFirestore


class MatchAdapter(private val matchList: List<Match>, private val userId:String,
    private val onItemClick:(Match) -> Unit) :
    RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMatchId: TextView = itemView.findViewById(R.id.textViewMatchId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val matchItem = matchList[position]
        var name = "test"
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(matchItem.participants[matchItem.participants.keys.first()].toString())
            .get().addOnSuccessListener {
                documentSnapshot ->
                holder.textViewMatchId.text = documentSnapshot.getString("name").toString()+"'s Match"
            }.addOnFailureListener{
                name = "not working"
            }
        //holder.textViewMatchId.text = name+"'s Match";

        holder.itemView.setOnClickListener{
            onItemClick(matchItem)
        }
    }

    override fun getItemCount(): Int {
        return matchList.size
    }
}


