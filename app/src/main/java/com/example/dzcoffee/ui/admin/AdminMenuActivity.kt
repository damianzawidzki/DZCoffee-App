package com.example.dzcoffee.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.data.datasource.MenuLocalDataSource
import com.example.dzcoffee.data.model.Product

class AdminMenuActivity : AppCompatActivity() {

    private val localSource = MenuLocalDataSource()
    private val items = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        val etName = findViewById<EditText>(R.id.etProductName)
        val etPrice = findViewById<EditText>(R.id.etProductPrice)
        val etDescription = findViewById<EditText>(R.id.etProductDescription)
        val chkCoffee = findViewById<CheckBox>(R.id.chkCoffee)
        val chkOptions = findViewById<CheckBox>(R.id.chkOptions)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnRefresh = findViewById<Button>(R.id.btnRefreshAdminMenu)
        val btnGoToOrders = findViewById<Button>(R.id.btnGoToOrders)

        items.clear()
        items.addAll(localSource.getMenu())

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val desc = etDescription.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter product name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                id = "admin_${items.size + 1}",
                name = name,
                description = desc,
                basePrice = price,
                category = if (chkCoffee.isChecked) "Coffee" else "Other",
                imageRes = R.drawable.americano,
                isCoffee = chkCoffee.isChecked
            )

            items.add(product)
            Toast.makeText(this, "Item added (local only)", Toast.LENGTH_SHORT).show()

            // Clear form
            etName.text = null
            etPrice.text = null
            etDescription.text = null
            chkCoffee.isChecked = false
            chkOptions.isChecked = false
        }

        btnRefresh.setOnClickListener {
            Toast.makeText(this, "Menu loaded: ${items.size} items", Toast.LENGTH_SHORT).show()
        }

        btnGoToOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
    }
}
