package com.example.dzcoffee.ui.superadmin

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.dzcoffee.R
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// Register screen for admin (should be used only by superadmin ideally)
class RegisterAdminActivity : AppCompatActivity() {

    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtDob: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtLoginName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvGoLoginAdmin: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_admin)

        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtDob = findViewById(R.id.edtDob)
        edtEmail = findViewById(R.id.edtEmail)
        edtLoginName = findViewById(R.id.edtLoginName)
        edtPassword = findViewById(R.id.edtPasswordReg)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegisterAdmin)
        tvGoLoginAdmin = findViewById(R.id.tvGoLoginAdmin)

        btnRegister.setOnClickListener { handleRegister() }

        tvGoLoginAdmin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleRegister() {
        val firstName = edtFirstName.text.toString().trim()
        val lastName = edtLastName.text.toString().trim()
        val dob = edtDob.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val login = edtLoginName.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirm = edtConfirmPassword.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
            login.isEmpty() || password.isEmpty()
        ) {
            showToast("Please fill required fields")
            return
        }

        if (password != confirm) {
            edtConfirmPassword.error = "Passwords do not match"
            return
        }

        btnRegister.isEnabled = false

        val usernamesRef = db.collection("usernames").document(login)
        usernamesRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    btnRegister.isEnabled = true
                    edtLoginName.error = "This login is already taken"
                } else {
                    createAdminAuth(firstName, lastName, dob, email, login, password)
                }
            }
            .addOnFailureListener {
                btnRegister.isEnabled = true
                showToast("Failed to check username")
            }
    }

    private fun createAdminAuth(
        firstName: String,
        lastName: String,
        dob: String,
        email: String,
        login: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    btnRegister.isEnabled = true
                    showToast("Registration failed")
                    return@addOnSuccessListener
                }

                val profile = hashMapOf(
                    "uid" to uid,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "dob" to dob,
                    "email" to email,
                    "login" to login,
                    "role" to "admin",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                val batch = db.batch()
                val usersRef = db.collection("users").document(uid)
                val usernamesRef = db.collection("usernames").document(login)

                batch.set(usersRef, profile)
                val usernameDoc = hashMapOf(
                    "email" to email,
                    "role" to "admin",
                    "uID" to uid
                )
                batch.set(usernamesRef, usernameDoc)

                batch.commit()
                    .addOnSuccessListener {
                        showToast("Admin registered")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnRegister.isEnabled = true
                        showToast(e.message ?: "Failed to save admin")
                    }
            }
            .addOnFailureListener { e ->
                btnRegister.isEnabled = true
                showToast(e.message ?: "Registration failed")
            }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
