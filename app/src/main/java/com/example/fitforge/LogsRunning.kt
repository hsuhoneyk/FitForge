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

class LogsRunning : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RunLogsAdapter
    private val runLogs = mutableListOf<RunLog>()
    private lateinit var runGoals_Distance: String
    private lateinit var runGoals_Cal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_logs_running, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Create recyclerView and fetchRunLogs Data from SQL
        recyclerView = view.findViewById(R.id.rvTable)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val userID = UserPrefs.getUserID(requireContext())
        if (userID != -1) {
            //Log.d("UserID", "User ID: $userId")
            runLogsFetch(userID)
        } else {
            Toast.makeText(requireContext(), "User ID isn't exist!", Toast.LENGTH_SHORT).show()
        }
        //This part is for running Goals
        val edtextrungoaldis = view.findViewById<EditText>(R.id.etgoaldis)
        val edtexrungoalcal = view.findViewById<EditText>(R.id.etgoalcal)
        val butsave = view.findViewById<Button>(R.id.butnsavegoals)

        butsave.setOnClickListener {
            runGoals_Distance = edtextrungoaldis.text.toString().trim()
            runGoals_Cal = edtexrungoalcal.text.toString().trim()

            if (runGoals_Distance.isEmpty() || runGoals_Cal.isEmpty()) {
                Toast.makeText(requireContext(), "Distance and Calories are required!!",
                    Toast.LENGTH_SHORT)
                    .show()
            } else {
                Save_RunGoal(userID)
            }
        }
    }
    private fun runLogsFetch(userId: Int) {
        val url = "http://10.0.2.2/hhk_FitTest/RunningDAO.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    runLogs.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val runLog = RunLog(
                            jsonObject.getString("Route_Name"),
                            jsonObject.getDouble("Total_Distance").toFloat(),
                            jsonObject.getDouble("Speed").toFloat(),
                            jsonObject.getDouble("Calorie_burn").toFloat(),
                            jsonObject.getString("Workout_date")
                        )
                        runLogs.add(runLog)
                    }
                    adapter = RunLogsAdapter(runLogs)
                    recyclerView.adapter = adapter
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
                params["phpFunction"] = "get_runlogs"
                params["User_ID"] = userId.toString()
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
    private fun Save_RunGoal(userId : Int) {

        val URL_Root = "http://10.0.2.2/hhk_FitTest/RunningTargetsDAO.php"

        //Create volley string request
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_Root,
            Response.Listener<String> { response ->
                try {
                    //Log.d("Response", response)
                    if (response == "true") {
                        Toast.makeText(
                            requireContext(),
                            "Running Goals Data Saved, Successfully.",
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
                params.put("phpFunction", "Save_RunGoal")
                params.put("User_ID", userId.toString())
                params.put("Goal_Distance", runGoals_Distance)
                params.put("Goal_Calorie", runGoals_Cal)
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)

    }

}