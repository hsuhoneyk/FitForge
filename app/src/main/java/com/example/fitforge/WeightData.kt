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

class WeightData : Fragment() {

    private lateinit var progressSetsWeight: ProgressBar
    private lateinit var progressCalories: ProgressBar

    private lateinit var tvSetsPercent: TextView
    private lateinit var tvCaloriesPercent: TextView

    private lateinit var tvSetsValue: TextView
    private lateinit var tvCaloriesValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data_weightlifting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressSetsWeight = view.findViewById(R.id.pbSets_bar)
        progressCalories = view.findViewById(R.id.pbWeight_calbar)

        tvSetsPercent = view.findViewById(R.id.tvSets_percent)
        tvCaloriesPercent = view.findViewById(R.id.tvWeight_calpercent)

        tvSetsValue = view.findViewById(R.id.tvSets_Value)
        tvCaloriesValue = view.findViewById(R.id.tvWeight_calval)

        fetchMonthlyComparison()
    }

    private fun fetchMonthlyComparison() {
        val url = "http://10.0.2.2/hhk_FitTest/WeightDAO.php"

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
                    "phpFunction" to "Get_Weightlifting_Monthly_Compare",
                    "User_ID" to UserPrefs.getUserID(requireContext()).toString()
                )
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateGoalProgress(array: JSONArray) {
        try {
            if (array.length() == 0) return

            val obj = array.getJSONObject(array.length() - 1)

            val totalSets = obj.optDouble("total_sets", 0.0).toFloat()
            val goalSets = obj.optDouble("Goal_Sets", 0.0).toFloat()

            val totalCalories = obj.optDouble("total_calories", 0.0).toFloat()

            // TEMPORARY: Use a default calorie goal of 500
            val goalCalories = 500f  // Default goal

            val setsPercent = if (goalSets > 0)
                (totalSets / goalSets * 100).coerceAtMost(100f)
            else 0f

            val calPercent = if (goalCalories > 0)
                (totalCalories / goalCalories * 100).coerceAtMost(100f)
            else 0f

            progressSetsWeight.progress = setsPercent.toInt()
            progressCalories.progress = calPercent.toInt()

            tvSetsPercent.text = "${setsPercent.toInt()}%"
            tvCaloriesPercent.text = "${calPercent.toInt()}%"

            tvSetsValue.text = "${totalSets.toInt()} / ${goalSets.toInt()} sets"
            tvCaloriesValue.text = "${totalCalories.toInt()} / ${goalCalories.toInt()} kcal"

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}