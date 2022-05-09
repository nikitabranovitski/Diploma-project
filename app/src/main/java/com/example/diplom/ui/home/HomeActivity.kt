package com.example.diplom.ui.home

import android.Manifest
import android.os.Bundle
import androidx.navigation.findNavController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.diplom.R
import com.example.diplom.databinding.ActivityHomeBinding
import com.example.diplom.repository.PhotoUserRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.navigation_map)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.navigation_map)
            }

            else -> {
                Toast.makeText(
                    this,
                    "No permission granted to find location",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PhotoUserRepository.loadPhotoAvatar()

        navController = findNavController(R.id.container)
        permissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        ))
        navController.graph = navController.navInflater.inflate(R.navigation.navigation_map)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.map -> {
                    navController.graph =
                        navController.navInflater.inflate(R.navigation.navigation_map)
                }
                R.id.message -> {
                    navController.graph =
                        navController.navInflater.inflate(R.navigation.message)
                }
                R.id.all_photo -> {
                    navController.graph =
                        navController.navInflater.inflate(R.navigation.navigation_all_photo)
                }
                R.id.profile -> {
                    navController.graph =
                        navController.navInflater.inflate(R.navigation.navigation_my_profile)
                }
            }
            return@setOnItemSelectedListener true
        }
    }
}