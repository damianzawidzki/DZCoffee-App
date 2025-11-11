package com.example.dzcoffee

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
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

class NotificationsActivity : AppCompatActivity() {

    private lateinit var layoutNotifications: LinearLayout
    private lateinit var btnBackNotifications: MaterialButton
    private lateinit var btnMarkAllRead: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK)

    private var listener: ListenerRegistration? = null
    private var currentItems: List<NotificationItem> = emptyList()

    data class NotificationItem(
        val id: String,
        val title: String,
        val message: String,
        val createdAt: Timestamp?,
        val read: Boolean,
        val type: String?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        layoutNotifications = findViewById(R.id.layoutNotifications)
        btnBackNotifications = findViewById(R.id.btnBackNotifications)
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead)

        btnBackNotifications.setOnClickListener { finish() }

        btnMarkAllRead.setOnClickListener {
            markAllAsRead()
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

        listener = db.collection("notifications")
            .whereEqualTo("userId", user.uid)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                if (snap == null) return@addSnapshotListener

                val list = snap.documents.map { doc ->
                    NotificationItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "Notification",
                        message = doc.getString("message") ?: "",
                        createdAt = doc.getTimestamp("createdAt"),
                        read = doc.getBoolean("read") ?: false,
                        type = doc.getString("type")
                    )
                }.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }

                currentItems = list
                renderNotifications(list)
            }
    }

    private fun renderNotifications(items: List<NotificationItem>) {
        layoutNotifications.removeAllViews()
        val inflater = LayoutInflater.from(this)

        if (items.isEmpty()) {
            val tv = TextView(this).apply {
                text = "You have no notifications"
                setTextColor(Color.WHITE)
                setPadding(16, 16, 16, 16)
            }
            layoutNotifications.addView(tv)
            btnMarkAllRead.isEnabled = false
            btnMarkAllRead.alpha = 0.6f
            return
        }

        btnMarkAllRead.isEnabled = items.any { !it.read }
        btnMarkAllRead.alpha = if (btnMarkAllRead.isEnabled) 1f else 0.6f

        for (item in items) {
            val row = inflater.inflate(R.layout.row_notification_item, layoutNotifications, false)

            val tvTitle = row.findViewById<TextView>(R.id.tvNotifTitle)
            val tvBody = row.findViewById<TextView>(R.id.tvNotifBody)

            val dateText = item.createdAt?.let { df.format(it.toDate()) } ?: "-"

            tvTitle.text = if (item.read) {
                item.title
            } else {
                "${item.title} • NEW"
            }

            tvBody.text = "$dateText · ${item.message}"

            if (!item.read) {
                row.setBackgroundColor(Color.parseColor("#FFF3E0"))
            } else {
                row.setBackgroundColor(Color.parseColor("#F5E0C8"))
            }

            row.setOnClickListener {
                showNotificationDialog(item)
            }

            layoutNotifications.addView(row)
        }
    }

    private fun showNotificationDialog(item: NotificationItem) {
        val dateText = item.createdAt?.let { df.format(it.toDate()) } ?: "-"
        val fullText = "Date: $dateText\n\n${item.message}"

        AlertDialog.Builder(this)
            .setTitle(item.title)
            .setMessage(fullText)
            .setPositiveButton("OK", null)
            .show()

        if (!item.read) {
            db.collection("notifications")
                .document(item.id)
                .update("read", true)
        }
    }

    private fun markAllAsRead() {
        val unread = currentItems.filter { !it.read }
        if (unread.isEmpty()) return

        val batch = db.batch()
        for (item in unread) {
            val ref = db.collection("notifications").document(item.id)
            batch.update(ref, "read", true)
        }
        batch.commit()
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to mark all as read: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
