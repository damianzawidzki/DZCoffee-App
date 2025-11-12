package com.example.dzcoffee.ui.customer

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        val btnBack = findViewById<MaterialButton>(R.id.btnBackMyOrders)
        val btnRefresh = findViewById<MaterialButton>(R.id.btnRefreshMyOrders)

        btnBack.setOnClickListener { finish() }
        btnRefresh.setOnClickListener { loadOrders() }

        loadOrders()
    }

    private fun loadOrders() {
        val container = findViewById<LinearLayout>(R.id.layoutMyOrders)
        container.removeAllViews()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("orders")
            .whereEqualTo("userId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                for (doc in snap.documents) {
                    val row = layoutInflater.inflate(R.layout.row_my_order_item, container, false)

                    val status = doc.getString("status") ?: "Pending"
                    val total = doc.getString("total") ?: "0.00"
                    val createdAt = doc.getLong("createdAt") ?: 0L

                    row.findViewById<TextView>(R.id.tvStatusMy).text = status
                    row.findViewById<TextView>(R.id.tvTotalMy).text = "£$total"
                    row.findViewById<TextView>(R.id.tvCreatedAtMy).text =
                        if (createdAt > 0) df.format(Date(createdAt)) else ""

                    // Tu możesz później podpiąć szczegóły, feedback itp.
                    // val orderId = doc.id

                    container.addView(row)
                }
            }
    }
}
