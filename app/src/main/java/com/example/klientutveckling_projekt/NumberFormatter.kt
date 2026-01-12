package com.example.klientutveckling_projekt

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Formatterar större nummer med scientific notation
 *
 * Exempel:
 *  - 950        -> "950"
 *  - 1_200      -> "1.20k"
 *  - 1_500_000  -> "1.50M"
 *  - 2_000_000_000 -> "2.00B"
 */
fun formatScientific(value: Double): String {
    if (value < 1000) return value.toInt().toString()

    val units = listOf("", "k", "M", "B", "T", "Qa", "Qi")
    val exponent = floor(log10(value) / 3)
        .toInt()
        .coerceAtMost(units.lastIndex)

    val scaled = value / 10.0.pow(exponent * 3)

    return String.format("%.2f%s", scaled, units[exponent])
}
