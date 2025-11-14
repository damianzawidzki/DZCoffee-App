package com.example.dzcoffee.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterCustomerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtDob: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtLoginName: EditText
    private lateinit var edtPasswordReg: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegisterCustomer: Button   // works also if view is MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_customer)

        auth = FirebaseAuth.getInstance()

        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtDob = findViewById(R.id.edtDob)
        edtEmail = findViewById(R.id.edtEmail)
        edtLoginName = findViewById(R.id.edtLoginName)
        edtPasswordReg = findViewById(R.id.edtPasswordReg)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegisterCustomer = findViewById(R.id.btnRegisterCustomer)

        btnRegisterCustomer.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val firstName = edtFirstName.text.toString().trim()
        val lastName = edtLastName.text.toString().trim()
        val dob = edtDob.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val loginName = edtLoginName.text.toString().trim()  // username for login
        val password = edtPasswordReg.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() ||
            email.isEmpty() || loginName.isEmpty() || password.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        btnRegisterCustomer.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    btnRegisterCustomer.isEnabled = true
                    Toast.makeText(this, "Registration error: missing user id", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 1) Save customer profile in "Customer" collection
                val customerData = hashMapOf(
                    "uid" to uid,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "dob" to dob,
                    "loginName" to loginName,
                    "email" to email,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.collection("Customer").document(uid)
                    .set(customerData)
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            e.message ?: "Failed to save customer data",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                // 2) Save username mapping for login (username OR email)
                val usernameData = hashMapOf(
                    "email" to email,
                    "role" to "customer",
                    "uID" to uid
                )

                db.collection("usernames").document(loginName)
                    .set(usernameData)
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            e.message ?: "Failed to save username mapping",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                btnRegisterCustomer.isEnabled = true

                // Go back to login screen
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                btnRegisterCustomer.isEnabled = true
                Toast.makeText(this, e.message ?: "Registration failed", Toast.LENGTH_LONG).show()
            }
    }
}
