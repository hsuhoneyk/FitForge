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

class CyclingData : Fragment() {
    private lateinit var CyclingprogressDistance: ProgressBar
    private lateinit var CyclingprogressCalories: ProgressBar

    private lateinit var tvCdispercent: TextView
    private lateinit var tvCcalper: TextView

    private lateinit var tvCdisval: TextView
    private lateinit var tvCcalval: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data_cycling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CyclingprogressDistance = view.findViewById(R.id.pbcycledisbar)
        CyclingprogressCalories = view.findViewById(R.id.pbcyclecalbar)

        tvCdispercent = view.findViewById(R.id.tvcycledispercent)
        tvCcalper = view.findViewById(R.id.tvcyclecalper)

        tvCdisval = view.findViewById(R.id.tvcycledisValue)
        tvCcalval = view.findViewById(R.id.tvcyclecalval)

        monthlyCyclingComparisonfetch()
    }

    private fun monthlyCyclingComparisonfetch() {
        val url = "http://10.0.2.2/hhk_FitTest/CyclingDAO.php"
        val userId = UserPrefs.getUserID(requireContext())

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    android.util.Log.d("CYCLING_DATA", "Response: $response")

                    val jsonArray = JSONArray(response)
                    updateCyclingGoalProgress(jsonArray)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show()
                }

            },
            { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "phpFunction" to "Get_Cycling_Monthly_Compare",
                    "User_ID" to UserPrefs.getUserID(requireContext()).toString()
                )
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateCyclingGoalProgress(array: JSONArray) {
        try {
            if (array.length() == 0) return


            // Get latest month data
            val obj = array.getJSONObject(array.length() - 1)

            // FIXED: Using optDouble instead of getDouble (prevents crashes)
            val actualCyclingDistance = obj.optDouble("total_distance", 0.0).toFloat()
            val goalCyclingDistance = obj.optDouble("Goal_Distance", 0.0).toFloat()


            val burnedCyclingCalories = obj.optDouble("total_calories", 0.0).toFloat()
            val goalCyclingCalories = obj.optDouble("Goal_Calorie", 0.0).toFloat()

            // Calculate percentages
            val distanceCyclingPercent = if (goalCyclingDistance > 0)
                (actualCyclingDistance / goalCyclingDistance * 100).coerceAtMost(100f)
            else 0f

            val caloriesPercent = if (goalCyclingCalories > 0)
                (burnedCyclingCalories / goalCyclingCalories * 100).coerceAtMost(100f)
            else 0f

            // Update progress bars
            CyclingprogressDistance.progress = distanceCyclingPercent.toInt()
            CyclingprogressCalories.progress = caloriesPercent.toInt()

            // Update percentage text
            tvCdispercent.text = "${distanceCyclingPercent.toInt()}%"
            tvCcalper.text = "${caloriesPercent.toInt()}%"

            // Update value text with better formatting
            tvCdisval.text = if (goalCyclingDistance > 0) {
                String.format("%.1f / %.1f km", actualCyclingDistance, goalCyclingDistance)
            } else {
                String.format("%.1f km (no goal set)", actualCyclingDistance)
            }

            tvCcalval.text = if (goalCyclingCalories > 0) {
                "${burnedCyclingCalories.toInt()} / ${goalCyclingCalories.toInt()} kcal"
            } else {
                "${burnedCyclingCalories.toInt()} kcal (no goal set)"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}