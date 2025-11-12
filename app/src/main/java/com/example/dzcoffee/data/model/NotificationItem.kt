package com.example.dzcoffee.data.model

// Notifications sent to users
data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val timestamp: Long = 0
)
