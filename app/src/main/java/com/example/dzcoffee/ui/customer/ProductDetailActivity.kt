package com.example.dzcoffee.ui.customer

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dzcoffee.R
import com.example.dzcoffee.data.datasource.CartManager
import com.example.dzcoffee.data.model.CartItem
import com.google.android.material.button.MaterialButton

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val img = findViewById<ImageView>(R.id.imgProduct)
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvDesc = findViewById<TextView>(R.id.tvIngredients)

        val layoutSize = findViewById<View>(R.id.layoutSize)
        val layoutMilk = findViewById<View>(R.id.layoutMilk)
        val layoutSugar = findViewById<View>(R.id.layoutSugar)

        val rgSize = findViewById<RadioGroup>(R.id.rgSize)
        val rbSmall = findViewById<RadioButton>(R.id.rbSmall)
        val rbMedium = findViewById<RadioButton>(R.id.rbMedium)
        val rbLarge = findViewById<RadioButton>(R.id.rbLarge)

        val spMilk = findViewById<Spinner>(R.id.spMilk)
        val edtSugar = findViewById<EditText>(R.id.edtSugar)
        val edtQty = findViewById<EditText>(R.id.edtQuantity)

        val btnAdd = findViewById<MaterialButton>(R.id.btnAddToCart)
        val btnBack = findViewById<MaterialButton>(R.id.btnBack)

        // received
        val id = intent.getStringExtra("id") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val basePrice = intent.getDoubleExtra("price", 0.0)
        val imageRes = intent.getIntExtra("imageRes", R.drawable.americano)
        val isCoffee = intent.getBooleanExtra("isCoffee", false)

        img.setImageResource(imageRes)
        tvTitle.text = name
        tvDesc.text = description

        val milkList = listOf("Regular milk", "Oat milk", "Soy milk", "Almond milk", "No milk")
        spMilk.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, milkList)

        if (!isCoffee) {
            layoutSize.visibility = View.GONE
            layoutMilk.visibility = View.GONE
            layoutSugar.visibility = View.GONE
        } else {
            rbMedium.isChecked = true
        }

        btnBack.setOnClickListener { finish() }

        btnAdd.setOnClickListener {

            val qty = edtQty.text.toString().toIntOrNull() ?: 1
            val sugar = edtSugar.text.toString().toIntOrNull() ?: 0
            val milk = spMilk.selectedItem.toString()

            var size = "Medium"
            var finalPrice = basePrice

            if (isCoffee) {
                when (rgSize.checkedRadioButtonId) {
                    R.id.rbSmall -> { size = "Small"; finalPrice = basePrice - 0.20 }
                    R.id.rbMedium -> size = "Medium"
                    R.id.rbLarge -> { size = "Large"; finalPrice = basePrice + 0.50 }
                }
            }

            val item = CartItem(
                id = id,
                name = name,
                description = description,
                category = category,
                imageRes = imageRes,
                basePrice = basePrice,
                isCoffee = isCoffee,
                size = if (isCoffee) size else "",
                milk = if (isCoffee) milk else "",
                sugar = if (isCoffee) sugar else 0,
                quantity = qty,
                unitPrice = finalPrice
            )

            CartManager.addItem(item)
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
