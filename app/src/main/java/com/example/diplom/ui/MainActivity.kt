package com.example.diplom.ui

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.diplom.R
import com.example.diplom.databinding.ActivityMainBinding
import com.example.diplom.ui.home.HomeActivity
import com.example.diplom.ui.login.LoginFragment
import com.example.diplom.ui.registration.RegistrationFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        isAuthorization()
    }

    private fun isAuthorization() {
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment()).commit()
        }
    }
}