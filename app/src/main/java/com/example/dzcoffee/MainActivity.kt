package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Uses the layout you moved from splash

        // Find the "Continue" button and navigate to LoginActivity
        val continueBtn = findViewById<MaterialButton>(R.id.btnContinue)
        continueBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close MainActivity so user can't go back to splash with back button
        }
    }
}
