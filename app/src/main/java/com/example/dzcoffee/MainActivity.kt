package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force fresh session on cold start
        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
