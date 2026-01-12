package com.example.klientutveckling_projekt

/**
 * Dataklass för uppgraderingar
 *
 * Specifierar de olika variablerna en uppgradering kan ha
 */
data class Upgrade(
    val id: Int,
    val name: String,
    val description: String,
    val multiplier: Double,
    val cost: Double,
    val metersPerSecondBonus: Double = 0.0,
    val flatClickBonus: Double = 0.0
)
