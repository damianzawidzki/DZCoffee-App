package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.UserDataSource
import com.example.dzcoffee.data.model.User

// Repository for user operations
class UserRepository(
    private val userDataSource: UserDataSource
) {

    // Save or update user profile
    suspend fun saveUser(user: User) {
        userDataSource.saveUser(user)
    }

    // Get single user
    suspend fun getUser(uid: String): User? {
        return userDataSource.getUser(uid)
    }

    // List admins (for superadmin)
    suspend fun getAdmins(): List<User> {
        return userDataSource.getAllAdmins()
    }

    // Delete user (for superadmin)
    suspend fun deleteUser(uid: String) {
        userDataSource.deleteUser(uid)
    }
}
