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

class RegisterAdminActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_register_admin)

        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtDob = findViewById(R.id.edtDob)
        edtEmail = findViewById(R.id.edtEmail)
        edtLoginName = findViewById(R.id.edtLoginName)
        edtPassword = findViewById(R.id.edtPasswordReg)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegisterAdmin)
        tvGoLogin = findViewById(R.id.tvGoLoginAdmin)

        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener { registerAdmin() }
    }

    private fun registerAdmin() {
        val firstName = edtFirstName.text.toString().trim()
        val lastName  = edtLastName.text.toString().trim()
        val dob       = edtDob.text.toString().trim()
        val email     = edtEmail.text.toString().trim()
        val loginName = edtLoginName.text.toString().trim()
        val password  = edtPassword.text.toString()
        val confirm   = edtConfirmPassword.text.toString()

        if (firstName.isEmpty()) { edtFirstName.error = "First name required"; return }
        if (lastName.isEmpty())  { edtLastName.error  = "Last name required";  return }
        if (dob.isEmpty())       { edtDob.error       = "Date of birth required"; return }
        if (email.isEmpty() || !email.contains("@")) { edtEmail.error = "Valid email required"; return }
        if (loginName.isEmpty()) { edtLoginName.error = "Username required"; return }
        if (password.length < 6) { edtPassword.error  = "Minimum 6 characters"; return }
        if (password != confirm) { edtConfirmPassword.error = "Passwords do not match"; return }

        btnRegister.isEnabled = false

        db.collection("usernames").document(loginName).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    btnRegister.isEnabled = true
                    edtLoginName.error = "Username already in use"
                } else {
                    createAuthUser(firstName, lastName, dob, email, loginName, password)
                }
            }
            .addOnFailureListener {
                btnRegister.isEnabled = true
                Toast.makeText(this, "Username check failed", Toast.LENGTH_LONG).show()
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
                val uid = result.user?.uid ?: run {
                    btnRegister.isEnabled = true
                    Toast.makeText(this, "Missing user ID", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                val customerData = hashMapOf(
                    "uID" to uid,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "DOB" to dob,
                    "email" to email,
                    "login" to loginName,
                    "role" to "admin",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                val usernameData = hashMapOf(
                    "email" to email,
                    "role" to "admin",
                    "uID" to uid
                )

                val adminData = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "DOB" to dob,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.runBatch { b ->
                    b.set(db.collection("Customer").document(uid), customerData)
                    b.set(db.collection("usernames").document(loginName), usernameData)
                    b.set(db.collection("admin").document(uid), adminData)
                }.addOnSuccessListener {
                    Toast.makeText(this, "Admin account created. Please log in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }.addOnFailureListener { e ->
                    btnRegister.isEnabled = true
                    Toast.makeText(this, "Save error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                btnRegister.isEnabled = true
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
