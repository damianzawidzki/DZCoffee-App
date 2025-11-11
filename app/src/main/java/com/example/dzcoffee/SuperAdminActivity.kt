package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SuperAdminActivity : AppCompatActivity() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var btnLogout: MaterialButton

    private lateinit var edtIdentifier: EditText
    private lateinit var btnFindUser: MaterialButton
    private lateinit var tvFoundUser: TextView

    private lateinit var edtAdminFirstName: EditText
    private lateinit var edtAdminLastName: EditText
    private lateinit var edtAdminDob: EditText
    private lateinit var edtAdminPhone: EditText
    private lateinit var edtAdminNotes: EditText
    private lateinit var btnSaveAdmin: MaterialButton
    private lateinit var btnRemoveAdmin: MaterialButton

    private lateinit var layoutAdminList: LinearLayout

    private var foundUid: String? = null
    private var foundEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superadmin)

        btnLogout = findViewById(R.id.btnLogout)

        edtIdentifier = findViewById(R.id.edtIdentifier)
        btnFindUser = findViewById(R.id.btnFindUser)
        tvFoundUser = findViewById(R.id.tvFoundUser)

        edtAdminFirstName = findViewById(R.id.edtAdminFirstName)
        edtAdminLastName = findViewById(R.id.edtAdminLastName)
        edtAdminDob = findViewById(R.id.edtAdminDob)
        edtAdminPhone = findViewById(R.id.edtAdminPhone)
        edtAdminNotes = findViewById(R.id.edtAdminNotes)
        btnSaveAdmin = findViewById(R.id.btnSaveAdmin)
        btnRemoveAdmin = findViewById(R.id.btnRemoveAdmin)

        layoutAdminList = findViewById(R.id.layoutAdminList)

        btnLogout.setOnClickListener { performLogout() }
        btnFindUser.setOnClickListener { lookupUser() }
        btnSaveAdmin.setOnClickListener { saveAdmin() }
        btnRemoveAdmin.setOnClickListener { removeAdmin() }

        loadAdminList()
    }

    private fun performLogout() {
        auth.signOut()
        CartManager.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun lookupUser() {
        val id = edtIdentifier.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(this, "Enter username or email", Toast.LENGTH_SHORT).show()
            return
        }

        foundUid = null
        foundEmail = null
        tvFoundUser.text = "-"
        clearAdminForm()

        // Try username first
        db.collection("usernames").document(id).get()
            .addOnSuccessListener { doc ->
                val emailFromUsername =
                    doc.getString("email")
                        ?: doc.getString("userEmail")

                when {
                    !emailFromUsername.isNullOrBlank() -> {
                        findCustomerByEmail(emailFromUsername)
                    }
                    id.contains("@") -> {
                        // treat identifier directly as email
                        findCustomerByEmail(id)
                    }
                    else -> {
                        tvFoundUser.text = "User not found for \"$id\""
                    }
                }
            }
            .addOnFailureListener {
                if (id.contains("@")) {
                    findCustomerByEmail(id)
                } else {
                    tvFoundUser.text = "Lookup failed"
                }
            }
    }

    private fun findCustomerByEmail(email: String) {
        db.collection("Customer")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    tvFoundUser.text = "No Customer found for $email"
                    foundUid = null
                    foundEmail = null
                    clearAdminForm()
                } else {
                    val doc = snap.documents.first()
                    val uid = doc.id
                    val firstName = doc.getString("firstName") ?: ""
                    val lastName = doc.getString("lastName") ?: ""
                    val dob = doc.getString("DOB") ?: ""
                    val phone = doc.getString("phone") ?: ""

                    foundUid = uid
                    foundEmail = email

                    tvFoundUser.text = "Found: $firstName $lastName <$email>\nUID: $uid"

                    // Prefill from Customer
                    edtAdminFirstName.setText(firstName)
                    edtAdminLastName.setText(lastName)
                    edtAdminDob.setText(dob)
                    edtAdminPhone.setText(phone)
                    edtAdminNotes.setText("")

                    // If admin doc already exists, merge its data into form
                    loadExistingAdminDetails(uid)
                }
            }
            .addOnFailureListener { e ->
                tvFoundUser.text = "Search failed: ${e.message}"
                foundUid = null
                foundEmail = null
                clearAdminForm()
            }
    }

    private fun loadExistingAdminDetails(uid: String) {
        db.collection("admin").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                val aFirst = doc.getString("firstName")
                val aLast = doc.getString("lastName")
                val aDob = doc.getString("DOB")
                val aPhone = doc.getString("phone")
                val aNotes = doc.getString("notes")

                if (!aFirst.isNullOrBlank()) edtAdminFirstName.setText(aFirst)
                if (!aLast.isNullOrBlank()) edtAdminLastName.setText(aLast)
                if (!aDob.isNullOrBlank()) edtAdminDob.setText(aDob)
                if (!aPhone.isNullOrBlank()) edtAdminPhone.setText(aPhone)
                if (!aNotes.isNullOrBlank()) edtAdminNotes.setText(aNotes)
            }
    }

    private fun clearAdminForm() {
        edtAdminFirstName.setText("")
        edtAdminLastName.setText("")
        edtAdminDob.setText("")
        edtAdminPhone.setText("")
        edtAdminNotes.setText("")
    }

    private fun saveAdmin() {
        val uid = foundUid
        val email = foundEmail

        if (uid == null || email == null) {
            Toast.makeText(this, "Find a user first", Toast.LENGTH_SHORT).show()
            return
        }

        val firstName = edtAdminFirstName.text.toString().trim()
        val lastName = edtAdminLastName.text.toString().trim()
        val dob = edtAdminDob.text.toString().trim()
        val phone = edtAdminPhone.text.toString().trim()
        val notes = edtAdminNotes.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Enter first and last name", Toast.LENGTH_SHORT).show()
            return
        }

        val current = auth.currentUser
        val adminData = hashMapOf(
            "uid" to uid,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "DOB" to dob,
            "phone" to phone,
            "notes" to notes,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        if (current != null) {
            adminData["updatedBy"] = current.uid
        }

        val customerPatch = hashMapOf(
            "role" to "admin",
            "firstName" to firstName,
            "lastName" to lastName,
            "DOB" to dob,
            "phone" to phone
        )

        db.runBatch { batch ->
            val adminRef = db.collection("admin").document(uid)
            val customerRef = db.collection("Customer").document(uid)

            batch.set(adminRef, adminData)
            batch.set(customerRef, customerPatch, SetOptions.merge())
        }.addOnSuccessListener {
            Toast.makeText(this, "Admin saved", Toast.LENGTH_SHORT).show()
            loadAdminList()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun removeAdmin() {
        val uid = foundUid
        if (uid == null) {
            Toast.makeText(this, "Find an admin first", Toast.LENGTH_SHORT).show()
            return
        }

        db.runBatch { batch ->
            val adminRef = db.collection("admin").document(uid)
            val customerRef = db.collection("Customer").document(uid)

            batch.delete(adminRef)
            batch.set(customerRef, mapOf("role" to "customer"), SetOptions.merge())
        }.addOnSuccessListener {
            Toast.makeText(this, "Admin removed", Toast.LENGTH_SHORT).show()
            loadAdminList()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Remove failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadAdminList() {
        layoutAdminList.removeAllViews()

        db.collection("admin").get()
            .addOnSuccessListener { snap ->
                val inflater = LayoutInflater.from(this)

                if (snap.isEmpty) {
                    val tv = TextView(this)
                    tv.text = "No admins yet"
                    layoutAdminList.addView(tv)
                    return@addOnSuccessListener
                }

                for (doc in snap.documents) {
                    val row = inflater.inflate(R.layout.row_admin_user, layoutAdminList, false)

                    val name = listOf(
                        doc.getString("firstName") ?: "",
                        doc.getString("lastName") ?: ""
                    ).filter { it.isNotBlank() }.joinToString(" ")

                    val email = doc.getString("email") ?: ""
                    val phone = doc.getString("phone") ?: ""
                    val uid = doc.id

                    row.findViewById<TextView>(R.id.tvAdminName).text =
                        if (name.isNotBlank()) name else "(no name)"

                    val contactParts = mutableListOf<String>()
                    if (email.isNotBlank()) contactParts.add(email)
                    if (phone.isNotBlank()) contactParts.add(phone)
                    row.findViewById<TextView>(R.id.tvAdminContact).text =
                        if (contactParts.isNotEmpty()) contactParts.joinToString(" · ")
                        else "-"

                    row.findViewById<TextView>(R.id.tvAdminUid).text = uid

                    layoutAdminList.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load admins: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
