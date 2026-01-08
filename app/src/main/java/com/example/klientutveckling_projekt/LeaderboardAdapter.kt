package com.example.klientutveckling_projekt

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView-adapter för leaderboarden.
 *
 * Ansvarar för att binda PlayerScore-objekt till
 * item_leaderboard_row.xml.
 */
class LeaderboardAdapter :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    // Lista med leaderboard-poster
    private val items = mutableListOf<PlayerScore>()

    // Uppdaterar listan med nya leaderboard-poster.
    fun submitList(list: List<PlayerScore>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    // ViewHolder som håller vy-referenser för en leaderboard-rad.
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankText: TextView = view.findViewById(R.id.rankText)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val scoreText: TextView = view.findViewById(R.id.scoreText)
    }

    // Skapar en ny ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_row, parent, false)
        return ViewHolder(view)
    }

    // Binder data till en ViewHolder för en given position.
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = items[position]
        holder.rankText.text = "#${player.rank}"
        holder.nameText.text = player.username
        holder.scoreText.text = player.score.toString()
    }

    // Returnerar antalet poster i leaderboarden
    override fun getItemCount(): Int = items.size
}






























