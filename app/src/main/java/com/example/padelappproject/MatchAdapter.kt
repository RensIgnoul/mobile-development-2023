package com.example.padelappproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.padelappproject.Model.Match


class MatchAdapter(private val matchList: List<Match>) :
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


    }

    override fun getItemCount(): Int {
        return matchList.size
    }
}


