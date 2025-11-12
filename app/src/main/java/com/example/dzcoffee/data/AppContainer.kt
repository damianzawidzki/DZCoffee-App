package com.example.dzcoffee.data

import com.example.dzcoffee.data.datasource.*
import com.example.dzcoffee.data.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Simple app container for repositories
class AppContainer {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // Data sources
    private val authDataSource by lazy { AuthFirebaseDataSource(auth) }
    private val userDataSource by lazy { UserFirebaseDataSource(db) }
    private val orderDataSource by lazy { OrderFirebaseDataSource(db) }
    private val menuLocalDataSource by lazy { MenuLocalDataSource() }
    private val feedbackDataSource by lazy { FeedbackFirebaseDataSource(db) }
    private val notificationDataSource by lazy { NotificationFirebaseDataSource(db) }
    private val usernameDataSource by lazy { UsernameFirebaseDataSource(db) }

    // Repositories
    val authRepository by lazy { AuthRepository(authDataSource, userDataSource) }
    val userRepository by lazy { UserRepository(userDataSource) }
    val menuRepository by lazy { MenuRepository(menuLocalDataSource) }
    val orderRepository by lazy { OrderRepository(orderDataSource) }
    val feedbackRepository by lazy { FeedbackRepository(feedbackDataSource) }
    val notificationRepository by lazy { NotificationRepository(notificationDataSource) }
    val usernameRepository by lazy { UsernameRepository(usernameDataSource) }
}
