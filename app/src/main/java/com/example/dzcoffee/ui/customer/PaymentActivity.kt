package com.example.dzcoffee.ui.customer

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.data.datasource.CartManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val btnBack = findViewById<Button>(R.id.btnBackPayment)
        val btnPay = findViewById<Button>(R.id.btnPay)
        val spPaymentMethod = findViewById<Spinner>(R.id.spPaymentMethod)

        val methods = listOf("Pay in cafe", "Card on pickup")
        spPaymentMethod.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, methods)

        btnBack.setOnClickListener { finish() }

        btnPay.setOnClickListener {
            val method = spPaymentMethod.selectedItem?.toString() ?: methods[0]
            placeOrderWithPayment(method)
        }
    }

    private fun placeOrderWithPayment(paymentMethod: String) {
        val items = CartManager.getItems()
        if (items.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val total = CartManager.getTotal()

        val itemsData = items.map { item ->
            mapOf(
                "productId" to item.id,
                "name" to item.name,
                "quantity" to item.quantity,
                "unitPrice" to item.unitPrice,
                "size" to item.size,
                "milk" to item.milk,
                "sugar" to item.sugar,
                "isCoffee" to item.isCoffee,
                "category" to item.category
            )
        }

        val orderData = hashMapOf(
            "userId" to uid,
            "status" to "Pending",
            "total" to String.format(Locale.UK, "%.2f", total),
            "createdAt" to System.currentTimeMillis(),
            "paymentMethod" to paymentMethod,
            "items" to itemsData
        )

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                CartManager.clear()
                Toast.makeText(this, "Order placed", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error placing order", Toast.LENGTH_SHORT).show()
            }
    }
}
