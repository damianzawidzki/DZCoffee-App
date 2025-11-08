package com.example.dzcoffee

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductsActivity : AppCompatActivity() {

    data class MenuItemDef(
        val id: String,
        val name: String,
        val fromPrice: Double,
        val imageRes: Int,
        val category: String,
        val isCoffee: Boolean
    )

    private val menuItems = listOf(
        // Coffee (sizes)
        MenuItemDef("americano", "Americano", 2.20, R.drawable.americano, "Coffee", true),
        MenuItemDef("latte", "Latte", 2.50, R.drawable.coffee_latte, "Coffee", true),
        MenuItemDef("cappuccino", "Cappuccino", 2.50, R.drawable.cappuccino, "Coffee", true),
        MenuItemDef("flat_white", "Flat White", 2.70, R.drawable.flat_white, "Coffee", true),
        MenuItemDef("mocha", "Mocha", 2.80, R.drawable.mocha, "Coffee", true),
        MenuItemDef("vanilla_latte", "Vanilla Latte", 2.80, R.drawable.vanilla_latte, "Coffee", true),
        MenuItemDef("caramel_latte", "Caramel Latte", 2.80, R.drawable.carmel_latte, "Coffee", true),
        MenuItemDef("ice_coffee", "Iced Coffee", 2.60, R.drawable.ice_coffee, "Coffee", true),
        MenuItemDef("double_espresso", "Double Espresso", 2.40, R.drawable.double_express, "Coffee", true),

        // Snacks (no sizes)
        MenuItemDef("sandwich", "Grilled Sandwich", 3.80, R.drawable.sandwich, "Snack", false),
        MenuItemDef("chicken_sandwich", "Chicken Sandwich", 4.00, R.drawable.chicken_sandwich, "Snack", false),
        MenuItemDef("veg_wrap", "Veggie Wrap", 3.50, R.drawable.veg_wrap, "Snack", false),
        MenuItemDef("bagel", "Bagel with Cream Cheese", 2.50, R.drawable.bagel, "Snack", false),
        MenuItemDef("croissant", "Butter Croissant", 2.20, R.drawable.croissant, "Snack", false),
        MenuItemDef("panini", "Panini", 4.20, R.drawable.panini, "Snack", false),

        // Sweets
        MenuItemDef("brownie", "Brownie", 2.20, R.drawable.brownie, "Sweet", false),
        MenuItemDef("cookie", "Cookie", 1.80, R.drawable.cookie, "Sweet", false),
        MenuItemDef("cheesecake", "Cheesecake", 3.00, R.drawable.cheesecake, "Sweet", false),
        MenuItemDef("blueberry_cupcake", "Blueberry Cupcake", 2.50, R.drawable.blueberry_cupcake, "Sweet", false),
        MenuItemDef("chocolate_muffin", "Chocolate Muffin", 2.20, R.drawable.chocolate_muffin, "Sweet", false),
        MenuItemDef("cinnamon_rolls", "Cinnamon Rolls", 2.50, R.drawable.cinnamon_rolls, "Sweet", false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        val layoutProducts = findViewById<LinearLayout>(R.id.layoutProducts)
        val inflater = LayoutInflater.from(this)

        menuItems.forEach { item ->
            val row = inflater.inflate(R.layout.row_product_item, layoutProducts, false)

            val img = row.findViewById<ImageView>(R.id.imgItem)
            val tvName = row.findViewById<TextView>(R.id.tvItemName)
            val tvInfo = row.findViewById<TextView>(R.id.tvItemInfo)
            val tvChoose = row.findViewById<TextView>(R.id.tvChoose)

            img.setImageResource(item.imageRes)
            tvName.text = item.name
            tvInfo.text = if (item.isCoffee) {
                "from £${"%.2f".format(item.fromPrice)} • tap to choose size"
            } else {
                "£${"%.2f".format(item.fromPrice)}"
            }

            row.setOnClickListener {
                openDetail(item)
            }
            tvChoose.setOnClickListener {
                openDetail(item)
            }

            layoutProducts.addView(row)
        }
    }

    private fun openDetail(item: MenuItemDef) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("id", item.id)
            putExtra("name", item.name)
            putExtra("fromPrice", item.fromPrice)
            putExtra("imageRes", item.imageRes)
            putExtra("category", item.category)
            putExtra("isCoffee", item.isCoffee)
        }
        startActivity(intent)
    }
}
