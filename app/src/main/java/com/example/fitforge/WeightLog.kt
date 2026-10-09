package com.example.fitforge

data class WeightLog(
    val exercise: String,
    val weight: Float,
    val reps: Int,
    val sets: Int,
    val volume: Float,
    val calories: Float,
    val date: String
)