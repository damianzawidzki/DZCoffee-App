package com.example.dzcoffee.ui.admin

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

// Admin screen showing customer ratings and feedback from orders
class AdminFeedbackActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var layoutNotifications: LinearLayout

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK)

    private var listener: ListenerRegistration? = null

    // Single feedback item based on order
    data class FeedbackItem(
        val orderId: String,
        val userEmail: String,
        val createdAt: Timestamp?,
        val rating: Double,
        val comment: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Reuse notifications layout to keep design consistent
        setContentView(R.layout.activity_notifications)

        // From activity_notifications.xml
        tvTitle = findViewById(R.id.tvNotificationsTitle)
        layoutNotifications = findViewById(R.id.layoutNotifications)

        // Change title text for admin feedback
        tvTitle.text = "Customer feedback"
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

    // Listen to all orders and extract only those with feedback
    private fun attachListener() {
        listener?.remove()

        listener = db.collection("orders")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                if (snap == null) return@addSnapshotListener

                val items = snap.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null

                    val rating = (data["rating"] as? Number)?.toDouble() ?: 0.0
                    val comment = (data["feedback"] as? String).orEmpty()

                    // No rating and no comment -> skip
                    if (rating <= 0.0 && comment.isBlank()) return@mapNotNull null

                    FeedbackItem(
                        orderId = doc.id,
                        userEmail = data["userEmail"] as? String ?: "user",
                        createdAt = data["createdAt"] as? Timestamp,
                        rating = rating,
                        comment = comment
                    )
                }.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }

                renderFeedback(items)
            }
    }

    // Render list using row_notification_item.xml
    private fun renderFeedback(list: List<FeedbackItem>) {
        layoutNotifications.removeAllViews()
        val inflater = LayoutInflater.from(this)

        if (list.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No feedback yet"
                setTextColor(Color.WHITE)
                setPadding(16, 16, 16, 16)
            }
            layoutNotifications.addView(tv)
            return
        }

        for (fb in list) {
            val row = inflater.inflate(R.layout.row_notification_item, layoutNotifications, false)

            val tvNotifTitle = row.findViewById<TextView>(R.id.tvNotifTitle)
            val tvNotifBody = row.findViewById<TextView>(R.id.tvNotifBody)

            val stars = String.format(Locale.UK, "%.1f★", fb.rating)
            tvNotifTitle.text = "$stars from ${fb.userEmail}"

            val dateStr = fb.createdAt?.let { df.format(it.toDate()) } ?: "-"
            val commentText =
                if (fb.comment.isBlank()) "No comment" else fb.comment

            tvNotifBody.text = "$dateStr · $commentText"

            // Click row to show more info in dialog
            row.setOnClickListener {
                showFeedbackDetails(fb)
            }

            layoutNotifications.addView(row)
        }
    }

    // Detailed popup for single feedback
    private fun showFeedbackDetails(fb: FeedbackItem) {
        val sb = StringBuilder()
        sb.append("Order ID: ").append(fb.orderId).append("\n")
        sb.append("User: ").append(fb.userEmail).append("\n")
        sb.append("Rating: ").append(String.format(Locale.UK, "%.1f★", fb.rating)).append("\n")
        fb.createdAt?.let {
            sb.append("Date: ").append(df.format(it.toDate())).append("\n")
        }
        if (fb.comment.isNotBlank()) {
            sb.append("\nComment:\n").append(fb.comment)
        }

        AlertDialog.Builder(this)
            .setTitle("Feedback details")
            .setMessage(sb.toString())
            .setPositiveButton("OK", null)
            .show()
    }
}
