package com.example.dzcoffee

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val tvWelcome = findViewById<TextView?>(R.id.tvWelcomeName)
        val user = FirebaseAuth.getInstance().currentUser
        if (tvWelcome != null && user != null) {
            val uid = user.uid
            val email = user.email ?: ""
            // Prefer users/{uid}
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("firstName")
                        ?: doc.getString("login")
                        ?: email.substringBefore("@")
                    tvWelcome.text = "Welcome, $name"
                }
                .addOnFailureListener {
                    tvWelcome.text = "Welcome, ${email.substringBefore("@")}"
                }
        }
    }
}
