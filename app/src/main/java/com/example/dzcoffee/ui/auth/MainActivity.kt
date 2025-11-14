package com.example.dzcoffee.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R

// Simple start screen shown when app launches
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnContinue)

        btnStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}