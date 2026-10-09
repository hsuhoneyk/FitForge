package com.example.fitforge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException

class BadmintonTrack : Fragment() {

    // UI Components
    private lateinit var chipGroupSessionType: ChipGroup
    private lateinit var chipSingles: Chip
    private lateinit var chipDoubles: Chip
    private lateinit var chipDrills: Chip
    private lateinit var etDuration: TextInputEditText
    private lateinit var btnAddSession: Button
    private lateinit var btnSave: Button
    private lateinit var totalSessionsText: TextView
    private lateinit var totalMinutesText: TextView
    private lateinit var totalCaloriesText: TextView
    private lateinit var badmintonProgressBar: ProgressBar
    private lateinit var progressText: TextView

    private lateinit var badmintonTracker: BadmintonTracker

    // Current session data
    private var currentSessionType = ""
    private var currentDuration = 0
    private var currentCalories = 0f

    private var totalSessions = 0
    private var totalMinutes = 0
    private var totalCalories = 0f
    private val goalSessions = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_track_badminton, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userWeight = UserPrefs.getWeight(requireContext())

        badmintonTracker = BadmintonTracker(
            userWeightKg = userWeight.toFloat(),
            metValue = 6.0f
        )
        badmintonTracker.start()

        // Initialize views
        chipGroupSessionType = view.findViewById(R.id.chipGroupSessionType)
        chipSingles = view.findViewById(R.id.chipSingles)
        chipDoubles = view.findViewById(R.id.chipDoubles)
        chipDrills = view.findViewById(R.id.chipDrills)
        etDuration = view.findViewById(R.id.etDuration)
        btnAddSession = view.findViewById(R.id.btnCalculate)
        btnSave = view.findViewById(R.id.btnSaveSession)
        totalSessionsText = view.findViewById(R.id.totalSessionsText)
        totalMinutesText = view.findViewById(R.id.totalMinutesText)
        totalCaloriesText = view.findViewById(R.id.totalCaloriesText)
        badmintonProgressBar = view.findViewById(R.id.badmintonProgressBar)
        progressText = view.findViewById(R.id.progressText)

        // Set progress bar max to 100 for percentage
        badmintonProgressBar.max = 100

        btnAddSession.setOnClickListener { addSession() }
        btnSave.setOnClickListener { saveBadmintonData() }

        updateUI()
    }

    private fun addSession() {
        val duration = etDuration.text.toString().toIntOrNull() ?: 0

        currentSessionType = when {
            chipSingles.isChecked -> "Single"
            chipDoubles.isChecked -> "Double"
            chipDrills.isChecked -> "Drills"
            else -> "Single"
        }

        if (duration <= 0) {
            Toast.makeText(requireContext(), "Enter valid duration", Toast.LENGTH_SHORT).show()
            return
        }

        val record = badmintonTracker.addSession(duration, currentSessionType)

        currentDuration = duration
        currentCalories = record.sessionCalories

        totalSessions = record.totalSessions
        totalMinutes = record.totalMinutes
        totalCalories = record.totalBadmintonCalories

        updateUI()

        Toast.makeText(
            requireContext(),
            "Added: ${currentDuration} min ${currentSessionType} - ${currentCalories.toInt()} kcal\nClick SAVE to store",
            Toast.LENGTH_LONG
        ).show()

        etDuration.text?.clear()
    }

    private fun updateUI() {
        totalSessionsText.text = totalSessions.toString()
        totalMinutesText.text = totalMinutes.toString()
        totalCaloriesText.text = String.format("%.0f", totalCalories)

        val percentage = if (goalSessions > 0) {
            ((totalSessions.toFloat() / goalSessions) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        badmintonProgressBar.progress = percentage
        progressText.text = "$totalSessions / $goalSessions sessions ($percentage%)"
    }

    private fun saveBadmintonData() {
        if (currentDuration == 0) {
            Toast.makeText(requireContext(), "Please add a session first", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = UserPrefs.getUserID(requireContext())
        val url = "http://10.0.2.2/hhk_FitTest/BadmintonDAO.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response == "true") {
                    Toast.makeText(requireContext(), "Badminton Session Saved!", Toast.LENGTH_SHORT).show()
                    currentDuration = 0
                    currentSessionType = ""
                    currentCalories = 0f
                } else {
                    Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["phpFunction"] = "Save_Badminton"
                params["User_ID"] = userId.toString()
                params["Session_type"] = currentSessionType
                params["Duration_Minutes"] = currentDuration.toString()
                params["Calorie_burn"] = currentCalories.toInt().toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}