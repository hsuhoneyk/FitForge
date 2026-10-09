package com.example.fitforge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class DataRunning : Fragment() {
    private lateinit var progressDistance: ProgressBar
    private lateinit var progressCalories: ProgressBar

    private lateinit var tvDistancePercent: TextView
    private lateinit var tvCaloriesPercent: TextView

    private lateinit var tvDistanceValue: TextView
    private lateinit var tvCaloriesValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data_running, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDistance = view.findViewById(R.id.pbdisbar)
        progressCalories = view.findViewById(R.id.pbcalbar)

        tvDistancePercent = view.findViewById(R.id.tvdispercent)
        tvCaloriesPercent = view.findViewById(R.id.tvcalper)

        tvDistanceValue = view.findViewById(R.id.tvdisValue)
        tvCaloriesValue = view.findViewById(R.id.tvcalval)

        fetchMonthlyComparison()
    }

    private fun fetchMonthlyComparison() {
        val url = "http://10.0.2.2/hhk_FitTest/RunningDAO.php"
        val userId = UserPrefs.getUserID(requireContext())

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                updateGoalProgress(JSONArray(response))
            },
            { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "phpFunction" to "Get_Monthly_Compare",
                    "User_ID" to UserPrefs.getUserID(requireContext()).toString()
                )
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateGoalProgress(array: JSONArray) {
        try {
            if (array.length() == 0)  return

            // Get latest month data
            val obj = array.getJSONObject(array.length() - 1)

            // FIXED: Using optDouble instead of getDouble (prevents crashes)
            val actualDistance = obj.optDouble("total_distance", 0.0).toFloat()
            val goalDistance = obj.optDouble("Goal_Distance", 0.0).toFloat()
            val burnedCalories = obj.optDouble("total_calories", 0.0).toFloat()
            val goalCalories = obj.optDouble("Goal_Calorie", 0.0).toFloat()

            // Calculate percentages
            val distancePercent = if (goalDistance > 0)
                (actualDistance / goalDistance * 100).coerceAtMost(100f)
            else 0f

            val caloriesPercent = if (goalCalories > 0)
                (burnedCalories / goalCalories * 100).coerceAtMost(100f)
            else 0f

            // Update progress bars
            progressDistance.progress = distancePercent.toInt()
            progressCalories.progress = caloriesPercent.toInt()

            // Update percentage text
            tvDistancePercent.text = "${distancePercent.toInt()}%"
            tvCaloriesPercent.text = "${caloriesPercent.toInt()}%"

            // Update value text with better formatting
            tvDistanceValue.text = if (goalDistance > 0) {
                String.format("%.1f / %.1f km", actualDistance, goalDistance)
            } else {
                String.format("%.1f km (no goal set)", actualDistance)
            }

            tvCaloriesValue.text = if (goalCalories > 0) {
                "${burnedCalories.toInt()} / ${goalCalories.toInt()} kcal"
            } else {
                "${burnedCalories.toInt()} kcal (no goal set)"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}