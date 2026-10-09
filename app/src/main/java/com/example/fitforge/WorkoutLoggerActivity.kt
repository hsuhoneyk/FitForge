package com.example.fitforge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject

class WorkoutLoggerActivity : AppCompatActivity() {
    private lateinit var UserNameedt : EditText
    private lateinit var Passwordedt : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_logger)

        //initialization UI elements
        UserNameedt=findViewById<EditText>(R.id.editTextTextEmailAddress)
        Passwordedt=findViewById<EditText>(R.id.editTextTextPassword)

        val btnSignIn = findViewById<MaterialButton>(R.id.SignInbtn)
        btnSignIn.setOnClickListener {
            if (inputValidates()) {
                login()
            }
        }

        val btnSignUp = findViewById<TextView>(R.id.edtSignUp)
        btnSignUp.setOnClickListener {
            val intentregform= Intent(this, WorkoutRegActivity::class.java)
            startActivity(intentregform)
        }
    }// end of oncreate

    private fun inputValidates(): Boolean {
        val userName = UserNameedt.text.toString().trim()
        val passwrd = Passwordedt.text.toString().trim()

        //FullName validates
        if (userName.isEmpty()) {
            UserNameedt.error = "FullName can not be empty"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            UserNameedt.error = " Invalid email format"
            return false
        }
        if (passwrd.isEmpty()) {
            Passwordedt.error = "Email can not be empty"
            return false
        }
        return true
    }// end of validation

    private fun login() {
        val username = UserNameedt.text.toString().trim()
        val password = Passwordedt.text.toString().trim()
        var URL_Root = "http://10.0.2.2/hhk_FitTest/SigninDAO.php"

        //Create volley string request
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_Root,
            Response.Listener<String> { response ->
                try {
                    //Log.d("ServerResponse", response) // Debugging
                    val obj = JSONObject(response)
                    val responseSuccess = obj.getString("response")
                    if (responseSuccess == "true") {
                        var userid = obj.getString("userid").toInt()
                        var fullName = obj.getString("FullName")
                        var weight = obj.getString("Weight").toInt()
                        var height = obj.getString("Height").toInt()
                        Toast.makeText(this,
                            "Thank you for loggin in" + fullName,
                            Toast.LENGTH_SHORT).show()

                        //fun call from userprefs
                        UserPrefs.saveUser(this,userid,fullName,weight,height)

                        val intentFitness = Intent (this@WorkoutLoggerActivity, MainFitnessActivity::class.java)
                        startActivity(intentFitness)
                    } else {
                        Toast.makeText(this, "Account does not exist", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("JSONError", "Failed to parse JSON: ${e.message}")
                    Toast.makeText(this@WorkoutLoggerActivity, "JSON Error!", Toast.LENGTH_SHORT).show()

                }
            },
            Response.ErrorListener { error ->
                Log.e("VolleyError", "Error: ${error.toString()}")
                Toast.makeText(this@WorkoutLoggerActivity, "Network Error!", Toast.LENGTH_SHORT).show()
            }
        )
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("phpFunction", "login")
                params.put("User_Name", username)
                params.put("Password", password)
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }
}//end of mainacivity