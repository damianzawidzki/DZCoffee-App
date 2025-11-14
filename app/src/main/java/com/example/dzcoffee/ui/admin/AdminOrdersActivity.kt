package com.example.dzcoffee.ui.admin

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminOrdersActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders)

        findViewById<MaterialButton?>(R.id.btnBackAdminOrders)?.setOnClickListener { finish() }
        findViewById<MaterialButton?>(R.id.btnRefreshOrders)?.setOnClickListener { loadOrders() }

        loadOrders()
    }

    private fun loadOrders() {
        val container = findViewById<LinearLayout?>(R.id.layoutOrders)
        if (container == null) {
            Toast.makeText(this, "Layout error: layoutOrders not found", Toast.LENGTH_SHORT).show()
            return
        }
        container.removeAllViews()

        db.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    addSimpleMessage(container, "No orders yet.")
                    return@addOnSuccessListener
                }

                for (doc in snap.documents) {
                    val row = layoutInflater.inflate(R.layout.row_order_item, container, false)

                    val email = doc.getString("userEmail")
                        ?: doc.getString("userId")
                        ?: "unknown"

                    val status = doc.getString("status") ?: "Pending"

                    val totalRaw = doc.get("total") ?: doc.get("totalPrice")
                    val totalValue = when (totalRaw) {
                        is Number -> totalRaw.toDouble()
                        is String -> totalRaw.toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }

                    val createdAny = doc.get("createdAt")
                    val createdAt: Date? = when (createdAny) {
                        is Timestamp -> createdAny.toDate()
                        is Long -> Date(createdAny)
                        is Double -> Date(createdAny.toLong())
                        else -> null
                    }

                    row.findViewById<TextView?>(R.id.tvUserEmail)?.text = email
                    row.findViewById<TextView?>(R.id.tvStatus)?.text = status
                    row.findViewById<TextView?>(R.id.tvTotalRow)?.text =
                        String.format(Locale.UK, "£%.2f", totalValue)
                    row.findViewById<TextView?>(R.id.tvCreatedAt)?.text =
                        createdAt?.let { df.format(it) } ?: ""

                    val orderId = doc.id
                    val btnAdvance = row.findViewById<MaterialButton?>(R.id.btnAdvanceStatus)

                    btnAdvance?.setOnClickListener {
                        val nextStatus = when {
                            status.equals("Pending", true) -> "In progress"
                            status.equals("In progress", true) -> "Completed"
                            else -> "Completed"
                        }

                        db.collection("orders").document(orderId)
                            .update("status", nextStatus)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show()
                                loadOrders()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    e.message ?: "Failed to update status",
                                    Toast.LENGTH_LONG
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
