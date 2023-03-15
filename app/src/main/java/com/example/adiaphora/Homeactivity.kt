package com.example.adiaphora

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.adiaphora.databinding.ActivityHomeactivityBinding
import com.example.adiaphora.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class Homeactivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeactivityBinding
    private lateinit var auth: FirebaseAuth
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    auth = FirebaseAuth.getInstance()


        binding.logbtn.setOnClickListener {
            auth.signOut()
            Intent(this, Loginpage::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this,"Logout Succesfully", Toast.LENGTH_SHORT).show()
            }

            }

        }
    }
