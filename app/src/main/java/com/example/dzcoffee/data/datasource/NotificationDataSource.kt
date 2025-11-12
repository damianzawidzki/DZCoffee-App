package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.NotificationItem

// Data source for notifications
interface NotificationDataSource {
    suspend fun getNotificationsForUser(userId: String): List<NotificationItem>
    suspend fun markAllAsRead(userId: String)
}
