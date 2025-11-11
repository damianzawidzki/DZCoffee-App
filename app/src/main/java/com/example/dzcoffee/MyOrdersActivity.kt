package com.example.dzcoffee

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

class MyOrdersActivity : AppCompatActivity() {

    private lateinit var layoutOrders: LinearLayout
    private lateinit var btnBack: MaterialButton
    private lateinit var btnRefresh: MaterialButton

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK)

    private var listener: ListenerRegistration? = null

    data class OrderLine(
        val name: String,
        val size: String,
        val milk: String,
        val sugar: String,
        val unitPrice: Double,
        val quantity: Int,
        val totalPrice: Double
    )

    data class OrderDoc(
        val id: String,
        val createdAt: Timestamp?,
        val total: Double,
        val status: String,
        val items: List<OrderLine>,
        val rating: Double?,
        val feedback: String?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        layoutOrders = findViewById(R.id.layoutMyOrders)
        btnBack = findViewById(R.id.btnBackMyOrders)
        btnRefresh = findViewById(R.id.btnRefreshMyOrders)

        btnBack.setOnClickListener { finish() }
        btnRefresh.setOnClickListener {
            // Live updates are already active
            Toast.makeText(this, "Live updates are enabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        attachListener()
    }

    override fun onStop() {
        super.onStop()
        listener?.remove()
        listener = null
    }

    private fun attachListener() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        listener?.remove()

        listener = db.collection("orders")
            .whereEqualTo("userId", user.uid)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                if (snap == null) return@addSnapshotListener

                val orders = snap.documents.map { doc ->
                    mapOrder(doc.id, doc.data ?: emptyMap())
                }.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }

                renderOrders(orders)
            }
    }

    private fun renderOrders(orders: List<OrderDoc>) {
        layoutOrders.removeAllViews()
        val inflater = LayoutInflater.from(this)

        if (orders.isEmpty()) {
            val tv = TextView(this).apply {
                text = "You have no orders yet"
                setTextColor(Color.WHITE)
                setPadding(16, 16, 16, 16)
            }
            layoutOrders.addView(tv)
            return
        }

        for (order in orders) {
            val row = inflater.inflate(R.layout.row_my_order_item, layoutOrders, false)

            val tvStatus = row.findViewById<TextView>(R.id.tvStatusMy)
            val tvDate = row.findViewById<TextView>(R.id.tvCreatedAtMy)
            val tvTotal = row.findViewById<TextView>(R.id.tvTotalMy)
            val btnDetails = row.findViewById<MaterialButton>(R.id.btnDetailsMy)
            val btnFeedback = row.findViewById<MaterialButton>(R.id.btnFeedbackMy)
            val btnCancel = row.findViewById<MaterialButton>(R.id.btnCancelMy)

            tvStatus.text = order.status
            tvDate.text = order.createdAt?.let { df.format(it.toDate()) } ?: "-"
            tvTotal.text = String.format(Locale.UK, "£%.2f", order.total)

            // Details button
            btnDetails.setOnClickListener { showDetails(order) }

            // Cancel button only for Pending/New
            val cancellable = order.status.equals("Pending", true) ||
                    order.status.equals("New", true)
            btnCancel.visibility = if (cancellable) View.VISIBLE else View.GONE
            btnCancel.isEnabled = cancellable
            btnCancel.alpha = if (cancellable) 1f else 0.4f

            btnCancel.setOnClickListener {
                if (!cancellable) return@setOnClickListener
                confirmCancel(order.id)
            }

            // Feedback button only for Completed
            val canLeaveFeedback = order.status.equals("Completed", true) &&
                    order.rating == null
            val hasFeedback = order.rating != null

            when {
                canLeaveFeedback -> {
                    btnFeedback.visibility = View.VISIBLE
                    btnFeedback.isEnabled = true
                    btnFeedback.text = "Feedback"
                    btnFeedback.alpha = 1f
                    btnFeedback.setOnClickListener {
                        openFeedbackDialog(order)
                    }
                }
                hasFeedback -> {
                    btnFeedback.visibility = View.VISIBLE
                    btnFeedback.isEnabled = false
                    val r = order.rating ?: 0.0
                    btnFeedback.text = "Rated: ${String.format(Locale.UK, "%.1f★", r)}"
                    btnFeedback.alpha = 0.6f
                }
                else -> {
                    btnFeedback.visibility = View.GONE
                }
            }

            layoutOrders.addView(row)
        }
    }

    private fun confirmCancel(orderId: String) {
        AlertDialog.Builder(this)
            .setTitle("Cancel order")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes") { _, _ ->
                db.collection("orders").document(orderId)
                    .update("status", "Cancelled")
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Cancel failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun openFeedbackDialog(order: OrderDoc) {
        val view = layoutInflater.inflate(R.layout.dialog_feedback, null)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val edtComment = view.findViewById<EditText>(R.id.edtFeedbackComment)

        AlertDialog.Builder(this)
            .setTitle("Leave feedback")
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating.toDouble()
                val comment = edtComment.text.toString().trim()

                if (rating <= 0.0) {
                    Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                db.collection("orders").document(order.id)
                    .update(
                        mapOf(
                            "rating" to rating,
                            "feedback" to comment
                        )
                    )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Feedback failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDetails(order: OrderDoc) {
        val b = StringBuilder()
        b.append("Status: ").append(order.status).append("\n")
        b.append("Date: ").append(order.createdAt?.let { df.format(it.toDate()) } ?: "-").append("\n")

        order.rating?.let {
            b.append("Rating: ").append(String.format(Locale.UK, "%.1f", it)).append("★").append("\n")
        }
        order.feedback?.takeIf { it.isNotBlank() }?.let {
            b.append("Feedback: ").append(it).append("\n")
        }

        b.append("\nItems:\n")

        for (it in order.items) {
            val opts = buildString {
                if (it.size.isNotBlank()) append(it.size)
                if (it.milk.isNotBlank()) append(if (isNotEmpty()) ", " else "").append(it.milk)
                if (it.sugar.isNotBlank()) append(if (isNotEmpty()) ", " else "").append("Sugar: ${it.sugar}")
            }
            b.append("• ").append(it.name)
                .append(if (opts.isNotBlank()) " ($opts)" else "")
                .append("  x").append(it.quantity)
                .append("  = £").append(String.format(Locale.UK, "%.2f", it.totalPrice))
                .append("\n")
        }
        b.append("\nTotal: £").append(String.format(Locale.UK, "%.2f", order.total))

        AlertDialog.Builder(this)
            .setTitle("Order details")
            .setMessage(b.toString())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun mapOrder(id: String, data: Map<String, Any>): OrderDoc {
        val createdAt = data["createdAt"] as? Timestamp
        val total = (data["total"] as? Number)?.toDouble() ?: 0.0
        val status = (data["status"] as? String) ?: "Pending"
        val rating = (data["rating"] as? Number)?.toDouble()
        val feedback = data["feedback"] as? String

        val itemsRaw = data["items"] as? List<*> ?: emptyList<Any>()
        val items = itemsRaw.map { m ->
            val mm = (m as? Map<*, *>)?.mapKeys { it.key?.toString() ?: "" } ?: emptyMap()
            OrderLine(
                name = mm["name"] as? String ?: "",
                size = mm["size"] as? String ?: "",
                milk = mm["milk"] as? String ?: "",
                sugar = mm["sugar"] as? String ?: "",
                unitPrice = (mm["unitPrice"] as? Number)?.toDouble() ?: 0.0,
                quantity = (mm["quantity"] as? Number)?.toInt() ?: 0,
                totalPrice = (mm["totalPrice"] as? Number)?.toDouble()
                    ?: (mm["lineTotal"] as? Number)?.toDouble()
                    ?: 0.0
            )
        }

        return OrderDoc(
            id = id,
            createdAt = createdAt,
            total = total,
            status = status,
            items = items,
            rating = rating,
            feedback = feedback
        )
    }
}
