package com.example.fitforge

class BadmintonTracker(
    val userWeightKg: Float,
    val metValue: Float = 6.0f
) {

    private var totalSessions = 0
    private var totalMinutes = 0
    private var totalCalories = 0f
    private var startTimeMillis = 0L

    fun start() {
        totalSessions = 0
        totalMinutes = 0
        totalCalories = 0f
        startTimeMillis = System.currentTimeMillis()
    }

    fun stop() {
        // nothing special needed
    }

    fun addSession(durationMinutes: Int, sessionType: String): RecordBadminton {
        val caloriesPerMinute = when (sessionType) {
            "Single" -> 8.5f
            "Double" -> 6.5f
            "Drills" -> 7.5f
            else -> 7.0f
        }

        val sessionCalories = caloriesPerMinute * durationMinutes

        totalSessions++
        totalMinutes += durationMinutes
        totalCalories += sessionCalories

        return RecordBadminton(
            totalSessions = totalSessions,
            totalMinutes = totalMinutes,
            totalBadmintonCalories = totalCalories,
            sessionCalories = sessionCalories,
            sessionType = sessionType,
            durationMinutes = durationMinutes
        )
    }
}

