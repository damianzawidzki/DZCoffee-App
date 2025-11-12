package com.example.dzcoffee.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.NotificationsActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.ui.customer.cart.CartActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnMenu = findViewById<Button>(R.id.btnMenu)
        val btnMyOrders = findViewById<Button>(R.id.btnMyOrders)
        val btnCart = findViewById<Button>(R.id.btnCart)
        val btnNotifications = findViewById<Button>(R.id.btnNotifications)

        btnMenu.setOnClickListener {
            startActivity(Intent(this, ProductsActivity::class.java))
        }

        btnMyOrders.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
    }
}

