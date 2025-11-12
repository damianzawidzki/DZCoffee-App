package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var btnManageMenu: MaterialButton
    private lateinit var btnFeedback: MaterialButton
    private lateinit var btnLogout: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        btnManageMenu = findViewById(R.id.btnManageMenu)
        btnFeedback = findViewById(R.id.btnFeedback)
        btnLogout = findViewById(R.id.btnLogout)

        // Open screen for managing products (add/edit/delete + image)
        btnManageMenu.setOnClickListener {
            val intent = Intent(this, AdminMenuActivity::class.java)
            startActivity(intent)
        }

        // Open feedback screen for admin replies
        btnFeedback.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        // Logout admin
        btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        auth.signOut()
        CartManager.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
