package com.example.padelappproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Court

class CourtListAdapter(
    private val courtList: List<Court>,
    private val onItemClick: (Court) -> Unit
) : RecyclerView.Adapter<CourtListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courtName: TextView = itemView.findViewById(R.id.CourtName)
        val location: TextView = itemView.findViewById(R.id.Location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.court_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courtItem = courtList[position]
        holder.courtName.text = courtItem.name
        holder.location.text = courtItem.address

        // Set click listener for item
        holder.itemView.setOnClickListener {
            onItemClick(courtItem)
        }
    }

    override fun getItemCount(): Int {
        return courtList.size
    }
}
