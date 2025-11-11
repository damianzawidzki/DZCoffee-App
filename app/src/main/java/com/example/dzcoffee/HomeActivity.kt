package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnOpenMenu: MaterialButton
    private lateinit var btnMyOrders: MaterialButton
    private lateinit var btnNotifications: MaterialButton
    private lateinit var btnLogout: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvWelcome = findViewById(R.id.tvWelcome)
        btnOpenMenu = findViewById(R.id.btnOpenMenu)
        btnMyOrders = findViewById(R.id.btnMyOrders)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnLogout = findViewById(R.id.btnLogout)

        btnOpenMenu.setOnClickListener {
            startActivity(Intent(this, ProductsActivity::class.java))
        }

        btnMyOrders.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }

        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            performLogout()
        }

        loadProfileName()
    }

    private fun loadProfileName() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        db.collection("Customer").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val firstName =
                    doc.getString("firstName")
                        ?: doc.getString("CusFullName")

                if (!firstName.isNullOrBlank()) {
                    tvWelcome.text = "Welcome, $firstName"
                }
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
