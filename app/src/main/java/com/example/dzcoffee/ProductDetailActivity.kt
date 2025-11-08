package com.example.dzcoffee

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProductDetailActivity : AppCompatActivity() {

    private var isCoffee = false
    private var fromPrice = 0.0
    private lateinit var productId: String
    private lateinit var productName: String
    private var imageRes: Int = 0
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productId = intent.getStringExtra("id") ?: ""
        productName = intent.getStringExtra("name") ?: ""
        fromPrice = intent.getDoubleExtra("fromPrice", 0.0)
        imageRes = intent.getIntExtra("imageRes", 0)
        category = intent.getStringExtra("category") ?: "Other"
        isCoffee = intent.getBooleanExtra("isCoffee", false)

        val img = findViewById<ImageView>(R.id.imgProduct)
        val tvName = findViewById<TextView>(R.id.tvProductName)
        val layoutSizes = findViewById<LinearLayout>(R.id.layoutSizes)
        val rgSizes = findViewById<RadioGroup>(R.id.rgSizes)
        val rbSmall = findViewById<RadioButton>(R.id.rbSmall)
        val rbMedium = findViewById<RadioButton>(R.id.rbMedium)
        val rbLarge = findViewById<RadioButton>(R.id.rbLarge)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val btnAdd = findViewById<MaterialButton>(R.id.btnAddToCart)

        img.setImageResource(imageRes)
        tvName.text = productName

        if (isCoffee) {
            // Example prices: S = fromPrice, M = +0.40, L = +0.80
            val priceSmall = fromPrice
            val priceMedium = fromPrice + 0.40
            val priceLarge = fromPrice + 0.80

            rbSmall.text = "Small - £${"%.2f".format(priceSmall)}"
            rbMedium.text = "Medium - £${"%.2f".format(priceMedium)}"
            rbLarge.text = "Large - £${"%.2f".format(priceLarge)}"

            rbSmall.isChecked = true
            tvPrice.text = "£${"%.2f".format(priceSmall)}"

            rgSizes.setOnCheckedChangeListener { _, checkedId ->
                val price = when (checkedId) {
                    R.id.rbSmall -> priceSmall
                    R.id.rbMedium -> priceMedium
                    R.id.rbLarge -> priceLarge
                    else -> priceSmall
                }
                tvPrice.text = "£${"%.2f".format(price)}"
            }

            btnAdd.setOnClickListener {
                val (sizeLabel, price) = when (rgSizes.checkedRadioButtonId) {
                    R.id.rbMedium -> "M" to priceMedium
                    R.id.rbLarge -> "L" to priceLarge
                    else -> "S" to priceSmall
                }

                val finalId = "${productId}_$sizeLabel"
                val finalName = "$productName ($sizeLabel)"

                CartManager.addItem(
                    Product(
                        id = finalId,
                        name = finalName,
                        category = category,
                        price = price
                    )
                )

                Toast.makeText(this, "$finalName added to cart", Toast.LENGTH_SHORT).show()
                finish()
            }

        } else {
            // Not coffee: hide sizes, use single price
            layoutSizes.visibility = android.view.View.GONE
            tvPrice.text = "£${"%.2f".format(fromPrice)}"

            btnAdd.setOnClickListener {
                CartManager.addItem(
                    Product(
                        id = productId,
                        name = productName,
                        category = category,
                        price = fromPrice
                    )
                )
                Toast.makeText(this, "$productName added to cart", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
