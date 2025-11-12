package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzcoffee.data.model.User
import com.example.dzcoffee.data.repository.AuthRepository
import kotlinx.coroutines.launch

// ViewModel for login and registration
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<ResultState<User?>>()
    val loginState: LiveData<ResultState<User?>> = _loginState

    private val _registerState = MutableLiveData<ResultState<User?>>()
    val registerState: LiveData<ResultState<User?>> = _registerState

    // Login with email + password
    fun login(email: String, password: String) {
        _loginState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val user = authRepository.login(email, password)
                if (user != null) {
                    _loginState.value = ResultState.Success(user)
                } else {
                    _loginState.value = ResultState.Error("User not found")
                }
            } catch (e: Exception) {
                _loginState.value = ResultState.Error(e.message)
            }
        }
    }

    // Register new customer
    fun registerCustomer(user: User, password: String) {
        val profile = user.copy(role = "customer")
        doRegister(profile, password)
    }

    // Register new admin
    fun registerAdmin(user: User, password: String) {
        val profile = user.copy(role = "admin")
        doRegister(profile, password)
    }

    // Common helper for registration
    private fun doRegister(profile: User, password: String) {
        _registerState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val created = authRepository.registerUser(profile.email, password, profile)
                if (created != null) {
                    _registerState.value = ResultState.Success(created)
                } else {
                    _registerState.value = ResultState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _registerState.value = ResultState.Error(e.message)
            }
        }
    }

    // Reset states when screen closed
    fun resetStates() {
        _loginState.value = ResultState.Idle
        _registerState.value = ResultState.Idle
    }
}
