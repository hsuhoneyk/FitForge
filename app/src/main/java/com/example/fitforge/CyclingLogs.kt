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
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class CyclingLogs : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cycle_adapter: CyclingLogsAdapter
    private val cycling_logs = mutableListOf<CyclingLog>()
    private lateinit var cycleGoals_Distance: String
    private lateinit var cycleGoals_Cal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_logs_cycling, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Create recyclerView and fetchRunLogs Data from SQL
        recyclerView = view.findViewById(R.id.rvcycleTable)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val userID = UserPrefs.getUserID(requireContext())
        if (userID != -1) {
            //Log.d("UserID", "User ID: $userId")
            cyclelogFetch(userID)
        } else {
            Toast.makeText(requireContext(), "User ID isn't exist!", Toast.LENGTH_SHORT).show()
        }

        //This part is for running Goals
        val etcycling_goalDis = view.findViewById<EditText>(R.id.etcyclegoaldis)
        val etcycling_goalCal = view.findViewById<EditText>(R.id.etcyclegoalcal)
        val btn_cycling = view.findViewById<Button>(R.id.butncyclesavegoals)

        btn_cycling.setOnClickListener {
            cycleGoals_Distance= etcycling_goalDis.text.toString().trim()
            cycleGoals_Cal = etcycling_goalCal.text.toString().trim()

            if (cycleGoals_Distance.isEmpty() || cycleGoals_Cal.isEmpty()) {
                Toast.makeText(requireContext(), "Distance and Calories are required!!",
                    Toast.LENGTH_SHORT)
                    .show()
            } else {
                save_cycling_goals(userID)
            }
        }
    }
    private fun cyclelogFetch(userId: Int) {
        val url = "http://10.0.2.2/hhk_FitTest/CyclingDAO.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    cycling_logs.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val cyclelog = CyclingLog(
                            jsonObject.getString("Route_Name"),
                            jsonObject.getDouble("Total_Distance").toFloat(),
                            jsonObject.getDouble("Speed").toFloat(),
                            jsonObject.getDouble("Calorie_burn").toFloat(),
                            jsonObject.getString("Workout_date")
                        )
                        cycling_logs.add(cyclelog)
                    }
                    cycle_adapter = CyclingLogsAdapter(cycling_logs)
                    recyclerView.adapter = cycle_adapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["phpFunction"] = "get_cyclelogs"
                params["User_ID"] = userId.toString()
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
    private fun save_cycling_goals(userId : Int) {

        val URL_Root = "http://10.0.2.2/hhk_FitTest/CyclingTargetDAO.php"

        //Create volley string request
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_Root,
            Response.Listener<String> { response ->
                try {
                    android.util.Log.d("GOAL_SAVE", "PHP Response: $response")
                    if (response == "true") {
                        Toast.makeText(
                            requireContext(),
                            "Cycling Goals Data Saved, Successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
                params.put("phpFunction", "SaveCyclingGoal")
                params.put("User_ID", userId.toString())
                params.put("Goal_Distance", cycleGoals_Distance)
                params.put("Goal_Calorie", cycleGoals_Cal)

                android.util.Log.d("GOAL_SAVE", "Sending params: $params")

                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)

    }

}