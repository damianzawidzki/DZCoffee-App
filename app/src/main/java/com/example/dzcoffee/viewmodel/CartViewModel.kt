package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dzcoffee.data.datasource.CartManager
import com.example.dzcoffee.data.model.CartItem
import java.util.Locale

// ViewModel for cart screen
class CartViewModel : ViewModel() {

    private val _items = MutableLiveData<List<CartItem>>()
    val items: LiveData<List<CartItem>> = _items

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    init {
        refresh()
    }

    // Reload cart from CartManager
    fun refresh() {
        val current = CartManager.getItems()
        _items.value = current
        _total.value = CartManager.getTotal()
    }

    // Clear all cart
    fun clearCart() {
        CartManager.clear()
        refresh()
    }

    // Remove item by index
    fun removeItemAt(index: Int) {
        CartManager.removeItemAt(index)
        refresh()
    }

    // Check if cart is empty
    fun isEmpty(): Boolean {
        return CartManager.isEmpty
    }

    // Helper for formatted total if you want
    fun getFormattedTotal(): String {
        val t = _total.value ?: 0.0
        return String.format(Locale.UK, "Total: £%.2f", t)
    }
}
