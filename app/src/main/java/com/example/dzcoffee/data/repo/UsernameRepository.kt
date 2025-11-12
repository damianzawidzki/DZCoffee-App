package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.UsernameDataSource

// Repository for username usage (login mapping)
class UsernameRepository(
    private val dataSource: UsernameDataSource
) {

    // Check if username already exists
    suspend fun isTaken(username: String): Boolean {
        return dataSource.isUsernameTaken(username)
    }

    // Save username mapping
    suspend fun save(username: String, email: String, role: String, uid: String) {
        dataSource.saveUsername(username, email, role, uid)
    }

    // Delete mapping
    suspend fun delete(username: String) {
        dataSource.deleteUsername(username)
    }
}
