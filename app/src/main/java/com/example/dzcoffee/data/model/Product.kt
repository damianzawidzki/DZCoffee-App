package com.example.dzcoffee.data.model

// Simple product model used across the app
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: Double,
    val category: String,
    val imageRes: Int,
    val isCoffee: Boolean
)
