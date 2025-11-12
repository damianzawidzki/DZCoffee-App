package com.example.dzcoffee.data.model

// Data class for cart item
data class CartItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val imageRes: Int = 0,
    val basePrice: Double = 0.0,
    val isCoffee: Boolean = false,
    val size: String = "",
    val milk: String = "",
    val sugar: Int = 0,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0
) {
    // Total for this line in cart
    val totalPrice: Double
        get() = unitPrice * quantity
}
