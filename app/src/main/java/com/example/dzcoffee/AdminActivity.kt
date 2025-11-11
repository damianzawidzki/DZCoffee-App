package com.example.dzcoffee

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class AdminActivity : AppCompatActivity() {

    private lateinit var layoutOrders: LinearLayout
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnRefresh: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        layoutOrders = findViewById(R.id.layoutOrders)
        btnLogout = findViewById(R.id.btnLogout)
        btnRefresh = findViewById(R.id.btnRefresh)

        btnLogout.setOnClickListener { performLogout() }
        btnRefresh.setOnClickListener { loadOrders() }

        loadOrders()
    }

    private fun performLogout() {
        auth.signOut()
        CartManager.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadOrders() {
        layoutOrders.removeAllViews()

        db.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    addInfoRow("No orders yet")
                    return@addOnSuccessListener
                }

                val inflater = LayoutInflater.from(this)
                val sdf = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

                for (doc in snapshot.documents) {
                    val row = inflater.inflate(R.layout.row_admin_order, layoutOrders, false)

                    val tvTitle = row.findViewById<TextView>(R.id.tvOrderTitle)
                    val tvDetails = row.findViewById<TextView>(R.id.tvOrderDetails)
                    val tvStatus = row.findViewById<TextView>(R.id.tvOrderStatus)
                    val btnMarkDone = row.findViewById<MaterialButton>(R.id.btnMarkDone)

                    val orderId = doc.id
                    val shortId = orderId.takeLast(6)

                    val userId = doc.getString("userId") ?: ""
                    val email = doc.getString("userEmail") ?: "Unknown"
                    val total = doc.getDouble("total") ?: 0.0
                    val statusRaw = doc.getString("status") ?: "New"
                    val ts = doc.getTimestamp("createdAt")
                    val createdText = ts?.toDate()?.let { sdf.format(it) } ?: "?"

                    val items = doc.get("items") as? List<*>
                    val itemsCount = items?.size ?: 0

                    val rating = doc.getDouble("rating")
                    val feedback = doc.getString("feedback")

                    val feedbackLine = when {
                        rating != null && !feedback.isNullOrBlank() ->
                            "\nRating: ${fmtOne(rating)}★ · \"$feedback\""
                        rating != null ->
                            "\nRating: ${fmtOne(rating)}★"
                        !feedback.isNullOrBlank() ->
                            "\nFeedback: \"$feedback\""
                        else -> ""
                    }

                    tvTitle.text = "Order $shortId · £${fmt(total)}"
                    tvDetails.text = "Customer: $email\nItems: $itemsCount · Placed: $createdText$feedbackLine"
                    tvStatus.text = "Status: $statusRaw"

                    val finalStatus = statusRaw.equals("Completed", true) ||
                            statusRaw.equals("Cancelled", true)

                    if (finalStatus) {
                        btnMarkDone.isEnabled = false
                        btnMarkDone.text = "Done"
                        btnMarkDone.alpha = 0.6f
                    } else {
                        val nextStatus = nextStatus(statusRaw)
                        btnMarkDone.isEnabled = true
                        btnMarkDone.text = when (nextStatus) {
                            "Preparing" -> "Mark preparing"
                            "Collect" -> "Mark collect"
                            "Completed" -> "Mark completed"
                            else -> "Next status"
                        }
                        btnMarkDone.alpha = 1f

                        btnMarkDone.setOnClickListener {
                            if (userId.isBlank()) {
                                Toast.makeText(this, "Missing user id for this order", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            updateStatusAndNotify(orderId, userId, shortId, nextStatus)
                        }
                    }

                    layoutOrders.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading orders: ${e.message}", Toast.LENGTH_SHORT).show()
                addInfoRow("Error loading orders")
            }
    }

    private fun nextStatus(current: String): String {
        return when (current.lowercase(Locale.UK)) {
            "pending", "new" -> "Preparing"
            "preparing" -> "Collect"
            "collect", "ready", "ready to collect" -> "Completed"
            else -> "Completed"
        }
    }

    private fun updateStatusAndNotify(
        orderId: String,
        userId: String,
        shortId: String,
        newStatus: String
    ) {
        db.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener {
                sendStatusNotification(userId, shortId, newStatus)
                Toast.makeText(this, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                loadOrders()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendStatusNotification(userId: String, shortId: String, newStatus: String) {
        val data = hashMapOf(
            "userId" to userId,
            "title" to "Order status updated",
            "message" to "Your order $shortId is now $newStatus",
            "orderId" to shortId,
            "type" to "status",
            "read" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("notifications")
            .add(data)
            .addOnFailureListener {
                // Ignore notification failure for admin
            }
    }

    private fun addInfoRow(text: String) {
        val tv = TextView(this)
        tv.text = text
        tv.setTextColor(Color.WHITE)
        tv.setPadding(16, 16, 16, 16)
        layoutOrders.addView(tv)
    }

    private fun fmt(v: Double) = String.format(Locale.UK, "%.2f", v)
    private fun fmtOne(v: Double) = String.format(Locale.UK, "%.1f", v)
}
