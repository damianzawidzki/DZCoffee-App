package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzcoffee.data.model.User
import com.example.dzcoffee.data.repository.AuthRepository
import com.example.dzcoffee.data.repository.UserRepository
import com.example.dzcoffee.data.repository.UsernameRepository
import kotlinx.coroutines.launch

// ViewModel for SuperAdmin panel (manage admins)
class SuperAdminViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val usernameRepository: UsernameRepository
) : ViewModel() {

    private val _adminsState = MutableLiveData<ResultState<List<User>>>()
    val adminsState: LiveData<ResultState<List<User>>> = _adminsState

    private val _actionState = MutableLiveData<ResultState<String>>()
    val actionState: LiveData<ResultState<String>> = _actionState

    private var editingAdminUid: String? = null
    private var editingUsername: String? = null

    // Load all admins
    fun loadAdmins() {
        _adminsState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val admins = userRepository.getAdmins()
                _adminsState.value = ResultState.Success(admins)
            } catch (e: Exception) {
                _adminsState.value = ResultState.Error(e.message)
            }
        }
    }

    // Use this when user taps on admin row -> to fill EditTexts
    fun startEditing(admin: User, username: String?) {
        editingAdminUid = admin.uid
        editingUsername = username
    }

    // Add new admin (create auth + profile + username mapping)
    fun addAdmin(
        firstName: String,
        lastName: String,
        dob: String,
        phone: String,
        email: String,
        login: String,
        password: String
    ) {
        _actionState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                // Check username first
                if (usernameRepository.isTaken(login)) {
                    _actionState.value = ResultState.Error("Username already taken")
                    return@launch
                }

                val profile = User(
                    uid = "",
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    login = login,
                    dob = dob,
                    phone = phone,
                    role = "admin"
                )

                // Create auth user + profile
                val created = authRepository.registerUser(email, password, profile)
                if (created == null || created.uid.isBlank()) {
                    _actionState.value = ResultState.Error("Failed to create admin account")
                    return@launch
                }

                // Save username mapping
                usernameRepository.save(login, email, "admin", created.uid)

                _actionState.value = ResultState.Success("Admin added")
                loadAdmins()
            } catch (e: Exception) {
                _actionState.value = ResultState.Error(e.message)
            }
        }
    }

    // Update existing admin profile (without changing Auth password here)
    fun editAdmin(
        firstName: String,
        lastName: String,
        dob: String,
        phone: String,
        email: String,
        login: String
    ) {
        val uid = editingAdminUid
        if (uid.isNullOrBlank()) {
            _actionState.value = ResultState.Error("No admin selected")
            return
        }

        _actionState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                // Optional: handle username change
                val oldUsername = editingUsername
                if (!oldUsername.isNullOrBlank() && oldUsername != login) {
                    // check new username
                    if (usernameRepository.isTaken(login)) {
                        _actionState.value = ResultState.Error("New username already taken")
                        return@launch
                    }
                    usernameRepository.delete(oldUsername)
                    usernameRepository.save(login, email, "admin", uid)
                }

                val updated = User(
                    uid = uid,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    login = login,
                    dob = dob,
                    phone = phone,
                    role = "admin"
                )
                userRepository.saveUser(updated)

                _actionState.value = ResultState.Success("Admin updated")
                loadAdmins()
            } catch (e: Exception) {
                _actionState.value = ResultState.Error(e.message)
            }
        }
    }

    // Delete admin (profile + username mapping) - does not remove from FirebaseAuth for simplicity
    fun deleteAdmin(username: String, uid: String) {
        _actionState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                usernameRepository.delete(username)
                userRepository.deleteUser(uid)
                _actionState.value = ResultState.Success("Admin deleted")
                loadAdmins()
            } catch (e: Exception) {
                _actionState.value = ResultState.Error(e.message)
            }
        }
    }

    fun resetActionState() {
        _actionState.value = ResultState.Idle
    }
}
