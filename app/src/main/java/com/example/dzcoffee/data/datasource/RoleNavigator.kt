package com.example.dzcoffee.data.datasource

import android.app.Activity
import android.content.Intent
import com.example.dzcoffee.ui.admin.AdminActivity
import com.example.dzcoffee.ui.auth.LoginActivity
import com.example.dzcoffee.ui.customer.HomeActivity
import com.example.dzcoffee.ui.superadmin.SuperAdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object RoleNavigator {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun go(activity: Activity, uid: String?, email: String?) {
        // If no session -> go to Login
        if (uid == null && email == null) {
            start(activity, LoginActivity::class.java, clear = true)
            return
        }

        // 1) Try unified users/{uid}
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role")
                    if (!role.isNullOrBlank()) {
                        route(activity, role)
                    } else {
                        // 2) Try legacy Customer by email
                        tryCustomerByEmail(activity, email)
                    }
                }
                .addOnFailureListener {
                    tryCustomerByEmail(activity, email)
                }
            return
        }


        tryCustomerByEmail(activity, email)
    }



    private fun tryCustomerByEmail(activity: Activity, email: String?) {
        if (email.isNullOrBlank()) {
            route(activity, "customer")
            return
        }

        // 2) Customer where email == ...
        db.collection("Customer").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { snap ->
                if (!snap.isEmpty) {
                    val role = snap.documents[0].getString("role") ?: "customer"
                    route(activity, role)
                } else {
                    // 3) admin by email OR uid field
                    tryAdminCollections(activity, email)
                }
            }
            .addOnFailureListener {
                tryAdminCollections(activity, email)
            }
    }

    private fun tryAdminCollections(activity: Activity, email: String) {
        // admin
        db.collection("admin").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { a ->
                if (!a.isEmpty) {
                    route(activity, "admin"); return@addOnSuccessListener
                }
                // superadmins
                db.collection("superadmins").whereEqualTo("email", email).limit(1).get()
                    .addOnSuccessListener { s ->
                        if (!s.isEmpty) route(activity, "superadmin")
                        else route(activity, "customer")
                    }
                    .addOnFailureListener { route(activity, "customer") }
            }
            .addOnFailureListener { route(activity, "customer") }
    }

    private fun route(activity: Activity, role: String) {
        when (role.lowercase()) {
            "admin" -> start(activity, AdminActivity::class.java, clear = true)
            "superadmin" -> start(activity, SuperAdminActivity::class.java, clear = true)
            else -> start(activity, HomeActivity::class.java, clear = true) // customer default
        }
    }

    private fun start(activity: Activity, cls: Class<*>, clear: Boolean) {
        val i = Intent(activity, cls)
        if (clear) i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(i)
        activity.finish()
    }


    fun goCurrentUser(activity: Activity) {
        val u = FirebaseAuth.getInstance().currentUser
        go(activity, u?.uid, u?.email)
    }
}