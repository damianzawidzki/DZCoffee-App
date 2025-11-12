package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.User

interface UserDataSource {
    suspend fun saveUser(user: User)
    suspend fun getUser(uid: String): User?
    suspend fun getAllAdmins(): List<User>
    suspend fun deleteUser(uid: String)
}
