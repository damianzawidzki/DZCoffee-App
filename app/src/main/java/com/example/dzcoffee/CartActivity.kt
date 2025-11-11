package com.example.dzcoffee

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var layoutCartItems: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnBackCart: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var btnPlaceOrder: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        layoutCartItems = findViewById(R.id.layoutCartItems)
        tvTotal = findViewById(R.id.tvTotal)
        btnBackCart = findViewById(R.id.btnBackCart)
        btnClear = findViewById(R.id.btnClear)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        btnBackCart.setOnClickListener { finish() }

        btnClear.setOnClickListener {
            if (CartManager.isEmpty) {
                Toast.makeText(this, "Cart is already empty", Toast.LENGTH_SHORT).show()
            } else {
                CartManager.clear()
                refreshCart()
                Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }

        refreshCart()
    }

    private fun placeOrder() {
        if (CartManager.isEmpty) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show()
            return
        }

        val items = CartManager.getItems()
        val total = CartManager.getTotal()

        // przygotuj listę pozycji w zamówieniu
        val itemsData = items.map { item ->
            mapOf(
                "name" to item.name,
                "category" to item.category,
                "size" to item.size,
                "milk" to item.milk,
                "sugar" to item.sugar,
                "unitPrice" to item.unitPrice,
                "quantity" to item.quantity,
                "lineTotal" to item.totalPrice
            )
        }

        val orderData = hashMapOf(
            "userId" to user.uid,
            "userEmail" to (user.email ?: ""),
            "createdAt" to FieldValue.serverTimestamp(),
            "total" to total,
            "status" to "New",                 // domyślny status
            "items" to itemsData
        )

        setButtonsEnabled(false)

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed", Toast.LENGTH_LONG).show()
                CartManager.clear()
                refreshCart()
                setButtonsEnabled(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error placing order: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                setButtonsEnabled(true)
            }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        btnPlaceOrder.isEnabled = enabled
        btnClear.isEnabled = enabled
        btnPlaceOrder.alpha = if (enabled) 1f else 0.6f
        btnClear.alpha = if (enabled) 1f else 0.6f
    }

    private fun refreshCart() {
        val items = CartManager.getItems()
        layoutCartItems.removeAllViews()

        val inflater = LayoutInflater.from(this)

        items.forEachIndexed { index, item ->
            val row = inflater.inflate(R.layout.row_cart_item, layoutCartItems, false)

            val imgItem = row.findViewById<android.widget.ImageView>(R.id.imgItem)
            val tvName = row.findViewById<TextView>(R.id.tvName)
            val tvOptions = row.findViewById<TextView>(R.id.tvOptions)
            val tvQtyPrice = row.findViewById<TextView>(R.id.tvQtyPrice)
            val btnRemove = row.findViewById<MaterialButton>(R.id.btnRemove)

            imgItem.setImageResource(item.imageResId)
            tvName.text = item.name

            val optionsText = when (item.category.lowercase(Locale.getDefault())) {
                "coffee" -> {
                    val parts = mutableListOf<String>()
                    if (item.size.isNotBlank()) parts.add(item.size)
                    if (item.milk.isNotBlank()) parts.add(item.milk)
                    if (item.sugar.isNotBlank()) parts.add("Sugar: ${item.sugar}")
                    parts.joinToString(" • ")
                }
                else -> "Qty: ${item.quantity}"
            }
            tvOptions.text = optionsText

            tvQtyPrice.text = String.format(Locale.UK, "x%d  •  £%.2f", item.quantity, item.totalPrice)

            btnRemove.setOnClickListener {
                CartManager.removeItemAt(index)
                refreshCart()
            }

            layoutCartItems.addView(row)
        }

        val total = CartManager.getTotal()
        tvTotal.text = String.format(Locale.UK, "Total: £%.2f", total)
    }
}
