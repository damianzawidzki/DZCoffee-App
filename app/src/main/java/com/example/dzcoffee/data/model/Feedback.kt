package com.example.dzcoffee.data.model

data class Feedback(
    val id: String = "",
    val orderId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
