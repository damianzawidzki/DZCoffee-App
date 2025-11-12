package com.example.dzcoffee.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis()
)
