package com.example.dzcoffee

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SuperAdminActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLogin: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnAddAdmin: MaterialButton
    private lateinit var btnEditAdmin: MaterialButton
    private lateinit var btnDeleteAdmin: MaterialButton
    private lateinit var btnRefresh: MaterialButton
    private lateinit var btnSignOut: MaterialButton
    private lateinit var listAdmins: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superadmin)


        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etBirthDate = findViewById(R.id.etBirthDate)
        etPhone = findViewById(R.id.etPhone)
        etLogin = findViewById(R.id.etLogin)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnAddAdmin = findViewById(R.id.btnAddAdmin)
        btnEditAdmin = findViewById(R.id.btnEditAdmin)
        btnDeleteAdmin = findViewById(R.id.btnDeleteAdmin)
        btnRefresh = findViewById(R.id.btnRefreshAdmins)
        btnSignOut = findViewById(R.id.btnSignOut)
        listAdmins = findViewById(R.id.listAdmins)

        btnAddAdmin.setOnClickListener { addAdmin() }
        btnEditAdmin.setOnClickListener { editAdmin() }
        btnDeleteAdmin.setOnClickListener { deleteAdmin() }
        btnRefresh.setOnClickListener { loadAdmins() }
        btnSignOut.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            finish()
        }

        loadAdmins()
    }

    private fun loadAdmins() {
        listAdmins.removeAllViews()
        db.collection("users").whereIn("role", listOf("admin", "superadmin"))
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val tv = TextView(this)
                    val fn = doc.getString("firstName") ?: ""
                    val ln = doc.getString("lastName") ?: ""
                    val email = doc.getString("email") ?: ""
                    val role = doc.getString("role") ?: ""
                    tv.text = "$fn $ln  •  $email  •  $role"
                    tv.textSize = 16f
                    tv.setPadding(12, 10, 12, 10)
                    listAdmins.addView(tv)
                }
                Toast.makeText(this, "Admins loaded: ${result.size()}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Load failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun addAdmin() {
        val fName = etFirstName.text.toString().trim()
        val lName = etLastName.text.toString().trim()
        val birth = etBirthDate.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val login = etLogin.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { res ->
                val uid = res.user?.uid ?: return@addOnSuccessListener
                val adminData = hashMapOf(
                    "uid" to uid,
                    "firstName" to fName,
                    "lastName" to lName,
                    "birthDate" to birth,
                    "phone" to phone,
                    "login" to login,
                    "email" to email,
                    "role" to "admin"
                )
                db.collection("users").document(uid).set(adminData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Admin added", Toast.LENGTH_SHORT).show()
                        clearFields(); loadAdmins()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Add failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun editAdmin() {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email to edit", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("users").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    Toast.makeText(this, "No admin found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                val id = snap.documents[0].id
                val data = mapOf(
                    "firstName" to etFirstName.text.toString().trim(),
                    "lastName" to etLastName.text.toString().trim(),
                    "birthDate" to etBirthDate.text.toString().trim(),
                    "phone" to etPhone.text.toString().trim(),
                    "login" to etLogin.text.toString().trim()
                )
                db.collection("users").document(id).update(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Admin updated", Toast.LENGTH_SHORT).show()
                        clearFields(); loadAdmins()
                    }
            }
    }

    private fun deleteAdmin() {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email to delete", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("users").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    Toast.makeText(this, "No admin found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                val id = snap.documents[0].id
                db.collection("users").document(id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Admin deleted (Auth account remains)", Toast.LENGTH_LONG).show()
                        clearFields(); loadAdmins()
                    }
            }
    }

    private fun clearFields() {
        etFirstName.text.clear()
        etLastName.text.clear()
        etBirthDate.text.clear()
        etPhone.text.clear()
        etLogin.text.clear()
        etEmail.text.clear()
        etPassword.text.clear()
    }
}
