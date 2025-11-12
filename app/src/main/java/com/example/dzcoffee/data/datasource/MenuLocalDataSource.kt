package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.R
import com.example.dzcoffee.data.model.Product

class MenuLocalDataSource {

    fun getMenu(): List<Product> {
        return listOf(
            // --- Coffee ---
            Product("coffee_americano", "Americano", "Hot americano coffee", 3.20, "Coffee", R.drawable.americano, true),
            Product("coffee_cappuccino", "Cappuccino", "Espresso with milk foam", 3.40, "Coffee", R.drawable.cappuccino, true),
            Product("coffee_latte", "Latte", "Smooth milk coffee", 3.60, "Coffee", R.drawable.coffee_latte, true),
            Product("coffee_flatwhite", "Flat white", "Double espresso with milk", 3.70, "Coffee", R.drawable.flat_white, true),
            Product("coffee_mocha", "Mocha", "Chocolate + espresso + milk", 3.80, "Coffee", R.drawable.mocha, true),
            Product("coffee_vanilla", "Vanilla latte", "Latte with vanilla flavour", 3.90, "Coffee", R.drawable.vanilla_latte, true),
            Product("coffee_single", "Single espresso", "One strong shot", 2.20, "Coffee", R.drawable.single_express, true),
            Product("coffee_double", "Double espresso", "Two espresso shots", 2.80, "Coffee", R.drawable.double_express, true),

            // --- Iced / special coffee ---
            Product("coffee_iced", "Iced coffee", "Cold coffee with ice", 3.50, "Iced & special coffee", R.drawable.ice_coffee, true),
            Product("coffee_caramel", "Caramel latte", "Latte with caramel syrup", 3.90, "Iced & special coffee", R.drawable.carmel_latte, true),

            // --- Snacks ---
            Product("snack_chicken_sandwich", "Chicken sandwich", "Grilled chicken with salad", 4.50, "Snacks", R.drawable.chicken_sandwich, false),
            Product("snack_panini", "Panini", "Hot grilled panini", 4.20, "Snacks", R.drawable.panini, false),
            Product("snack_sandwich", "Ham & cheese sandwich", "Classic warm sandwich", 4.00, "Snacks", R.drawable.sandwich, false),
            Product("snack_bagel", "Bagel with cheese", "Toasted bagel with tomatoes", 3.20, "Snacks", R.drawable.bagel, false),
            Product("snack_croissant", "Almond croissant", "Buttery croissant with almonds", 2.80, "Snacks", R.drawable.croissant, false),
            Product("snack_veg_wrap", "Veggie wrap", "Wrap with veggies & chicken", 4.60, "Snacks", R.drawable.veg_wrap, false),

            // --- Desserts ---
            Product("dessert_cheesecake", "Cheesecake", "Creamy baked cheesecake", 2.90, "Desserts", R.drawable.cheesecake, false),
            Product("dessert_brownie", "Brownie", "Rich chocolate brownie", 2.50, "Desserts", R.drawable.brownie, false),
            Product("dessert_cookie", "Cookie", "Big cookie with chocolate chips", 1.50, "Desserts", R.drawable.cookie, false),
            Product("dessert_choco_muffin", "Chocolate muffin", "Soft muffin with chocolate", 2.20, "Desserts", R.drawable.chocolate_muffin, false),
            Product("dessert_blueberry_cupcake", "Blueberry cupcake", "Cupcake with cream & blueberry", 2.40, "Desserts", R.drawable.blueberry_cupcake, false),
            Product("dessert_cinnamon_rolls", "Cinnamon rolls", "Fresh cinnamon pastry", 2.60, "Desserts", R.drawable.cinnamon_rolls, false),
            Product("dessert_carrot_cake", "Carrot cake", "Carrot cake with nuts", 2.80, "Desserts", R.drawable.carrot, false)
        )
    }
}
