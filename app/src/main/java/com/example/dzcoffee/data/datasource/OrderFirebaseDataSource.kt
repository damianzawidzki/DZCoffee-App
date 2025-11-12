package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderFirebaseDataSource(
    private val db: FirebaseFirestore
) : OrderDataSource {

    override suspend fun createOrder(order: Order): String {
        val ref = db.collection("orders").document()
        val id = ref.id
        ref.set(order.copy(id = id)).await()
        return id
    }

    override suspend fun getOrdersForUser(uid: String): List<Order> {
        return db.collection("orders")
            .whereEqualTo("userId", uid)
            .get().await()
            .toObjects(Order::class.java)
    }

    override suspend fun getAllOrders(): List<Order> {
        return db.collection("orders")
            .get().await()
            .toObjects(Order::class.java)
    }

    override suspend fun updateStatus(orderId: String, status: String) {
        db.collection("orders").document(orderId)
            .update("status", status).await()
    }
}
