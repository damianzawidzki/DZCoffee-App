package com.example.dzcoffee.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dzcoffee.R
import com.example.dzcoffee.data.model.Product
import com.example.dzcoffee.ui.auth.LoginActivity
import com.example.dzcoffee.ui.customer.cart.CartActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProductsActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        viewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        val layoutProducts = findViewById<LinearLayout>(R.id.layoutProducts)
        val btnCart = findViewById<MaterialButton>(R.id.btnCart)
        val btnMyOrders = findViewById<MaterialButton>(R.id.btnMyOrders)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        btnMyOrders.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModel.products.observe(this) { products ->
            buildMenuUI(layoutProducts, products)
        }
    }

    private fun buildMenuUI(container: LinearLayout, products: List<Product>) {
        container.removeAllViews()
        val byCategory = products.groupBy { it.category }

        byCategory.forEach { (category, items) ->
            val header = layoutInflater.inflate(R.layout.row_category_header, container, false)
            header.findViewById<TextView>(R.id.tvCategoryTitle).text = category
            container.addView(header)

            items.forEach { product ->
                val row = layoutInflater.inflate(R.layout.row_product_item, container, false)

                row.findViewById<TextView>(R.id.tvItemName).text = product.name
                row.findViewById<TextView>(R.id.tvItemInfo).text = product.description
                row.findViewById<TextView>(R.id.tvItemPrice).text =
                    String.format("from £%.2f", product.basePrice)
                row.findViewById<ImageView>(R.id.imgItem).setImageResource(product.imageRes)

                fun openDetail() {
                    val i = Intent(this, ProductDetailActivity::class.java).apply {
                        putExtra("id", product.id)
                        putExtra("name", product.name)
                        putExtra("description", product.description)
                        putExtra("price", product.basePrice)
                        putExtra("category", product.category)
                        putExtra("imageRes", product.imageRes)
                        putExtra("isCoffee", product.isCoffee)
                    }
                    startActivity(i)
                }

                row.setOnClickListener { openDetail() }
                row.findViewById<TextView>(R.id.tvChoose).setOnClickListener { openDetail() }

                container.addView(row)
            }
        }
    }
}

