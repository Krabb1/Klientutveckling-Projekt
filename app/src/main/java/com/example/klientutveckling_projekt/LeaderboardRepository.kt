package com.example.klientutveckling_projekt

import android.content.Context
import com.google.firebase.database.FirebaseDatabase

/**
 * Hanterar all kommunikation med leaderboarden i Firebase.
 * Använder ett lokalt UUID för att identifiera användaren
 */
class LeaderboardRepository(context: Context) {

    private val database = FirebaseDatabase.getInstance().getReference("leaderboard")
    private val prefs = context.getSharedPreferences("leaderboard_prefs", Context.MODE_PRIVATE)
    private val userId: String = prefs.getString("user_id", null)
        ?: "Guest"

    // Uppdaterar användarens score i Firebase
    fun updateScore(score: Double) {
        database.child(userId).child("score").setValue(score)
    }

}