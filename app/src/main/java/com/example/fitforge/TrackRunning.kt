package com.example.fitforge

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException

class TrackRunning : Fragment(), OnMapReadyCallback {
    private lateinit var locationManager: LocationManager
    private val mapController = MapTrackingController()
    private lateinit var tvdistance: TextView
    private lateinit var tvspeed: TextView
    private lateinit var tvcalories: TextView
    private lateinit var etRouteName: EditText
    private lateinit var stats: RecordTrack

    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_running, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvdistance = view.findViewById(R.id.tvdistance)
        tvspeed = view.findViewById(R.id.tvspeed)
        tvcalories = view.findViewById(R.id.tvcal)

        val userWeight = UserPrefs.getWeight(requireContext())
        Log.d("RUNNING", "User weight: $userWeight kg")
        //create instant obj of ActivityTracker
        val statsTracker = TrackerActivity(
            userWeightKg = if (userWeight > 0) userWeight.toFloat() else 70f,
            metValue = 9.8f //for running
        )
        //initialize mapFragment
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.runningMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //call back function onLocation will update location when it changes
        locationManager = LocationManager(this) { location ->
            // Map update
            mapController.onLocationUpdate(location)

            // Stats update get the Track Record
            stats = statsTracker.onLocationUpdate(location)
            updateUi(stats)

        }

        // FAB click: start tracking
        val fabTrackRun = view.findViewById<FloatingActionButton>(R.id.fabruntrack)
        fabTrackRun.setOnClickListener {
            val tracking = mapController.toggleTracking()
            if (tracking) {
                statsTracker.start()
                locationManager.start()
                locationManager.requestLocationUpdates()
            } else {
                locationManager.stop()
                statsTracker.stop()
            }

            fabTrackRun.setImageResource(
                if (tracking) R.drawable.cross
                else R.drawable.runicon
            )
        }
        etRouteName = view.findViewById(R.id.tietRouteName)
        val btnSave_Run = view.findViewById<Button>(R.id.btnSaveRun)
        btnSave_Run.setOnClickListener {
            saveRunningData(stats)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mapController.attachMap(map)

        // Show default location before tracking
        val defaultLatLng = LatLng(16.8409, 96.1735) // e.g., Yangon
        mapController.setInitialLocation(defaultLatLng)
    }

    private fun updateUi(stats: RecordTrack) {
        tvspeed.text = String.format("%.1f km/h", stats.speedkilometerh)
        tvdistance.text = String.format("%.2f km", stats.distancem / 1000)
        tvcalories.text = "${stats.cal} kcal"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager.stop()
    }

    private fun saveRunningData(runLogs: RecordTrack) {
        val userID = UserPrefs.getUserID(requireContext())
        val URL_Root = "http://10.0.2.2/hhk_FitTest/RunningDAO.php"

        //Create volley string request
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_Root,
            Response.Listener<String> { response ->
                try {
                    //Log.d("Response", response)
                    if (response == "true") {
                        Toast.makeText(
                            requireContext(),
                            "Running Logs Data Saved, Successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${volleyError.message ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("phpFunction", "Save_RunData")
                params.put("User_ID", userID.toString())
                params.put("Route_Name", etRouteName.text.toString())
                params.put("Total_Distance", runLogs.distancem.toString())
                params.put("Speed", runLogs.speedkilometerh.toString())
                params.put("Calorie_burn", runLogs.cal.toString())
                //Log.e("Parameter", "ParameterData: $params")
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}