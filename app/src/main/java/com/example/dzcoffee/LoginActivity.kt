package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var edtLogin: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var tvRegister: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Bind views
        edtLogin = findViewById(R.id.edtLogin)          // username (not email)
        edtPassword = findViewById(R.id.edtPassword)    // password
        btnSignIn = findViewById(R.id.btnSignIn)
        tvRegister = findViewById(R.id.tvRegister)

        // Go to register screen for customers
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterCustomerActivity::class.java))
        }

        // Sign in logic
        btnSignIn.setOnClickListener {
            val login = edtLogin.text.toString().trim()
            val password = edtPassword.text.toString()

            if (login.isEmpty()) {
                edtLogin.error = "Login required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                edtPassword.error = "Password required"
                return@setOnClickListener
            }

            signInWithUsername(login, password)
        }
    }

    private fun signInWithUsername(login: String, password: String) {
        setLoading(true)

        // 1) Find mapping for this username
        db.collection("usernames").document(login).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    setLoading(false)
                    showToast("Invalid login or password")
                    return@addOnSuccessListener
                }

                val email = doc.getString("email")
                val role = doc.getString("role") ?: "customer"

                if (email.isNullOrEmpty()) {
                    setLoading(false)
                    showToast("Account configuration error (no email)")
                    return@addOnSuccessListener
                }

                // 2) Sign in with mapped email + password via FirebaseAuth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        setLoading(false)
                        navigateByRole(role)
                    }
                    .addOnFailureListener {
                        setLoading(false)
                        showToast("Invalid login or password")
                    }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showToast("Login error: ${e.message}")
            }
    }

    // Decide where to go based on role from Firestore
    private fun navigateByRole(role: String) {
        val target = when (role.lowercase()) {
            "admin" -> AdminActivity::class.java
            "superadmin" -> SuperAdminActivity::class.java
            else -> HomeActivity::class.java  // default is customer
        }

        startActivity(Intent(this, target))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        val root = findViewById<View>(android.R.id.content)
        root.isEnabled = !loading
        btnSignIn.isEnabled = !loading
        btnSignIn.alpha = if (loading) 0.6f else 1f
    }

    private fun showToast(msg: String)  {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
