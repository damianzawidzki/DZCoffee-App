package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Firebase implementation for notifications
class NotificationFirebaseDataSource(
    private val db: FirebaseFirestore
) : NotificationDataSource {

    override suspend fun getNotificationsForUser(userId: String): List<NotificationItem> {
        return db.collection("notifications")
            .whereEqualTo("userId", userId)
            .get().await()
            .toObjects(NotificationItem::class.java)
    }

    override suspend fun markAllAsRead(userId: String) {
        val snap = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .get().await()

        val batch = db.batch()
        snap.documents.forEach { doc ->
            batch.update(doc.reference, "read", true)
        }
        batch.commit().await()
    }
}
