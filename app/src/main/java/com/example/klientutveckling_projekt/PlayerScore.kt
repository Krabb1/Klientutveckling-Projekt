package com.example.klientutveckling_projekt
/**
 * Representerar ett resultat i leaderboarden.
 *
 * @param rank Spelarens placering i leaderboarden
 * @param username Spelarens visningsnamn
 * @param score Spelarens poäng
 */
data class PlayerScore(
    val rank: Int,
    val username: String,
    val score: Long
)
