package com.example.dzcoffee.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Firebase implementation for username mapping
class UsernameFirebaseDataSource(
    private val db: FirebaseFirestore
) : UsernameDataSource {

    override suspend fun isUsernameTaken(username: String): Boolean {
        val doc = db.collection("usernames").document(username).get().await()
        return doc.exists()
    }

    override suspend fun saveUsername(username: String, email: String, role: String, uid: String) {
        val data = hashMapOf(
            "email" to email,
            "role" to role,
            "uID" to uid
        )
        db.collection("usernames").document(username).set(data).await()
    }

    override suspend fun deleteUsername(username: String) {
        db.collection("usernames").document(username).delete().await()
    }
}
