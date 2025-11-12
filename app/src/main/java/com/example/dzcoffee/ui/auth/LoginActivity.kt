package com.example.dzcoffee.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.data.datasource.RoleNavigator
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Login screen with username -> email mapping and role navigation
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

        btnSignIn.setOnClickListener { handleLogin() }

        // Go to customer registration
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterCustomerActivity::class.java))
        }
    }

    private fun handleLogin() {
        val loginInput = edtLogin.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (loginInput.isEmpty() || password.isEmpty()) {
            showToast("Please enter login and password")
            return
        }

        setLoading(true)

        // If it's already email
        if (loginInput.contains("@")) {
            loginWithEmail(loginInput, password)
        } else {
            // Username -> lookup email in 'usernames' collection
            val usernamesRef = db.collection("usernames").document(loginInput)
            usernamesRef.get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        setLoading(false)
                        edtLogin.error = "This login does not exist"
                        return@addOnSuccessListener
                    }

                    val email = doc.getString("email")
                    if (email.isNullOrBlank()) {
                        setLoading(false)
                        showToast("Cannot find email for this user")
                    } else {
                        loginWithEmail(email, password)
                    }
                }
                .addOnFailureListener {
                    setLoading(false)
                    showToast("Failed to load user")
                }
        }
    }

    // Login with FirebaseAuth using email
    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser
                if (user == null) {
                    setLoading(false)
                    showToast("Login failed")
                    return@addOnSuccessListener
                }
                // Delegate role decision to com.example.dzcoffee.com.example.dzcoffee.data.datasource.RoleNavigator
                RoleNavigator.go(this, user.uid, user.email)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showToast(e.message ?: "Login failed")
            }
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


