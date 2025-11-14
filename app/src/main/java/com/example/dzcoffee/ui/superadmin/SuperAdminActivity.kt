package com.example.dzcoffee.ui.superadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.ui.admin.AdminActivity
import com.example.dzcoffee.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class SuperAdminActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superadmin)

        val btnManageAdmin = findViewById<MaterialButton>(R.id.btnManageAdminPanel)
        val btnRegisterAdmin = findViewById<MaterialButton>(R.id.btnRegisterAdmin)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogoutSuperAdmin)

        btnManageAdmin.setOnClickListener {
            // open normal admin panel (menu, orders, feedback)
            startActivity(Intent(this, AdminActivity::class.java))
        }

        btnRegisterAdmin.setOnClickListener {
            // open form where you register admins
            startActivity(Intent(this, RegisterAdminActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
