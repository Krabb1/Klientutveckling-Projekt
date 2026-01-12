package com.example.klientutveckling_projekt

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID
import androidx.core.content.edit

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

        recyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter

        loadLeaderboard()

        view.findViewById<Button>(R.id.createAccountButton)
            .setOnClickListener { showCreateAccountDialog() }
    }

    private fun getOrCreateUserId(): String {
        val prefs = requireContext()
            .getSharedPreferences("leaderboard_prefs", Context.MODE_PRIVATE)

        var userId = prefs.getString("user_id", null)

        if (userId == null) {
            userId = UUID.randomUUID().toString()
            prefs.edit { putString("user_id", userId) }
        }
        return userId
    }

    private fun showCreateAccountDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Username"

        AlertDialog.Builder(requireContext())
            .setTitle("Create account")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val username = editText.text.toString().trim()
                if (username.isNotEmpty()) {saveUser(username)}
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    private fun saveUser(username: String) {
        val userId = getOrCreateUserId()

        val userData = mapOf(
            "username" to username,
            "score" to 0
        )
        leaderboardRef.child(userId).setValue(userData)
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






























