package com.example.dzcoffee

data class CartItem(
    val name: String,
    val category: String,
    val size: String,
    val milk: String,
    val sugar: String,
    val unitPrice: Double,
    val quantity: Int,
    val imageResId: Int
) {
    val totalPrice: Double
        get() = unitPrice * quantity
}

object CartManager {

    private val items = mutableListOf<CartItem>()

    fun addItem(
        name: String,
        category: String,
        size: String,
        milk: String,
        sugar: String,
        unitPrice: Double,
        imageResId: Int,
        quantity: Int
    ) {
        val existing = items.find {
            it.name == name &&
                    it.category == category &&
                    it.size == size &&
                    it.milk == milk &&
                    it.sugar == sugar &&
                    it.unitPrice == unitPrice
        }

        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + quantity)
            items.remove(existing)
            items.add(updated)
        } else {
            items.add(
                CartItem(
                    name = name,
                    category = category,
                    size = size,
                    milk = milk,
                    sugar = sugar,
                    unitPrice = unitPrice,
                    quantity = quantity,
                    imageResId = imageResId
                )
            )
        }
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getTotal(): Double = items.sumOf { it.totalPrice }

    fun clear() {
        items.clear()
    }

    fun removeItemAt(index: Int) {
        if (index in items.indices) {
            items.removeAt(index)
        }
    }

    val isEmpty: Boolean
        get() = items.isEmpty()
}
