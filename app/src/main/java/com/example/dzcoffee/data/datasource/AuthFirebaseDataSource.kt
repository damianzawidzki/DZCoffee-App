package com.example.dzcoffee.data.datasource

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthFirebaseDataSource(
    private val auth: FirebaseAuth
) : AuthDataSource {

    override suspend fun login(email: String, password: String): String {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser?.uid ?: ""
    }

    override suspend fun register(email: String, password: String): String {
        auth.createUserWithEmailAndPassword(email, password).await()
        return auth.currentUser?.uid ?: ""
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
