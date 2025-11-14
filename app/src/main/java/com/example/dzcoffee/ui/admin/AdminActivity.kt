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

        // Buttons from activity_admin.xml
        val btnManageMenu = findViewById<MaterialButton>(R.id.btnManageMenu)
        val btnManageOrders = findViewById<MaterialButton>(R.id.btnManageOrders)
        val btnAdminFeedback = findViewById<MaterialButton>(R.id.btnAdminFeedback)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogoutAdmin)

        btnManageMenu.setOnClickListener {
            startActivity(Intent(this, AdminMenuActivity::class.java))
        }

        btnManageOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }

        btnAdminFeedback.setOnClickListener {
            startActivity(Intent(this, AdminFeedbackActivity::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

