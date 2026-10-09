package com.example.fitforge

data class RecordBadminton(
    val totalSessions: Int,
    val totalMinutes: Int,
    val totalBadmintonCalories: Float,
    val sessionCalories: Float,
    val sessionType: String,
    val durationMinutes: Int
)
