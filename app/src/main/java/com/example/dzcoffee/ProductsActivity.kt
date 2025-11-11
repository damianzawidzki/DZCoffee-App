package com.example.dzcoffee

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProductsActivity : AppCompatActivity() {

    data class MenuItemDef(
        val id: String,
        val name: String,
        val category: String,      // Coffee / Snack / Dessert
        val basePrice: Double,
        val imageRes: Int,
        val isCoffee: Boolean,
        val allowOptions: Boolean, // size/milk/sugar (only regular coffees)
        val ingredients: String
    )

    private lateinit var layoutProducts: LinearLayout

    private val menuItems by lazy {
        listOf(
            // Coffee with size/milk options
            MenuItemDef(
                "americano",
                "Americano",
                "Coffee",
                3.00,
                R.drawable.americano,
                true,
                true,
                "Espresso shot, hot water"
            ),
            MenuItemDef(
                "latte",
                "Caffè Latte",
                "Coffee",
                3.50,
                R.drawable.coffee_latte,
                true,
                true,
                "Espresso, steamed milk, light milk foam"
            ),
            MenuItemDef(
                "cappuccino",
                "Cappuccino",
                "Coffee",
                3.20,
                R.drawable.cappuccino,
                true,
                true,
                "Espresso, steamed milk, thick milk foam, cocoa dusting"
            ),
            MenuItemDef(
                "flatwhite",
                "Flat White",
                "Coffee",
                3.30,
                R.drawable.flat_white,
                true,
                true,
                "Double espresso, steamed milk, microfoam"
            ),
            MenuItemDef(
                "mocha",
                "Mocha",
                "Coffee",
                3.60,
                R.drawable.mocha,
                true,
                true,
                "Espresso, steamed milk, chocolate syrup, whipped cream (optional)"
            ),

            // Espresso without options
            MenuItemDef(
                "single_espresso",
                "Single Espresso",
                "Coffee",
                4.00,
                R.drawable.single_express,
                true,
                false,
                "Single espresso shot (100% arabica beans)"
            ),
            MenuItemDef(
                "double_espresso",
                "Double Espresso",
                "Coffee",
                5.00,
                R.drawable.double_express,
                true,
                false,
                "Double espresso shot (100% arabica beans)"
            ),

            // Snacks
            MenuItemDef(
                "panini",
                "Panini",
                "Snack",
                4.20,
                R.drawable.panini,
                false,
                false,
                "Ciabatta bread, ham, cheddar cheese, tomato, butter"
            ),
            MenuItemDef(
                "sandwich",
                "Sandwich",
                "Snack",
                3.80,
                R.drawable.sandwich,
                false,
                false,
                "White bread, ham or turkey, lettuce, tomato, mayonnaise"
            ),
            MenuItemDef(
                "bagel",
                "Bagel",
                "Snack",
                3.30,
                R.drawable.bagel,
                false,
                false,
                "Wheat bagel, cream cheese, butter"
            ),
            MenuItemDef(
                "croissant",
                "Croissant",
                "Snack",
                2.80,
                R.drawable.croissant,
                false,
                false,
                "Wheat flour, butter, yeast, milk, egg, salt"
            ),
            MenuItemDef(
                "veg_wrap",
                "Veggie Wrap",
                "Snack",
                4.50,
                R.drawable.veg_wrap,
                false,
                false,
                "Tortilla wrap, lettuce, tomato, cucumber, peppers, hummus"
            ),

            // Desserts
            MenuItemDef(
                "brownie",
                "Brownie",
                "Dessert",
                2.60,
                R.drawable.brownie,
                false,
                false,
                "Dark chocolate, butter, sugar, eggs, wheat flour, cocoa"
            ),
            MenuItemDef(
                "cheesecake",
                "Cheesecake",
                "Dessert",
                3.20,
                R.drawable.cheesecake,
                false,
                false,
                "Cream cheese, biscuit base, sugar, eggs, butter, vanilla"
            ),
            MenuItemDef(
                "carrot_cake",
                "Carrot Cake",
                "Dessert",
                3.00,
                R.drawable.carrot,
                false,
                false,
                "Carrots, wheat flour, sugar, eggs, walnuts, cinnamon, cream cheese frosting"
            ),
            MenuItemDef(
                "cookie",
                "Cookie",
                "Dessert",
                1.80,
                R.drawable.cookie,
                false,
                false,
                "Wheat flour, butter, sugar, chocolate chips, eggs"
            ),
            MenuItemDef(
                "cin_rolls",
                "Cinnamon Rolls",
                "Dessert",
                2.90,
                R.drawable.cinnamon_rolls,
                false,
                false,
                "Sweet dough, butter, sugar, cinnamon, vanilla icing"
            ),
            MenuItemDef(
                "choco_muffin",
                "Chocolate Muffin",
                "Dessert",
                2.40,
                R.drawable.chocolate_muffin,
                false,
                false,
                "Cocoa, dark chocolate chips, wheat flour, butter, sugar, eggs"
            ),
            MenuItemDef(
                "blueberry_cupcake",
                "Blueberry Cupcake",
                "Dessert",
                2.50,
                R.drawable.blueberry_cupcake,
                false,
                false,
                "Wheat flour, sugar, butter, eggs, blueberries, cream cheese frosting"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        layoutProducts = findViewById(R.id.layoutProducts)

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
            CartManager.clear()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        buildMenu()
    }

    private fun buildMenu() {
        val inflater = LayoutInflater.from(this)
        layoutProducts.removeAllViews()

        var currentCategory: String? = null

        menuItems.forEach { item ->
            if (currentCategory != item.category) {
                currentCategory = item.category
                val header = inflater.inflate(R.layout.row_category_header, layoutProducts, false)
                header.findViewById<TextView>(R.id.tvCategoryTitle).text = currentCategory
                layoutProducts.addView(header)
            }

            val row = inflater.inflate(R.layout.row_product_item, layoutProducts, false)
            row.findViewById<ImageView>(R.id.imgItem).setImageResource(item.imageRes)
            row.findViewById<TextView>(R.id.tvItemName).text = item.name
            row.findViewById<TextView>(R.id.tvItemInfo).text = item.ingredients

            val priceText = if (item.isCoffee && item.allowOptions) {
                "from £${fmt(item.basePrice)}"
            } else {
                "£${fmt(item.basePrice)}"
            }

            row.findViewById<TextView>(R.id.tvItemPrice).text = priceText

            row.setOnClickListener {
                openDetail(item)
            }

            layoutProducts.addView(row)
        }
    }

    private fun openDetail(item: MenuItemDef) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("name", item.name)
        intent.putExtra("category", item.category)
        intent.putExtra("basePrice", item.basePrice)
        intent.putExtra("imageRes", item.imageRes)
        intent.putExtra("isCoffee", item.isCoffee)
        intent.putExtra("allowOptions", item.allowOptions)
        intent.putExtra("ingredients", item.ingredients)
        startActivity(intent)
    }

    @SuppressLint("DefaultLocale")
    private fun fmt(v: Double) = String.format("%.2f", v)
}
