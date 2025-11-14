package com.example.dzcoffee.ui.customer

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyOrdersActivity : AppCompatActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        val btnBack = findViewById<MaterialButton?>(R.id.btnBackMyOrders)
        val btnRefresh = findViewById<MaterialButton?>(R.id.btnRefreshMyOrders)

        btnBack?.setOnClickListener { finish() }
        btnRefresh?.setOnClickListener { loadOrders() }

        loadOrders()
    }

    private fun loadOrders() {
        val container = findViewById<LinearLayout?>(R.id.layoutMyOrders)
        if (container == null) {
            Toast.makeText(this, "Layout error: layoutMyOrders not found", Toast.LENGTH_SHORT).show()
            return
        }

        container.removeAllViews()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            addSimpleMessage(container, "You are not logged in.")
            return
        }

        db.collection("orders")
            .whereEqualTo("userId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    addSimpleMessage(container, "You have no orders yet.")
                    return@addOnSuccessListener
                }

                for (doc in snap.documents) {
                    val row = layoutInflater.inflate(R.layout.row_my_order_item, container, false)

                    // ----- STATUS -----
                    val status = doc.getString("status") ?: "Pending"

                    // ----- TOTAL (supports different field names and types) -----
                    val totalRaw = when {
                        doc.contains("totalPrice") -> doc.get("totalPrice")
                        doc.contains("total") -> doc.get("total")
                        else -> null
                    }

                    val totalValue = when (totalRaw) {
                        is Number -> totalRaw.toDouble()
                        is String -> totalRaw.toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }
                    val totalText = String.format(Locale.UK, "£%.2f", totalValue)

                    // ----- CREATED AT (Timestamp / Long / Double / null) -----
                    val createdAny = doc.get("createdAt")
                    val createdDate: Date? = when (createdAny) {
                        is Timestamp -> createdAny.toDate()
                        is Long -> Date(createdAny)
                        is Double -> Date(createdAny.toLong())
                        else -> null
                    }
                    val createdText = createdDate?.let { df.format(it) } ?: ""

                    // ----- Set text to views (all nullable-safe) -----
                    row.findViewById<TextView?>(R.id.tvStatusMy)?.text = status
                    row.findViewById<TextView?>(R.id.tvTotalMy)?.text = totalText
                    row.findViewById<TextView?>(R.id.tvCreatedAtMy)?.text = createdText

                    val orderId = doc.id
                    val btnCancel = row.findViewById<MaterialButton?>(R.id.btnCancelMy)

                    // Cancel button is optional
                    btnCancel?.setOnClickListener {
                        if (!status.equals("Pending", true)) {
                            Toast.makeText(
                                this,
                                "Only pending orders can be cancelled",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        db.collection("orders").document(orderId)
                            .update("status", "Cancelled")
                            .addOnSuccessListener {
                                Toast.makeText(this, "Order cancelled", Toast.LENGTH_SHORT).show()
                                loadOrders()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    e.message ?: "Failed to cancel order",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    container.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    e.message ?: "Failed to load orders",
                    Toast.LENGTH_LONG
                ).show()
                addSimpleMessage(container, "Could not load orders.")
            }
    }

    private fun addSimpleMessage(container: LinearLayout, text: String) {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(resources.getColor(android.R.color.white, null))
            textSize = 14f
        }
        container.addView(tv)
    }
}

