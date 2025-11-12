package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.Feedback
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Firebase implementation for feedback
class FeedbackFirebaseDataSource(
    private val db: FirebaseFirestore
) : FeedbackDataSource {

    override suspend fun addFeedback(feedback: Feedback) {
        val ref = db.collection("feedback").document()
        val id = ref.id
        ref.set(feedback.copy(id = id)).await()
    }

    override suspend fun getFeedbackForOrder(orderId: String): Feedback? {
        val snap = db.collection("feedback")
            .whereEqualTo("orderId", orderId)
            .limit(1)
            .get().await()

        return snap.documents.firstOrNull()?.toObject(Feedback::class.java)
    }
}
