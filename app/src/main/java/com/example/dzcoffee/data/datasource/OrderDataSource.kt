package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.Order

interface OrderDataSource {
    suspend fun createOrder(order: Order): String
    suspend fun getOrdersForUser(uid: String): List<Order>
    suspend fun getAllOrders(): List<Order>
    suspend fun updateStatus(orderId: String, status: String)
}
