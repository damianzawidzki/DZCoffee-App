package com.example.dzcoffee.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val btnMenu = findViewById<MaterialButton>(R.id.btnManageMenu)
        val btnOrders = findViewById<MaterialButton>(R.id.btnManageOrders)
        val btnFeedback = findViewById<MaterialButton>(R.id.btnFeedback)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogoutAdmin)

        btnMenu.setOnClickListener {
            startActivity(Intent(this, AdminMenuActivity::class.java))
        }

        btnOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }

        btnFeedback.setOnClickListener {
            startActivity(Intent(this, AdminFeedbackActivity::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
