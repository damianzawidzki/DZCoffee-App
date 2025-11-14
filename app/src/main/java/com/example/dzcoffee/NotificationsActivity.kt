package com.example.dzcoffee

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsActivity : AppCompatActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        val btnBack = findViewById<MaterialButton>(R.id.btnBackNotifications)
        val btnRefresh = findViewById<MaterialButton>(R.id.btnRefreshNotifications)

        btnBack.setOnClickListener { finish() }
        btnRefresh.setOnClickListener { loadNotifications() }

        loadNotifications()
    }

    private fun loadNotifications() {
        val container = findViewById<LinearLayout>(R.id.layoutNotifications)
        container.removeAllViews()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            addSimpleMessage(container, "You are not logged in.")
            return
        }

        db.collection("notifications")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    addSimpleMessage(container, "No notifications yet.")
                    return@addOnSuccessListener
                }

                for (doc in snap.documents) {
                    val title = doc.getString("title") ?: "Notification"
                    val message = doc.getString("message") ?: ""
                    val ts = doc.getLong("createdAt") ?: 0L

                    val row = layoutInflater.inflate(
                        R.layout.row_notification_item,
                        container,
                        false
                    )

                    row.findViewById<TextView>(R.id.tvNotificationTitle).text = title
                    row.findViewById<TextView>(R.id.tvNotificationMessage).text = message
                    row.findViewById<TextView>(R.id.tvNotificationDate).text =
                        if (ts > 0) dateFormat.format(Date(ts)) else ""

                    container.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message ?: "Failed to load notifications", Toast.LENGTH_SHORT).show()
                addSimpleMessage(container, "Could not load notifications.")
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
