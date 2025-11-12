package com.example.dzcoffee.data.datasource

// Data source for username -> email mapping
interface UsernameDataSource {
    suspend fun isUsernameTaken(username: String): Boolean
    suspend fun saveUsername(username: String, email: String, role: String, uid: String)
    suspend fun deleteUsername(username: String)
}
