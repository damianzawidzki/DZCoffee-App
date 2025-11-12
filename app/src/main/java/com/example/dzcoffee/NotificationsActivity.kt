package com.example.dzcoffee

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

// Screen for user notifications from Firestore "notifications" collection
class NotificationsActivity : AppCompatActivity() {

    private lateinit var layoutNotifications: LinearLayout
    private lateinit var btnBack: MaterialButton
    private lateinit var btnMarkAllRead: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK)

    private var listener: ListenerRegistration? = null

    data class NotificationDoc(
        val id: String,
        val title: String,
        val message: String,
        val createdAt: Timestamp?,
        val read: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // layout_notifications.xml – zakładam:
        // - LinearLayout listy: layoutNotifications
        // - przycisk wstecz: btnBackNotifications
        // - przycisk "Mark all": btnMarkAllRead
        layoutNotifications = findViewById(R.id.layoutNotifications)
        btnBack = findViewById(R.id.btnBackNotifications)
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead)

        btnBack.setOnClickListener { finish() }

        btnMarkAllRead.setOnClickListener {
            val user = auth.currentUser ?: return@setOnClickListener
            markAllAsRead(user.uid)
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

    // Listen for notifications for current user
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

                val items = snap.documents.map { d ->
                    val data = d.data ?: emptyMap()
                    NotificationDoc(
                        id = d.id,
                        title = data["title"] as? String ?: "Notification",
                        message = data["message"] as? String ?: "",
                        createdAt = data["createdAt"] as? Timestamp,
                        read = data["read"] as? Boolean ?: false
                    )
                }.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }

                renderNotifications(items)
            }
    }

    // Show notifications in list using row_notification_item.xml
    private fun renderNotifications(list: List<NotificationDoc>) {
        layoutNotifications.removeAllViews()
        val inflater = LayoutInflater.from(this)

        if (list.isEmpty()) {
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

        btnMarkAllRead.isEnabled = true
        btnMarkAllRead.alpha = 1f

        for (n in list) {
            val row = inflater.inflate(R.layout.row_notification_item, layoutNotifications, false)

            val tvTitle = row.findViewById<TextView>(R.id.tvNotifTitle)
            val tvBody = row.findViewById<TextView>(R.id.tvNotifBody)

            tvTitle.text = n.title

            val dateStr = n.createdAt?.let { df.format(it.toDate()) } ?: "-"
            tvBody.text = "$dateStr · ${n.message}"

            // If read, slightly dim row
            if (n.read) {
                row.alpha = 0.7f
            } else {
                row.alpha = 1f
            }

            // Tap to mark single notification as read
            row.setOnClickListener {
                if (!n.read) {
                    db.collection("notifications").document(n.id)
                        .update("read", true)
                        .addOnFailureListener {
                            // ignore
                        }
                }
            }

            layoutNotifications.addView(row)
        }
    }

    // Mark all notifications as read for user
    private fun markAllAsRead(userId: String) {
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { snap ->
                val batch = db.batch()
                for (doc in snap.documents) {
                    batch.update(doc.reference, "read", true)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
    }
}
