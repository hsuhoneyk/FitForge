package com.example.fitforge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMain = findViewById<MaterialButton>(R.id.btnstart)
        btnMain.setOnClickListener {
            val intentFirst = Intent(this, WorkoutLoggerActivity::class.java)
            startActivity(intentFirst)
        }
    }
}
