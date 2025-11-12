package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFirebaseDataSource(
    private val db: FirebaseFirestore
) : UserDataSource {

    override suspend fun saveUser(user: User) {
        db.collection("users").document(user.uid).set(user).await()
    }

    override suspend fun getUser(uid: String): User? {
        return db.collection("users").document(uid)
            .get().await().toObject(User::class.java)
    }

    override suspend fun getAllAdmins(): List<User> {
        return db.collection("users")
            .whereEqualTo("role", "admin")
            .get().await()
            .toObjects(User::class.java)
    }

    override suspend fun deleteUser(uid: String) {
        db.collection("users").document(uid).delete().await()
    }
}
