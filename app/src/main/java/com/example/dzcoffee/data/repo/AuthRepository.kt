package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.AuthDataSource
import com.example.dzcoffee.data.datasource.UserDataSource
import com.example.dzcoffee.data.model.User

// Repository for auth and basic user registration
class AuthRepository(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    // Login by email + password, then fetch User profile
    suspend fun login(email: String, password: String): User? {
        val uid = authDataSource.login(email, password)
        if (uid.isBlank()) return null
        return userDataSource.getUser(uid)
    }

    // Register new user (customer/admin) and save profile in Firestore
    suspend fun registerUser(email: String, password: String, profile: User): User? {
        val uid = authDataSource.register(email, password)
        if (uid.isBlank()) return null
        val userWithId = profile.copy(uid = uid)
        userDataSource.saveUser(userWithId)
        return userWithId
    }

    // Logout current user
    suspend fun logout() {
        authDataSource.logout()
    }
}
