package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.Feedback

// Data source for feedback operations
interface FeedbackDataSource {
    suspend fun addFeedback(feedback: Feedback)
    suspend fun getFeedbackForOrder(orderId: String): Feedback?
}
