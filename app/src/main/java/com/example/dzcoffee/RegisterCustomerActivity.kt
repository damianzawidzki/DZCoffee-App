package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterCustomerActivity : AppCompatActivity() {

    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtDob: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtLoginName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvGoLogin: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_customer)

        // Bind views
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtDob = findViewById(R.id.edtDob)
        edtEmail = findViewById(R.id.edtEmail)
        edtLoginName = findViewById(R.id.edtLoginName)
        edtPassword = findViewById(R.id.edtPasswordReg)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegisterCustomer)
        tvGoLogin = findViewById(R.id.tvGoLogin)

        // Link: back to login screen
        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Register button click
        btnRegister.setOnClickListener {
            registerCustomer()
        }
    }

    private fun registerCustomer() {
        val firstName = edtFirstName.text.toString().trim()
        val lastName = edtLastName.text.toString().trim()
        val dob = edtDob.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val loginName = edtLoginName.text.toString().trim()
        val password = edtPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()

        // Basic validation
        if (firstName.isEmpty()) {
            edtFirstName.error = "First name required"
            return
        }
        if (lastName.isEmpty()) {
            edtLastName.error = "Last name required"
            return
        }
        if (dob.isEmpty()) {
            edtDob.error = "Date of birth required"
            return
        }
        if (email.isEmpty()) {
            edtEmail.error = "Email required"
            return
        }
        if (!email.contains("@")) {
            edtEmail.error = "Invalid email"
            return
        }
        if (loginName.isEmpty()) {
            edtLoginName.error = "Login required"
            return
        }
        if (password.isEmpty()) {
            edtPassword.error = "Password required"
            return
        }
        if (password.length < 6) {
            edtPassword.error = "Minimum 6 characters"
            return
        }
        if (password != confirmPassword) {
            edtConfirmPassword.error = "Passwords do not match"
            return
        }

        btnRegister.isEnabled = false

        // Step 1: check if username already exists
        val usernamesRef = db.collection("usernames").document(loginName)

        usernamesRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    btnRegister.isEnabled = true
                    edtLoginName.error = "This login is already taken"
                } else {
                    // Username is free -> create Auth user
                    createAuthUser(firstName, lastName, dob, email, loginName, password)
                }
            }
            .addOnFailureListener { e ->
                btnRegister.isEnabled = true
                Toast.makeText(
                    this,
                    "Username check failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun createAuthUser(
        firstName: String,
        lastName: String,
        dob: String,
        email: String,
        loginName: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid.isNullOrEmpty()) {
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        "Registration error: missing user ID",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                // Prepare Firestore data
                val customerData = hashMapOf(
                    "uID" to uid,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "DOB" to dob,
                    "email" to email,
                    "login" to loginName,
                    "role" to "customer",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                val usernameData = hashMapOf(
                    "email" to email,
                    "role" to "customer",
                    "uID" to uid
                )

                // Step 2: save both documents in one batch (all-or-nothing)
                db.runBatch { batch ->
                    val customerRef = db.collection("Customer").document(uid)
                    val usernameRef = db.collection("usernames").document(loginName)

                    batch.set(customerRef, customerData)
                    batch.set(usernameRef, usernameData)
                }.addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Account created. Please log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Go to login screen
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }.addOnFailureListener { e ->
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        "Save error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                btnRegister.isEnabled = true
                Toast.makeText(
                    this,
                    "Registration failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
