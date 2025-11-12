package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.OrderDataSource
import com.example.dzcoffee.data.model.CartItem
import com.example.dzcoffee.data.model.Order

// Repository for orders
class OrderRepository(
    private val orderDataSource: OrderDataSource
) {

    // Create new order from cart items
    suspend fun placeOrder(userId: String, cartItems: List<CartItem>): String {
        val total = cartItems.sumOf { it.totalPrice }
        val order = Order(
            id = "",            // will be set in datasource
            userId = userId,
            items = cartItems,
            totalPrice = total,
            status = "Pending"
        )
        return orderDataSource.createOrder(order)
    }

    // Orders for current customer
    suspend fun getOrdersForUser(userId: String): List<Order> {
        return orderDataSource.getOrdersForUser(userId)
    }

    // All orders (admin view)
    suspend fun getAllOrders(): List<Order> {
        return orderDataSource.getAllOrders()
    }

    // Update status (admin or cancel by user)
    suspend fun updateOrderStatus(orderId: String, status: String) {
        orderDataSource.updateStatus(orderId, status)
    }

    fun setCompleted(id: Any) {}
}
