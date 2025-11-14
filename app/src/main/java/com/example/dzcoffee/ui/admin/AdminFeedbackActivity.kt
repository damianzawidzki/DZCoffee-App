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

class AdminFeedbackActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_feedback)

        findViewById<MaterialButton?>(R.id.btnBackAdminFeedback)?.setOnClickListener { finish() }
        findViewById<MaterialButton?>(R.id.btnRefreshFeedback)?.setOnClickListener { loadFeedback() }

        loadFeedback()
    }

    private fun loadFeedback() {
        val container = findViewById<LinearLayout?>(R.id.layoutFeedback)
        if (container == null) {
            Toast.makeText(this, "Layout error: layoutFeedback not found", Toast.LENGTH_SHORT).show()
            return
        }
        container.removeAllViews()

        db.collection("feedback")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    addSimpleMessage(container, "No feedback yet.")
                    return@addOnSuccessListener
                }

                for (doc in snap.documents) {
                    val row = layoutInflater.inflate(R.layout.row_feedback_item, container, false)

                    val email = doc.getString("userEmail") ?: "Unknown user"
                    val comment = doc.getString("comment") ?: "No comment"
                    val rating = when (val r = doc.get("rating")) {
                        is Number -> r.toDouble()
                        is String -> r.toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }
                    val createdAny = doc.get("createdAt")
                    val createdDate: Date? = when (createdAny) {
                        is Timestamp -> createdAny.toDate()
                        is Long -> Date(createdAny)
                        is Double -> Date(createdAny.toLong())
                        else -> null
                    }

                    row.findViewById<TextView?>(R.id.tvFeedbackTitle)?.text =
                        String.format(Locale.UK, "%.1f★ from %s", rating, email)
                    row.findViewById<TextView?>(R.id.tvFeedbackBody)?.text = comment
                    row.findViewById<TextView?>(R.id.tvFeedbackDate)?.text =
                        createdDate?.let { df.format(it) } ?: ""

                    container.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    e.message ?: "Failed to load feedback",
                    Toast.LENGTH_LONG
                ).show()
                addSimpleMessage(container, "Could not load feedback.")
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
