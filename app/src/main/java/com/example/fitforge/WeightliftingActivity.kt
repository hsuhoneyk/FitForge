package com.example.fitforge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fitforge.databinding.ActivityWeightliftingBinding

class WeightliftingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeightliftingBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeightliftingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_Weightlifting_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.WeightbottomNav.setupWithNavController(navController)

    }
}
