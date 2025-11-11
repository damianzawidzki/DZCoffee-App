package com.example.dzcoffee

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imgProduct: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvIngredients: TextView

    private lateinit var layoutSize: LinearLayout
    private lateinit var rgSize: RadioGroup
    private lateinit var rbSmall: RadioButton
    private lateinit var rbMedium: RadioButton
    private lateinit var rbLarge: RadioButton

    private lateinit var layoutMilk: LinearLayout
    private lateinit var spMilk: Spinner

    private lateinit var layoutSugar: LinearLayout
    private lateinit var edtSugar: EditText

    private lateinit var edtQuantity: EditText
    private lateinit var btnAddToCart: MaterialButton
    private lateinit var btnBack: MaterialButton

    private var productId: String = ""
    private var productName: String = ""
    private var category: String = ""
    private var basePrice: Double = 0.0
    private var imageRes: Int = 0
    private var isCoffee: Boolean = false
    private var ingredients: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        bindViews()
        readIntent()
        setupUi()
        setupActions()
    }

    private fun bindViews() {
        imgProduct = findViewById(R.id.imgProduct)
        tvTitle = findViewById(R.id.tvDetailTitle)
        tvIngredients = findViewById(R.id.tvIngredients)

        layoutSize = findViewById(R.id.layoutSize)
        rgSize = findViewById(R.id.rgSize)
        rbSmall = findViewById(R.id.rbSmall)
        rbMedium = findViewById(R.id.rbMedium)
        rbLarge = findViewById(R.id.rbLarge)

        layoutMilk = findViewById(R.id.layoutMilk)
        spMilk = findViewById(R.id.spMilk)

        layoutSugar = findViewById(R.id.layoutSugar)
        edtSugar = findViewById(R.id.edtSugar)

        edtQuantity = findViewById(R.id.edtQuantity)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun readIntent() {
        val i = intent
        productId = i.getStringExtra("id") ?: ""
        productName = i.getStringExtra("name") ?: ""
        category = i.getStringExtra("category") ?: ""
        // IMPORTANT: use "basePrice" key the same as in ProductsActivity
        basePrice = i.getDoubleExtra("basePrice", 0.0)
        imageRes = i.getIntExtra("imageRes", 0)
        isCoffee = i.getBooleanExtra("isCoffee", false)
        ingredients = i.getStringExtra("ingredients") ?: ""
    }

    private fun setupUi() {
        tvTitle.text = productName
        imgProduct.setImageResource(imageRes)

        // ingredients from menu or fallback by category
        val ingText = if (ingredients.isNotBlank()) {
            ingredients
        } else {
            when (category.lowercase()) {
                "coffee" -> "Freshly brewed coffee, customizable milk and sugar."
                "snack" -> "Perfect snack to go with your coffee."
                "dessert" -> "Sweet treat served fresh."
                else -> ""
            }
        }

        tvIngredients.text = if (ingText.isNotBlank()) "Ingredients: $ingText" else ""

        val allowCoffeeOptions =
            isCoffee && !productName.contains("espresso", ignoreCase = true)

        if (allowCoffeeOptions) {
            layoutSize.visibility = View.VISIBLE
            layoutMilk.visibility = View.VISIBLE
            layoutSugar.visibility = View.VISIBLE
            rbSmall.isChecked = true
        } else if (isCoffee) {
            // espresso / double espresso – fixed, no options
            layoutSize.visibility = View.GONE
            layoutMilk.visibility = View.GONE
            layoutSugar.visibility = View.GONE
        } else {
            // snacks & desserts – no options
            layoutSize.visibility = View.GONE
            layoutMilk.visibility = View.GONE
            layoutSugar.visibility = View.GONE
        }

        // simple milk options
        if (spMilk.adapter == null) {
            val milks = listOf(
                "No extra milk",
                "Whole milk",
                "Oat milk (+£0.20)",
                "Almond milk (+£0.20)"
            )
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, milks)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spMilk.adapter = adapter
        }

        edtQuantity.setText("1")
    }

    private fun setupActions() {
        btnBack.setOnClickListener { finish() }

        btnAddToCart.setOnClickListener {
            val qtyText = edtQuantity.text.toString().trim()
            val quantity = qtyText.toIntOrNull() ?: 0
            if (quantity <= 0) {
                Toast.makeText(this, "Enter quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sel = buildSelection()

            CartManager.addItem(
                name = productName,
                category = category,
                size = sel.size,
                milk = sel.milk,
                sugar = sel.sugar,
                unitPrice = sel.price,
                imageResId = imageRes,
                quantity = quantity
            )

            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Holds calculated options
    private data class Selection(
        val size: String,
        val milk: String,
        val sugar: String,
        val price: Double
    )

    private fun buildSelection(): Selection {
        val cat = category.lowercase()

        // Snacks & desserts: just base price per item
        if (cat == "snack" || cat == "dessert") {
            return Selection(
                size = "",
                milk = "",
                sugar = "",
                price = basePrice
            )
        }

        // Espresso / double espresso: fixed
        if (isCoffee && productName.contains("espresso", ignoreCase = true)) {
            return Selection(
                size = "",
                milk = "",
                sugar = "",
                price = basePrice
            )
        }

        // Regular coffees with options
        var sizeLabel = "Small"
        var price = basePrice

        when (rgSize.checkedRadioButtonId) {
            R.id.rbMedium -> {
                sizeLabel = "Medium"
                price += 1.0
            }
            R.id.rbLarge -> {
                sizeLabel = "Large"
                price += 1.5
            }
            else -> {
                sizeLabel = "Small"
            }
        }

        val milkChoice =
            if (spMilk.selectedItemPosition > 0) spMilk.selectedItem.toString() else ""

        val sugarText = edtSugar.text.toString().trim()
        val sugarLabel = if (sugarText.isEmpty()) "0" else sugarText

        val milkLower = milkChoice.lowercase()
        if (milkLower.contains("oat") || milkLower.contains("almond")) {
            price += 0.20
        }

        return Selection(
            size = sizeLabel,
            milk = milkChoice,
            sugar = sugarLabel,
            price = price
        )
    }
}
