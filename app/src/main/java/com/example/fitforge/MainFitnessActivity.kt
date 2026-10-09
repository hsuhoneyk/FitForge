package com.example.fitforge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainFitnessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_fitness)

        val fullname = UserPrefs.getName(this)
        Log.d("FullName", "Full Name:$fullname")

        val fitnessMsg = findViewById<TextView>(R.id.tvallfitness)
        fitnessMsg.text = "Hello $fullname!"

        val cardrunning = findViewById<CardView>(R.id.actrunning)
        cardrunning.setOnClickListener {
            val intentRunning = Intent(this,RunningActivity::class.java)
            startActivity(intentRunning)
        }

        val cardcycling = findViewById<CardView>(R.id.actcycling)
        cardcycling.setOnClickListener {
            val intentCycling = Intent(this, CyclingActivity::class.java)
            startActivity(intentCycling)
        }

        val cardweight = findViewById<CardView>(R.id.actweightlifting)
        cardweight.setOnClickListener {
            val intentWeight = Intent(this, WeightliftingActivity::class.java)
            startActivity(intentWeight)
        }

        val cardbadminton = findViewById<CardView>(R.id.actBadminton)
        cardbadminton.setOnClickListener {
            val intentbadminton = Intent(this, BadmintonActivity::class.java)
            startActivity(intentbadminton)
        }
    }
}