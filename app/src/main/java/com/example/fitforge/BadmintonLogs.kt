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
import java.util.Calendar

class BadmintonLogs : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var badmintonAdapter: BadmintonLogsAdapter
    private val badmintonLogs = mutableListOf<BadmintonLog>()

    private var goalSessions = ""
    private var goalCalories = ""
    private var goalMonth = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logs_badminton, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvBadmintonLogs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        badmintonAdapter = BadmintonLogsAdapter(badmintonLogs)
        recyclerView.adapter = badmintonAdapter

        val userID = UserPrefs.getUserID(requireContext())

        if (userID != -1) {
            fetchBadmintonLogs(userID)
        } else {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show()
        }

        val etGoalSessions = view.findViewById<EditText>(R.id.etbadgoalSession)
        val etGoalCalories = view.findViewById<EditText>(R.id.etbadgoalCalories)
        val btnSaveGoal = view.findViewById<Button>(R.id.btnUpdateGoals)

        val calendar = Calendar.getInstance()
        goalMonth = String.format("%d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

        btnSaveGoal.setOnClickListener {
            goalSessions = etGoalSessions.text.toString().trim()
            goalCalories = etGoalCalories.text.toString().trim()

            if (goalSessions.isEmpty() && goalCalories.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter at least one goal!", Toast.LENGTH_SHORT).show()
            } else {
                saveBadmintonGoals(userID)
            }
        }
    }

    private fun fetchBadmintonLogs(userId: Int) {
        val url = "http://10.0.2.2/hhk_FitTest/BadmintonDAO.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    badmintonLogs.clear()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val log = BadmintonLog(
                            sessionType = obj.getString("Session_type"),
                            durationMinutes = obj.getInt("Duration_Minutes"),
                            calorieBurn = obj.getInt("Calorie_burn"),
                            workoutDate = obj.getString("Workout_date")
                        )
                        badmintonLogs.add(log)
                    }

                    badmintonAdapter.notifyDataSetChanged()

                    if (badmintonLogs.isEmpty()) {
                        Toast.makeText(requireContext(), "No badminton sessions found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "JSON Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["phpFunction"] = "Get_Badminton_Logs"
                params["User_ID"] = userId.toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun saveBadmintonGoals(userId: Int) {
        val url = "http://10.0.2.2/hhk_FitTest/BadmintonTargetDAO.php"

        val sessionsValue = if (goalSessions.isNotEmpty()) goalSessions.toIntOrNull() ?: 0 else 0
        val caloriesValue = if (goalCalories.isNotEmpty()) goalCalories.toIntOrNull() ?: 0 else 0

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response == "true") {
                    Toast.makeText(requireContext(), "Goals saved for $goalMonth!", Toast.LENGTH_SHORT).show()
                    view?.let {
                        val etGoalSessions = it.findViewById<EditText>(R.id.etbadgoalSession)
                        val etGoalCalories = it.findViewById<EditText>(R.id.etbadgoalCalories)
                        etGoalSessions.text?.clear()
                        etGoalCalories.text?.clear()
                    }
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
                params["phpFunction"] = "SaveBadmintonGoal"
                params["User_id"] = userId.toString()
                params["Goal_Sessions"] = sessionsValue.toString()
                params["Goal_Calories"] = caloriesValue.toString()
                params["Goal_Month"] = goalMonth
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }
}
