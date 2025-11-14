package com.example.dzcoffee.ui.superadmin

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterAdminActivity : AppCompatActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtNote: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_admin)

        edtFirstName = findViewById(R.id.edtAdminFirstName)
        edtLastName = findViewById(R.id.edtAdminLastName)
        edtUsername = findViewById(R.id.edtAdminUsername)
        edtEmail = findViewById(R.id.edtAdminEmail)
        edtPhone = findViewById(R.id.edtAdminPhone)
        edtNote = findViewById(R.id.edtAdminNote)

        val btnSave = findViewById<MaterialButton>(R.id.btnSaveAdmin)
        val btnBack = findViewById<MaterialButton>(R.id.btnBackRegisterAdmin)

        btnSave.setOnClickListener { saveAdmin() }
        btnBack.setOnClickListener { finish() }
    }

    private fun saveAdmin() {
        val firstName = edtFirstName.text.toString().trim()
        val lastName = edtLastName.text.toString().trim()
        val username = edtUsername.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val note = edtNote.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill first name, last name, username and email", Toast.LENGTH_SHORT).show()
            return
        }

        val btnSave = findViewById<MaterialButton>(R.id.btnSaveAdmin)
        btnSave.isEnabled = false

        // Data for "admin" collection
        val adminData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "username" to username,
            "email" to email,
            "phone" to phone,
            "note" to note,
            "active" to true,
            "createdAt" to FieldValue.serverTimestamp()
        )

        // Data for "usernames" collection -> used by login screen
        val usernameData = hashMapOf(
            "email" to email,
            "role" to "admin"
        )

        // Save in admin/{username} and usernames/{username}
        db.collection("admin").document(username).set(adminData)
            .continueWithTask {
                db.collection("usernames").document(username).set(usernameData)
            }
            .addOnSuccessListener {
                Toast.makeText(this, "Admin saved", Toast.LENGTH_SHORT).show()
                btnSave.isEnabled = true
                clearForm()
            }
            .addOnFailureListener { e ->
                btnSave.isEnabled = true
                Toast.makeText(
                    this,
                    e.message ?: "Failed to save admin",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun clearForm() {
        edtFirstName.setText("")
        edtLastName.setText("")
        edtUsername.setText("")
        edtEmail.setText("")
        edtPhone.setText("")
        edtNote.setText("")
    }
}
