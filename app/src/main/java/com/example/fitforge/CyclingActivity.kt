package com.example.fitforge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fitforge.databinding.ActivityCyclingBinding


class CyclingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCyclingBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCyclingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_cycling_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomCyclingNav.setupWithNavController(navController)

    }
}