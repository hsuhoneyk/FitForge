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

class CyclingTrack : Fragment(), OnMapReadyCallback {
    private lateinit var locationManager: LocationManager
    private val mapController = MapTrackingController()
    private lateinit var tvCyclingDistance: TextView
    private lateinit var tvCyclingSpeed: TextView
    private lateinit var tvCyclingCalories: TextView
    private lateinit var etCyclingRouteName: EditText
    private lateinit var stats: RecordTrack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_cycling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCyclingDistance= view.findViewById(R.id.tvcycledistance)
        tvCyclingSpeed = view.findViewById(R.id.tvcyclespeed)
        tvCyclingCalories = view.findViewById(R.id.tvcyclecal)

        val userWeight = UserPrefs.getWeight(requireContext())
        Log.d("WEIGHT_CHECK", "User weight from prefs: $userWeight kg")
        //create instant obj of ActivityTracker
        val statsTracker = TrackerActivity(
            userWeightKg = 70f,
            metValue = 9.8f //for cycling
        )
        //initialize mapFragment
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.cvmapcycle) as SupportMapFragment
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
        val fabTrackCycle = view.findViewById<FloatingActionButton>(R.id.fabcycletrack)
        fabTrackCycle.setOnClickListener {
            val tracking = mapController.toggleTracking()
            if (tracking) {
                startTime = System.currentTimeMillis()
                statsTracker.start()
                locationManager.start()
                locationManager.requestLocationUpdates()
            } else {
                locationManager.stop()
                statsTracker.stop()
            }

            fabTrackCycle.setImageResource(
                if (tracking) R.drawable.cross
                else R.drawable.cycling
            )
        }
        etCyclingRouteName = view.findViewById(R.id.tietcycleRouteName)
        val btnSave_cycle = view.findViewById<Button>(R.id.btnSaveCycle)
        btnSave_cycle.setOnClickListener {
            saveCyclingData(stats)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mapController.attachMap(map)

        // Show default location before tracking
        val defaultLatLng = LatLng(16.8409, 96.1735) // e.g., Yangon
        mapController.setInitialLocation(defaultLatLng)
    }

    private var startTime: Long=0
    private fun updateUi(stats: RecordTrack) {
        tvCyclingSpeed.text = String.format("%.1f km/h", stats.speedkilometerh)
        tvCyclingDistance.text = String.format("%.2f km", stats.distancem / 1000)


        val userWeight = UserPrefs.getWeight(requireContext())
        val testCalories = if (stats.cal == 0 && stats.distancem > 0) {
            // Manual calculation
            val durationHours = (System.currentTimeMillis() - startTime) / (1000f * 60f * 60f)
            (9.8f * userWeight * durationHours).toInt()
        } else {
            stats.cal
        }

        tvCyclingCalories.text = "$testCalories kcal"

        // Also log what's happening
        Log.d("CALORIES", "stats.cal=${stats.cal}, testCalories=$testCalories, distance=${stats.distancem}")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        locationManager.stop()
    }

    private fun saveCyclingData(cyclingLogs: RecordTrack) {
        val userID = UserPrefs.getUserID(requireContext())
        val URL_Root = "http://10.0.2.2/hhk_FitTest/CyclingDAO.php"

        //Create volley string request
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_Root,
            Response.Listener<String> { response ->
                try {
                    //Log.d("Response", response)
                    if (response == "true") {
                        Toast.makeText(
                            requireContext(),
                            "Cycling Logs Data Has Been Saved, Successfully.",
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
                params.put("phpFunction", "Save_cycleData")
                params.put("User_ID", userID.toString())
                params.put("Route_Name", etCyclingRouteName.text.toString())
                params.put("Total_Distance", cyclingLogs.distancem.toString())
                params.put("Speed", cyclingLogs.speedkilometerh.toString())
                params.put("Calorie_burn", cyclingLogs.cal.toString())

                Log.d("DISTANCE_DEBUG", "Distance being sent: ${cyclingLogs.distancem}")
                //Log.e("Parameter", "ParameterData: $params")
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}