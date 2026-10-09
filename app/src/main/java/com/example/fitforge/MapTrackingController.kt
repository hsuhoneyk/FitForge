package com.example.fitforge

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import android.location.Location

class MapTrackingController {

    private var googleMap: GoogleMap? = null
    private val pathPoints = mutableListOf<LatLng>()
    private var tracking = false

    fun attachMap(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    /**
     * Set a default location before tracking starts
     */
    fun setInitialLocation(latLng: LatLng, zoom: Float = 15f, title: String = "Start") {
        googleMap?.apply {
            clear()
            addMarker(MarkerOptions().position(latLng).title(title))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    /**
     * Call this only when tracking is enabled
     */
    fun onLocationUpdate(location: Location) {
        if (!tracking) return

        val latLng = LatLng(location.latitude, location.longitude)
        pathPoints.add(latLng)

        googleMap?.apply {
            addPolyline(
                PolylineOptions().addAll(pathPoints).width(8f)
            )
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    fun toggleTracking(): Boolean {
        tracking = !tracking
        if (!tracking) pathPoints.clear()
        return tracking
    }
}
//same for cycling