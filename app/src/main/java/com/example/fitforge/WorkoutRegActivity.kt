package com.example.fitforge

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException


class WorkoutRegActivity : AppCompatActivity() {

    private lateinit var tietFullname: TextInputEditText
    private lateinit var tietUsname: TextInputEditText
    private lateinit var tietDoB : TextInputEditText
    private lateinit var rgGender : RadioGroup
    private lateinit var tietEmail : TextInputEditText
    private lateinit var tietpsw : TextInputEditText
    private lateinit var tietconpsw : TextInputEditText
    private lateinit var weightSld : Slider
    private lateinit var weightVal : TextView
    private lateinit var heightSld : Slider
    private lateinit var heightVal : TextView
    private var gender : String = ""
    private var weight : String = "0"
    private var height : String = "0"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_reg)


        // UI button initialization
        tietFullname = findViewById<TextInputEditText>(R.id.editFullName)
        tietUsname = findViewById<TextInputEditText>(R.id.editUname)
        tietDoB = findViewById<TextInputEditText>(R.id.editDoB)
        rgGender = findViewById<RadioGroup>(R.id.radioGender)
        tietEmail= findViewById<TextInputEditText>(R.id.editEmail)
        tietpsw = findViewById<TextInputEditText>(R.id.editpsw)
        tietconpsw = findViewById<TextInputEditText>(R.id.editcpsw)
        weightSld = findViewById<Slider>(R.id.sldWeight)
        weightVal = findViewById<TextView>(R.id.sldValWeight)
        heightSld = findViewById<Slider>(R.id.sldHeight)
        heightVal = findViewById<TextView>(R.id.sldValHeight)

        val btnSignup = findViewById<MaterialButton>(R.id.butnReg)

        // slider  & listener
        weightSld.addOnChangeListener { _, value, _ ->
            weightVal.text = " Weight: ${value.toInt()}"
            weight = value.toInt().toString()
        }

        heightSld.addOnChangeListener { _, value, _ ->
            heightVal.text = " Height: ${value.toInt()}"
            height = value.toInt().toString()
        }

        // radio group
        rgGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radioMale -> gender = "Male"
                R.id.radioFemale -> gender = "Female"
            }
        }

        //onclick function for btn register
        btnSignup.setOnClickListener {
            if (inputValidates()) {
                Signup()
            } else{
                Snackbar.make(btnSignup, "All fields are required for submission",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }// end of oncreate

    //get user data function
    private fun getUserData(): UserData {
        return UserData(
            FullName = tietFullname.text.toString().trim(),
            UserName = tietUsname.text.toString().trim(),
            DateofBirth =  tietDoB.text.toString().trim(),
            Gender =  gender,
            Email =  tietEmail.text.toString().trim(),
            Password =  tietpsw.text.toString().trim(),
            ConfirmPsw =  tietconpsw.text.toString().trim(),
            Weight =  weight,
            Height =  height
        )

    }//end of GetUserData

    //input validations() function
    private fun inputValidates(): Boolean{
        val userData = getUserData()

        //FullName validates
        if(userData.FullName.isEmpty()) {
            tietFullname.error = "FullName is required"
            return false
        }
        if(userData.UserName.isEmpty()) {
            tietUsname.error = "Don't  forget to add your username"
            return false
        }
        if(userData.DateofBirth.isEmpty()) {
            tietDoB.error = "Please enter your date of birth"
            return false
        }
        if(userData.Email.isEmpty()) {
            tietEmail.error = "Need your email to complete"
            return false
        }
        if( !android.util.Patterns.EMAIL_ADDRESS.matcher(userData.Email).matches()){
            tietEmail.error = " Invalid email format"
            return false
        }
        if(userData.Password.isEmpty()) {
            tietpsw.error = "password can not be empty"
            return false
        }
        if(userData.ConfirmPsw.isEmpty()) {
            tietconpsw.error = "Please confirm your password"
            return false
        }
        if(userData.ConfirmPsw != userData.Password) {
            tietconpsw.error = "Password do not match. "
            return false
        }
        if (rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Choose Gender: male or female",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }// end of validation

    // signup() function
    private fun Signup() {
        // val signupmethod = "Signup"
        val URL_Root = "http://10.0.2.2/hhk_FitTest/SignUpDAO.php"
        val userData = getUserData()

        //Create volley string request
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_Root,
            Response.Listener<String> { response ->
                try {
                    //Log.d("Response", response)
                    if (response == "true") {
                        Toast.makeText(
                            this,
                            "Welcome!! You've signed up successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(
                        applicationContext,
                        "Error: ${volleyError.message ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("phpFunction", "createUser")
                params.put("Full_Name", userData.FullName)
                params.put("User_Name", userData.UserName)
                params.put("Date_of_Birth", userData.DateofBirth)
                params.put("Gender", userData.Gender)
                params.put("Email", userData.Email)
                params.put("Password",userData.Password)
                params.put("Weight(kg)", userData.Weight)
                params.put("Height(cm)", userData.Height)
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)

    }




}//end of reg