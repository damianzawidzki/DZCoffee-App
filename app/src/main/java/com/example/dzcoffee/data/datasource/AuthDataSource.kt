package com.example.dzcoffee.data.datasource

interface AuthDataSource {
    suspend fun login(email: String, password: String): String
    suspend fun register(email: String, password: String): String
    suspend fun logout()
}
