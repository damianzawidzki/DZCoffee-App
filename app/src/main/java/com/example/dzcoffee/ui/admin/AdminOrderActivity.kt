package com.example.dzcoffee.ui.admin

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.android.material.button.MaterialButton
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

        val btnBack = findViewById<MaterialButton>(R.id.btnBackAdminOrders)
        val btnRefresh = findViewById<MaterialButton>(R.id.btnRefreshOrders)

        btnBack.setOnClickListener { finish() }
        btnRefresh.setOnClickListener { loadOrders() }

        loadOrders()
    }

    private fun loadOrders() {
        val container = findViewById<LinearLayout>(R.id.layoutOrders)
        container.removeAllViews()

        db.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                for (doc in snap.documents) {
                    val row = layoutInflater.inflate(R.layout.row_order_item, container, false)

                    val email = doc.getString("userEmail") ?: doc.getString("userId") ?: ""
                    val status = doc.getString("status") ?: "Pending"
                    val total = doc.getString("total") ?: "0.00"
                    val createdAt = doc.getLong("createdAt") ?: 0L

                    row.findViewById<TextView>(R.id.tvUserEmail).text = email
                    row.findViewById<TextView>(R.id.tvStatus).text = status
                    row.findViewById<TextView>(R.id.tvTotalRow).text = "£$total"
                    row.findViewById<TextView>(R.id.tvCreatedAt).text =
                        if (createdAt > 0) df.format(Date(createdAt)) else ""

                    val orderId = doc.id

                    row.findViewById<MaterialButton>(R.id.btnAdvanceStatus).setOnClickListener {
                        val next = when {
                            status.equals("Pending", true) -> "In progress"
                            status.equals("In progress", true) -> "Completed"
                            else -> "Completed"
                        }
                        db.collection("orders").document(orderId)
                            .update("status", next)
                    }

                    container.addView(row)
                }
            }
    }
}
