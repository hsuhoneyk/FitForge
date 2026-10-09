package com.example.fitforge

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class LocationManager(
    private val fragment: Fragment,
    private val onLocation: (location: Location) -> Unit
) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    private val permissionLauncher =
        fragment.registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }
            if (granted) startUpdates()
            else Toast.makeText(fragment.requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }

    fun start() {
        val fineGranted = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
            return
        }
        //if permission is granted
        startUpdates()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startUpdates() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()
        //listener of location changes
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let(onLocation)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        locationCallback?.let {
            fusedClient.requestLocationUpdates(
                LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    5000L
                ).build(),
                it,
                Looper.getMainLooper()
            )
        }
    }

    fun stop() {
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
    }
}
//same for cycling {