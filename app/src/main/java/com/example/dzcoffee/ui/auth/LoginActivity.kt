package com.example.dzcoffee.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.ui.admin.AdminActivity
import com.example.dzcoffee.ui.customer.HomeActivity
import com.example.dzcoffee.ui.superadmin.SuperAdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var edtIdentifier: EditText   // username OR email
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        edtIdentifier = findViewById(R.id.edtIdentifier)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        btnLogin.setOnClickListener { doLogin() }

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterCustomerActivity::class.java))
        }
    }

    private fun doLogin() {
        val identifier = edtIdentifier.text.toString().trim() // username or email
        val password = edtPassword.text.toString().trim()

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username/email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Email or username?
        if (identifier.contains("@")) {
            loginWithEmailIdentifier(identifier, password)
        } else {
            loginWithUsername(identifier, password)
        }
    }

    // identifier is EMAIL, but we still use usernames collection to get role
    private fun loginWithEmailIdentifier(email: String, password: String) {
        db.collection("usernames")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    Toast.makeText(this, "User not found for this email", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val doc = snap.documents.first()
                val role = doc.getString("role") ?: "customer"
                val emailFromDoc = doc.getString("email") ?: email

                auth.signInWithEmailAndPassword(emailFromDoc, password)
                    .addOnSuccessListener {
                        openRoleScreen(role)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message ?: "Login failed", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error while checking email", Toast.LENGTH_SHORT).show()
            }
    }

    // identifier is USERNAME (doc id in `usernames` collection)
    private fun loginWithUsername(username: String, password: String) {
        db.collection("usernames")
            .document(username)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val email = doc.getString("email")
                val role = doc.getString("role") ?: "customer"

                if (email.isNullOrBlank()) {
                    Toast.makeText(this, "User has no email field", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        openRoleScreen(role)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message ?: "Login failed", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error while checking username", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openRoleScreen(role: String) {
        val intent = when (role) {
            "admin" -> Intent(this, AdminActivity::class.java)
            "superadmin" -> Intent(this, SuperAdminActivity::class.java)
            else -> Intent(this, HomeActivity::class.java)
        }

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        finish()
    }
}
