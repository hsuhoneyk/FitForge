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
import org.json.JSONException

class BadmintonData : Fragment() {

    // Progress bars
    private lateinit var DataprogressBar: ProgressBar
    private lateinit var progressSingle: ProgressBar
    private lateinit var progressDouble: ProgressBar
    private lateinit var progressDrills: ProgressBar

    // Text views for goal progress
    private lateinit var tvGoalInfo: TextView
    private lateinit var tvProgressDetail: TextView
    private lateinit var tvRemaining: TextView

    // Text views for session breakdown
    private lateinit var tvSingleCount: TextView
    private lateinit var tvDoubleCount: TextView
    private lateinit var tvDrillsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data_badminton, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Goal Progress Views
        DataprogressBar = view.findViewById(R.id.DataprogressBar)
        tvGoalInfo = view.findViewById(R.id.tvGoalInfo)
        tvProgressDetail = view.findViewById(R.id.tvProgressDetail)
        tvRemaining = view.findViewById(R.id.tvRemaining)

        // Initialize Session Breakdown Views
        progressSingle = view.findViewById(R.id.progressSingle)
        progressDouble = view.findViewById(R.id.progressDouble)
        progressDrills = view.findViewById(R.id.progressDrills)
        tvSingleCount = view.findViewById(R.id.tvSingleCount)
        tvDoubleCount = view.findViewById(R.id.tvDoubleCount)
        tvDrillsCount = view.findViewById(R.id.tvDrillsCount)

        // Fetch monthly comparison data
        fetchMonthlyComparison()
    }

    private fun fetchMonthlyComparison() {
        val url = "http://10.0.2.2/hhk_FitTest/BadmintonDAO.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    updateGoalProgress(jsonArray)
                    updateSessionBreakdown(jsonArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "JSON Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "phpFunction" to "Get_Badminton_Monthly_Compare",
                    "User_ID" to UserPrefs.getUserID(requireContext()).toString()
                )
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateGoalProgress(array: JSONArray) {
        try {
            if (array.length() == 0) {
                // No data - show default empty state
                tvGoalInfo.text = "No goals set for this month"
                tvProgressDetail.text = "0 sessions | 0 kcal"
                tvRemaining.text = "Set a goal to track progress"
                DataprogressBar.progress = 0
                return
            }

            // Get the most recent month's data (last item in array)
            val obj = array.getJSONObject(array.length() - 1)

            // Get actual values from database
            val totalSessions = obj.optInt("total_session", 0)
            val totalCalories = obj.optInt("total_calorie", 0)

            // Get goal values from database
            val goalSessions = obj.optInt("Goal_Sessions", 0)
            val goalCalories = obj.optInt("Goal_Calories", 0)

            // Calculate percentages
            val sessionsPercent = if (goalSessions > 0) {
                (totalSessions.toFloat() / goalSessions * 100).coerceAtMost(100f)
            } else 0f

            val caloriesPercent = if (goalCalories > 0) {
                (totalCalories.toFloat() / goalCalories * 100).coerceAtMost(100f)
            } else 0f

            // Calculate which progress to show (use higher percentage)
            val overallProgress = maxOf(sessionsPercent, caloriesPercent).toInt()
            DataprogressBar.progress = overallProgress

            // Update text views
            tvGoalInfo.text = "Goal: $goalSessions sessions | $goalCalories kcal"

            tvProgressDetail.text = when {
                goalSessions > 0 && goalCalories > 0 ->
                    "$totalSessions/$goalSessions sessions | $totalCalories/$goalCalories kcal"
                goalSessions > 0 ->
                    "$totalSessions/$goalSessions sessions"
                goalCalories > 0 ->
                    "$totalCalories/$goalCalories kcal"
                else ->
                    "No goals set"
            }

            // Calculate remaining
            val remainingSessions = if (goalSessions > totalSessions) goalSessions - totalSessions else 0
            val remainingCalories = if (goalCalories > totalCalories) goalCalories - totalCalories else 0

            tvRemaining.text = when {
                remainingSessions > 0 && remainingCalories > 0 ->
                    "$remainingSessions sessions or $remainingCalories kcal remaining"
                remainingSessions > 0 ->
                    "$remainingSessions sessions remaining"
                remainingCalories > 0 ->
                    "$remainingCalories kcal remaining"
                goalSessions > 0 || goalCalories > 0 ->
                    "🎉 Goal achieved! 🎉"
                else ->
                    "Set a goal to track progress"
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updateSessionBreakdown(array: JSONArray) {
        try {
            if (array.length() == 0) return

            // We need to get session type counts separately
            // This requires a separate API call or modify the existing one
            fetchSessionTypeBreakdown()

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun fetchSessionTypeBreakdown() {
        val url = "http://10.0.2.2/hhk_FitTest/BadmintonDAO.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    calculateSessionBreakdown(jsonArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "phpFunction" to "Get_Badminton_Logs",
                    "User_ID" to UserPrefs.getUserID(requireContext()).toString()
                )
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun calculateSessionBreakdown(jsonArray: JSONArray) {
        try {
            var singleCount = 0
            var doubleCount = 0
            var drillsCount = 0

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val sessionType = obj.getString("Session_type")

                when (sessionType) {
                    "Single" -> singleCount++
                    "Double" -> doubleCount++
                    "Drills" -> drillsCount++
                }
            }

            val totalSessions = singleCount + doubleCount + drillsCount

            // Update UI with counts
            tvSingleCount.text = singleCount.toString()
            tvDoubleCount.text = doubleCount.toString()
            tvDrillsCount.text = drillsCount.toString()

            // Update progress bars based on distribution
            if (totalSessions > 0) {
                val singlesPercent = (singleCount.toFloat() / totalSessions * 100).toInt()
                val doublesPercent = (doubleCount.toFloat() / totalSessions * 100).toInt()
                val drillsPercent = (drillsCount.toFloat() / totalSessions * 100).toInt()

                progressSingle.progress = singlesPercent
                progressDouble.progress = doublesPercent
                progressDrills.progress = drillsPercent
            } else {
                progressSingle.progress = 0
                progressDouble.progress = 0
                progressDrills.progress = 0
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}