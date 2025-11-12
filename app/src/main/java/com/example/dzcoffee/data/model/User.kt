package com.example.dzcoffee.data.model

data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val login: String = "",
    val dob: String = "",
    val phone: String = "",
    val role: String = "customer"   // customer / admin / superadmin
)
