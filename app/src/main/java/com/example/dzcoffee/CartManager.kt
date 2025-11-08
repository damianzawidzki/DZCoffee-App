package com.example.dzcoffee

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

object CartManager {

    private val items = mutableListOf<CartItem>()

    fun addItem(product: Product) {
        val existing = items.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += 1
        } else {
            items.add(CartItem(product, 1))
        }
    }

    fun removeItem(productId: String) {
        val existing = items.find { it.product.id == productId }
        if (existing != null) {
            existing.quantity -= 1
            if (existing.quantity <= 0) {
                items.remove(existing)
            }
        }
    }

    fun clear() {
        items.clear()
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getTotalPrice(): Double {
        return items.sumOf { it.product.price * it.quantity }
    }

    fun isEmpty(): Boolean = items.isEmpty()
}
