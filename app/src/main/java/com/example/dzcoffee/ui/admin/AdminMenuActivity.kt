package com.example.dzcoffee.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.android.material.button.MaterialButton

class AdminMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        val btnBack = findViewById<MaterialButton>(R.id.btnBackAdminMenu)
        val btnGoToOrders = findViewById<MaterialButton>(R.id.btnGoToOrders)

        btnBack.setOnClickListener { finish() }

        btnGoToOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
    }
}
