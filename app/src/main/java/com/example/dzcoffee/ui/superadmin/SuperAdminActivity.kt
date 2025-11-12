package com.example.dzcoffee.ui.superadmin

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dzcoffee.R
import com.example.dzcoffee.data.AppContainer
import com.example.dzcoffee.data.model.User
import com.example.dzcoffee.viewmodel.ResultState
import com.example.dzcoffee.viewmodel.SimpleViewModelFactory
import com.example.dzcoffee.viewmodel.SuperAdminViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

// SuperAdmin panel: manage admin users
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
    private lateinit var btnRefreshAdmins: MaterialButton
    private lateinit var btnSignOut: MaterialButton

    private lateinit var listAdmins: LinearLayout

    private lateinit var viewModel: SuperAdminViewModel

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var selectedAdmin: User? = null
    private var selectedUsername: String? = null   // można trzymać osobno, jeśli chcesz kiedyś wyciągać z Firestore

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
        btnRefreshAdmins = findViewById(R.id.btnRefreshAdmins)
        btnSignOut = findViewById(R.id.btnSignOut)
        listAdmins = findViewById(R.id.listAdmins)

        val container = AppContainer()
        viewModel = ViewModelProvider(
            this,
            SimpleViewModelFactory {
                SuperAdminViewModel(
                    container.authRepository,
                    container.userRepository,
                    container.usernameRepository
                )
            }
        )[SuperAdminViewModel::class.java]

        btnAddAdmin.setOnClickListener { handleAddAdmin() }
        btnEditAdmin.setOnClickListener { handleEditAdmin() }
        btnDeleteAdmin.setOnClickListener { handleDeleteAdmin() }
        btnRefreshAdmins.setOnClickListener { viewModel.loadAdmins() }
        btnSignOut.setOnClickListener {
            auth.signOut()
            finish()
        }

        viewModel.adminsState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> showAdminsLoading()
                is ResultState.Success -> renderAdmins(state.data)
                is ResultState.Error -> showAdminsError(state.message)
                else -> {}
            }
        }

        viewModel.actionState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {}
                is ResultState.Success -> Toast.makeText(this, state.data, Toast.LENGTH_SHORT).show()
                is ResultState.Error -> Toast.makeText(this, state.message ?: "Action failed", Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }

        viewModel.loadAdmins()
    }

    private fun handleAddAdmin() {
        val fName = etFirstName.text.toString().trim()
        val lName = etLastName.text.toString().trim()
        val birth = etBirthDate.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val login = etLogin.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || login.isEmpty()) {
            Toast.makeText(this, "Email, login and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.addAdmin(fName, lName, birth, phone, email, login, password)
    }

    private fun handleEditAdmin() {
        val admin = selectedAdmin
        if (admin == null) {
            Toast.makeText(this, "Select admin first", Toast.LENGTH_SHORT).show()
            return
        }

        val fName = etFirstName.text.toString().trim()
        val lName = etLastName.text.toString().trim()
        val birth = etBirthDate.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val login = etLogin.text.toString().trim()
        val email = etEmail.text.toString().trim()

        viewModel.startEditing(admin, selectedUsername)
        viewModel.editAdmin(fName, lName, birth, phone, email, login)
    }

    private fun handleDeleteAdmin() {
        val admin = selectedAdmin
        val username = selectedUsername
        if (admin == null || username.isNullOrBlank()) {
            Toast.makeText(this, "Select admin first", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.deleteAdmin(username, admin.uid)
    }

    private fun showAdminsLoading() {
        listAdmins.removeAllViews()
        val tv = TextView(this).apply {
            text = "Loading admins..."
            setTextColor(resources.getColor(android.R.color.black, theme))
            setPadding(16, 16, 16, 16)
        }
        listAdmins.addView(tv)
    }

    private fun showAdminsError(message: String?) {
        listAdmins.removeAllViews()
        val tv = TextView(this).apply {
            text = message ?: "Error loading admins"
            setTextColor(resources.getColor(android.R.color.black, theme))
            setPadding(16, 16, 16, 16)
        }
        listAdmins.addView(tv)
    }

    private fun renderAdmins(admins: List<User>) {
        listAdmins.removeAllViews()

        if (admins.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No admins"
                setTextColor(resources.getColor(android.R.color.black, theme))
                setPadding(16, 16, 16, 16)
            }
            listAdmins.addView(tv)
            return
        }

        val inflater = layoutInflater

        for (admin in admins) {
            val row = inflater.inflate(R.layout.row_admin_user, listAdmins, false)

            val tvName = row.findViewById<TextView>(R.id.tvAdminName)
            val tvContact = row.findViewById<TextView>(R.id.tvAdminContact)
            val tvUid = row.findViewById<TextView>(R.id.tvAdminUid)

            tvName.text = "${admin.firstName} ${admin.lastName}"
            tvContact.text = "${admin.email} · ${admin.phone}"
            tvUid.text = admin.uid

            row.setOnClickListener {
                // select admin and fill form
                selectedAdmin = admin
                selectedUsername = admin.login

                etFirstName.setText(admin.firstName)
                etLastName.setText(admin.lastName)
                etBirthDate.setText(admin.dob)
                etPhone.setText(admin.phone)
                etLogin.setText(admin.login)
                etEmail.setText(admin.email)
                etPassword.setText("")
            }

            listAdmins.addView(row)
        }
    }
}
