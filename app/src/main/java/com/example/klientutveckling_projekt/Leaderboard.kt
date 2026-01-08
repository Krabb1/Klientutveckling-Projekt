package com.example.klientutveckling_projekt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Fragment som visar spelets leaderboard.
 *
 * Leaderboarden hämtas från Firebase Realtime Database och visas
 * i en RecyclerView soeterad efter högst till lägst
 *
 * Databastruktur som förväntas:
 * leaderboard/
 *   userID/
 *     username: String
 *     score: Long
 */
class Leaderboard : Fragment(R.layout.fragment_leaderboard) {

    // RecyclerView som visar leaderboard-listan
    private lateinit var recyclerView: RecyclerView

    // Adapter osm kopplar PlayerScore till RecyclerView
    private lateinit var adapter: LeaderboardAdapter

    private val database = FirebaseDatabase.getInstance()

    // Referens till leaderboard-noden i databasen
    private val leaderboardRef = database.getReference("leaderboard")

    /**
     * Anropas när fragmentets vy är skapad.
     * Startar upp RecyclerView och laddar leaderboard-data.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if (FirebaseAuth)

        recyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter

        loadLeaderboard()
    }

    /**
     * Hämtar leaderboard-data från Firebase
     *
     * - Hämtar de 20 bästa resultaten baserat på score
     * - Sorterar resultatet i fallande ordning
     * - Tilldelar rank (1, 2, 3, ...)
     * - Uppdaterar adaptern med resultat
     */
    private fun loadLeaderboard() {
        leaderboardRef
            .orderByChild("score")
            .limitToLast(20)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<PlayerScore>()

                    for (child in snapshot.children) {
                        val username = child.child("username").getValue(String::class.java) ?: "Unknown"
                        val score = child.child("score").getValue(Long::class.java) ?: 0L

                        list.add(PlayerScore(0, username, score))
                    }

                    // Realtime DB returnerar lägsta score först
                    list.sortByDescending { it.score }

                    list.forEachIndexed { index, player ->
                        list[index] = player.copy(rank = index + 1)
                    }
                    adapter.submitList(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Leaderboard", "Database error: ${error.message}")
                }
            })
    }

}






























