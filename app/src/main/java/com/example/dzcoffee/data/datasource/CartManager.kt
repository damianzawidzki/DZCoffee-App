package com.example.dzcoffee.data.datasource

import com.example.dzcoffee.data.model.CartItem

// Simple in-memory cart manager
object CartManager {

    private val items = mutableListOf<CartItem>()

    fun getItems(): List<CartItem> = items.toList()

    fun addItem(item: CartItem) {
        // if same product & options – just increase quantity
        val existingIndex = items.indexOfFirst {
            it.id == item.id &&
                    it.size == item.size &&
                    it.milk == item.milk &&
                    it.sugar == item.sugar
        }

        if (existingIndex >= 0) {
            val current = items[existingIndex]
            val updated = current.copy(
                quantity = current.quantity + item.quantity
            )
            items[existingIndex] = updated
        } else {
            items.add(item)
        }
    }

    fun remove(item: CartItem) {
        items.remove(item)
    }

    // Remove by index (used in CartViewModel)
    fun removeItemAt(index: Int) {
        if (index in items.indices) {
            items.removeAt(index)
        }
    }

    fun clear() {
        items.clear()
    }

    fun getTotal(): Double {
        return items.sumOf { it.unitPrice * it.quantity }
    }

    // Check if cart is empty (used in CartViewModel)
    val isEmpty: Boolean
        get() = items.isEmpty()
}
