package com.example.fitforge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class WeightLogs : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var weightadapter: WeightLogsAdapter
    private val weightLogs = mutableListOf<WeightLog>()

    private lateinit var weightGoalSets: String
    private lateinit var calGoal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logs_weightlifting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvweightTable)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val userID = UserPrefs.getUserID(requireContext())

        if (userID != -1) {
            fetchWeightLogs(userID)
        } else {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show()
        }

        // Goals
        val etWeightGoalsets = view.findViewById<EditText>(R.id.etweight_goalsets)
        val etCalGoal = view.findViewById<EditText>(R.id.etweight_goalcal)
        val btnSave = view.findViewById<Button>(R.id.butnsaveweight_goals)

        btnSave.setOnClickListener {
            weightGoalSets = etWeightGoalsets.text.toString().trim()
            calGoal = etCalGoal.text.toString().trim()

            if (weightGoalSets.isEmpty() || calGoal.isEmpty()) {
                Toast.makeText(requireContext(), "All fields required!", Toast.LENGTH_SHORT).show()
            } else {
                saveWeightGoals(userID)
            }
        }
    }

    // 🔹 Fetch logs from PHP
    private fun fetchWeightLogs(userId: Int) {
        val url = "http://10.0.2.2/hhk_FitTest/WeightDAO.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    weightLogs.clear()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)

                        val log = WeightLog(
                            obj.getString("Exercise_Name"),
                            obj.getDouble("Weight").toFloat(),
                            obj.getInt("Reps"),
                            obj.getInt("Total_Sets"),
                            obj.getDouble("Total_Volume").toFloat(),
                            obj.getDouble("Calories_Burned").toFloat(),
                            obj.getString("Workout_Date")
                        )

                        weightLogs.add(log)
                    }

                    weightadapter = WeightLogsAdapter(weightLogs)
                    recyclerView.adapter = weightadapter

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "JSON Error", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["phpFunction"] = "Get_Weightlifting_Logs"
                params["User_ID"] = userId.toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    // 🔹 Save Goals
    private fun saveWeightGoals(userId: Int) {
        val url = "http://10.0.2.2/hhk_FitTest/WeightTargetDAO.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response == "true") {
                    Toast.makeText(requireContext(), "Goals saved!", Toast.LENGTH_SHORT).show()
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
                params["phpFunction"] = "SaveWeightliftingGoal"
                params["User_ID"] = userId.toString()
                params["Goal_Sets"] = weightGoalSets
                params["Goal_Calories_Burned"] = calGoal
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }
}