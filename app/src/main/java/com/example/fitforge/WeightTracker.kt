package com.example.fitforge

class WeightTracker(
    val userWeightKg: Float,
    val metValue: Float = 6.0f // moderate weightlifting MET
) {

    private var totalSets = 0
    private var totalVolume = 0f
    private var totalCalories = 0f

    private var startTimeMillis = 0L

    fun start() {
        totalSets = 0
        totalVolume = 0f
        totalCalories = 0f
        startTimeMillis = System.currentTimeMillis()
    }

    fun stop() {
        // nothing special needed
    }

    fun addSet(weight: Float, reps: Int, sets: Int = 1): RecordWeight {

        val volume = weight * reps * sets
        totalVolume += volume
        totalSets += sets

        // Time since start (hours)
        val elapsedHours =
            (System.currentTimeMillis() - startTimeMillis) / 3600000f

        // Calories (same formula as running but different MET)
        totalCalories = metValue * userWeightKg * elapsedHours

        return RecordWeight(
            totalSets = totalSets,
            totalVolume = totalVolume,
            calories = totalCalories
        )
    }
}