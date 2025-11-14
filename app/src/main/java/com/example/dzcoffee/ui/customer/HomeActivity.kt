package com.example.dzcoffee.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.NotificationsActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.dialogs.FeedbackDialog
import com.example.dzcoffee.ui.auth.LoginActivity
import com.example.dzcoffee.ui.customer.cart.CartActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnMenu = findViewById<MaterialButton>(R.id.btnMenu)
        val btnMyOrders = findViewById<MaterialButton>(R.id.btnMyOrders)
        val btnCart = findViewById<MaterialButton>(R.id.btnCart)
        val btnNotifications = findViewById<MaterialButton>(R.id.btnNotifications)
        val btnRateUs = findViewById<MaterialButton>(R.id.btnRateUs)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

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

        btnRateUs.setOnClickListener {
            showFeedbackDialog()
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showFeedbackDialog() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "You must be logged in to send feedback", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = user.uid
        val email = user.email ?: "unknown"

        val dialog = FeedbackDialog(this) { rating, comment ->
            val data = hashMapOf(
                "uid" to uid,
                "userEmail" to email,
                "rating" to rating.toDouble(),
                "comment" to comment,
                "createdAt" to Timestamp.now()
            )

            db.collection("feedback")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        e.message ?: "Failed to send feedback",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        dialog.show()
    }
}
