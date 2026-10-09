package com.example.fitforge

import android.location.Location

class TrackerActivity(
    private val userWeightKg: Float,
    private val metValue: Float
) {
    private var startTime: Long = 0
    private var lastLocation: Location? = null
    private var totalDistanceMeters: Float = 0f
    private var currentSpeedKmh: Float = 0f
    private var isTracking = false

    fun start() {
        isTracking = true
        startTime = System.currentTimeMillis()
        totalDistanceMeters = 0f
        lastLocation = null
    }

    fun stop() {
        isTracking = false
    }

    fun onLocationUpdate(location: Location): RecordTrack {
        if (!isTracking) {
            return RecordTrack(0f, 0f, 0)
        }

        // Calculate distance
        lastLocation?.let { last ->
            val distance = last.distanceTo(location)
            if (distance > 0 && distance < 500) { // Filter invalid jumps
                totalDistanceMeters += distance
            }
        }
        lastLocation = location

        // Calculate speed
        currentSpeedKmh = location.speed * 3.6f

        // ✅ CALCULATE CALORIES (THE FIX)
        val currentTime = System.currentTimeMillis()
        val durationHours = (currentTime - startTime) / (1000f * 60f * 60f)
        val calories = (metValue * userWeightKg * durationHours).toInt()

        println("CALC: durationHours=$durationHours, weight=$userWeightKg, met=$metValue, calories=$calories")


        return RecordTrack(
            distancem = totalDistanceMeters,
            speedkilometerh = currentSpeedKmh,
            cal = calories
        )
    }
}