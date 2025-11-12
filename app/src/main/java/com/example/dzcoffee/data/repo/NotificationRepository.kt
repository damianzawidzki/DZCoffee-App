package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.NotificationDataSource
import com.example.dzcoffee.data.model.NotificationItem

// Repository for notifications
class NotificationRepository(
    private val dataSource: NotificationDataSource
) {

    // Load notifications list
    suspend fun getNotifications(userId: String): List<NotificationItem> {
        return dataSource.getNotificationsForUser(userId)
    }

    // Mark all as read
    suspend fun markAllRead(userId: String) {
        dataSource.markAllAsRead(userId)
    }
}
