package com.example.dzcoffee.ui.customer.cart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.data.datasource.CartManager
import com.example.dzcoffee.ui.customer.PaymentActivity
import com.google.android.material.button.MaterialButton

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val layout = findViewById<LinearLayout>(R.id.layoutCartItems)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        val btnBack = findViewById<MaterialButton>(R.id.btnBackCart)
        val btnClear = findViewById<MaterialButton>(R.id.btnClear)
        val btnCheckout = findViewById<MaterialButton>(R.id.btnPlaceOrder)

        btnBack.setOnClickListener { finish() }

        btnClear.setOnClickListener {
            CartManager.clear()
            loadItems(layout, tvTotal)
        }

        btnCheckout.setOnClickListener {
            if (CartManager.getItems().isNotEmpty()) {
                startActivity(Intent(this, PaymentActivity::class.java))
            }
        }

        loadItems(layout, tvTotal)
    }

    private fun loadItems(container: LinearLayout, tvTotal: TextView) {
        container.removeAllViews()

        for (item in CartManager.getItems()) {
            val row = layoutInflater.inflate(R.layout.row_cart_item, container, false)

            row.findViewById<ImageView>(R.id.imgItem).setImageResource(item.imageRes)
            row.findViewById<TextView>(R.id.tvName).text = item.name

            row.findViewById<TextView>(R.id.tvOptions).text =
                if (item.isCoffee)
                    "${item.size} • ${item.milk} • sugar ${item.sugar}"
                else
                    item.category

            row.findViewById<TextView>(R.id.tvQtyPrice).text =
                "x${item.quantity}  •  £${String.format("%.2f", item.unitPrice * item.quantity)}"

            row.findViewById<MaterialButton>(R.id.btnRemove).setOnClickListener {
                CartManager.remove(item)
                loadItems(container, tvTotal)
            }

            container.addView(row)
        }

        tvTotal.text = "Total: £${String.format("%.2f", CartManager.getTotal())}"
    }
}
