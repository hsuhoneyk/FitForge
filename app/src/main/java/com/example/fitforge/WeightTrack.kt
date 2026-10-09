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
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException

class WeightTrack : Fragment() {

    private lateinit var tietExercise: TextInputEditText
    private lateinit var tietWeight: TextInputEditText
    private lateinit var tietReps: TextInputEditText
    private lateinit var tietSets: TextInputEditText

    private lateinit var totalSetsText: TextView
    private lateinit var totalVolumeText: TextView
    private lateinit var totalCaloriesText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private lateinit var weightTracker : WeightTracker

    private lateinit var btnAddSets: Button
    private lateinit var btnSave: Button

    private var totalSets = 0
    private var totalVolume = 0f
    private var totalCalories = 0f
    private val goalSets = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_track_weightlifting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userWeight = UserPrefs.getWeight(requireContext())

        weightTracker = WeightTracker(
            userWeightKg = userWeight.toFloat(),
            metValue = 6.0f
        )

        weightTracker.start()

        // Inputs
        tietExercise = view.findViewById(R.id.Tietweightname)
        tietWeight = view.findViewById(R.id.Tietweightinput)
        tietReps = view.findViewById(R.id.Tietrepsinput)
        tietSets = view.findViewById(R.id.Tietweightsets)

        // Stats UI
        totalSetsText = view.findViewById(R.id.totalSetsText)
        totalVolumeText = view.findViewById(R.id.totalVolumeText)
        totalCaloriesText = view.findViewById(R.id.totalCaloriesText)
        progressBar = view.findViewById(R.id.weightprogressBar)
        progressText = view.findViewById(R.id.progressText)

        // Buttons
        btnAddSets = view.findViewById(R.id.btnaddSets)
        btnSave = view.findViewById(R.id.btnsaveweight)

        btnAddSets.setOnClickListener {
            addSets()
        }

        btnSave.setOnClickListener {
            saveWeightData()
        }

        updateUI()
    }

    private fun addSets() {
        val weight = tietWeight.text.toString().toFloatOrNull() ?: 0f
        val reps = tietReps.text.toString().toIntOrNull() ?: 0
        val sets = tietSets.text.toString().toIntOrNull() ?: 1

        if (weight <= 0 || reps <= 0) {
            Toast.makeText(requireContext(), "Enter valid weight & reps", Toast.LENGTH_SHORT).show()
            return
        }
        val stats = weightTracker.addSet(weight, reps, sets)

        totalSets += sets

        val volume = weight * reps * sets
        totalVolume += volume
        totalCalories += volume * 0.1f

        updateUI()
    }

    private fun updateUI() {
        totalSetsText.text = totalSets.toString()
        totalVolumeText.text = String.format("%.1f", totalVolume)
        totalCaloriesText.text = String.format("%.1f", totalCalories)

        val progress = ((totalSets.toFloat() / goalSets) * 100).toInt().coerceAtMost(100)
        progressBar.progress = progress
        progressText.text = "$totalSets / $goalSets sets"
    }

    private fun saveWeightData() {
        val userId = UserPrefs.getUserID(requireContext())
        val URL_ROOT = "http://10.0.2.2/hhk_FitTest/WeightDAO.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    if (response == "true") {
                        Toast.makeText(requireContext(), "Workout Saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["phpFunction"] = "Save_Weightlifting"
                params["User_ID"] = userId.toString()
                params["Exercise_Name"] = tietExercise.text.toString()
                params["Weight"] = tietWeight.text.toString()
                params["Reps"] = tietReps.text.toString()
                params["Total_Sets"] = totalSets.toString()
                params["Total_Volume"] = totalVolume.toString()
                params["Calories_Burned"] = totalCalories.toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}