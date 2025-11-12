package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.FeedbackDataSource
import com.example.dzcoffee.data.model.Feedback

// Repository for feedback
class FeedbackRepository(
    private val feedbackDataSource: FeedbackDataSource
) {

    // Save feedback for order
    suspend fun sendFeedback(feedback: Feedback) {
        feedbackDataSource.addFeedback(feedback)
    }

    // Load feedback for one order
    suspend fun getFeedback(orderId: String): Feedback? {
        return feedbackDataSource.getFeedbackForOrder(orderId)
    }
}
