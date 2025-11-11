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

        edtLogin = findViewById(R.id.edtLogin)
        edtPassword = findViewById(R.id.edtPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvRegister = findViewById(R.id.tvRegister)

        btnSignIn.setOnClickListener { attemptLogin() }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterCustomerActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = edtLogin.text.toString().trim()
        val passwordText = edtPassword.text.toString()

        if (username.isEmpty()) {
            edtLogin.error = "Enter username"
            return
        }
        if (passwordText.isEmpty()) {
            edtPassword.error = "Enter password"
            return
        }

        setLoading(true)

        // Always treat input as username, then look up email
        db.collection("usernames")
            .document(username)
            .get()
            .addOnSuccessListener { snap ->
                val email = snap.getString("email")
                    ?: snap.getString("userEmail")
                    ?: snap.getString("mail")

                if (email.isNullOrBlank()) {
                    setLoading(false)
                    showToast("Unknown username or no email linked")
                } else {
                    signInWithEmail(email, passwordText)
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showToast("Login failed: ${e.message}")
            }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                routeAfterLogin()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showToast("Login failed: ${e.message}")
            }
    }

    private fun routeAfterLogin() {
        val user = auth.currentUser
        if (user == null) {
            setLoading(false)
            showToast("Login error, please try again")
            return
        }

        val uid = user.uid

        // First check Customer collection (role stored there)
        db.collection("Customer").document(uid)
            .get()
            .addOnSuccessListener { customerDoc ->
                val roleFromCustomer = customerDoc.getString("role")

                if (customerDoc.exists() && !roleFromCustomer.isNullOrBlank()) {
                    openByRole(roleFromCustomer)
                } else {
                    checkAdminCollections(uid)
                }
            }
            .addOnFailureListener {
                checkAdminCollections(uid)
            }
    }

    private fun checkAdminCollections(uid: String) {
        // Check "admin" collection
        db.collection("admin").document(uid)
            .get()
            .addOnSuccessListener { adminDoc ->
                if (adminDoc.exists()) {
                    openByRole("admin")
                } else {
                    // Check "superadmins" collection
                    db.collection("superadmins").document(uid)
                        .get()
                        .addOnSuccessListener { superDoc ->
                            if (superDoc.exists()) {
                                openByRole("superadmin")
                            } else {
                                openByRole("customer")
                            }
                        }
                        .addOnFailureListener {
                            openByRole("customer")
                        }
                }
            }
            .addOnFailureListener {
                openByRole("customer")
            }
    }

    private fun openByRole(role: String) {
        setLoading(false)

        val target = when (role.lowercase()) {
            "admin" -> AdminActivity::class.java
            "superadmin" -> SuperAdminActivity::class.java
            else -> HomeActivity::class.java   // customer goes to HomeActivity
        }

        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        val root = findViewById<View>(android.R.id.content)
        root.isEnabled = !loading
        btnSignIn.isEnabled = !loading
        btnSignIn.alpha = if (loading) 0.6f else 1f
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
