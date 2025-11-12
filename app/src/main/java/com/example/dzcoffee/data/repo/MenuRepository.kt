package com.example.dzcoffee.data.repository

import com.example.dzcoffee.data.datasource.MenuLocalDataSource
import com.example.dzcoffee.data.model.Product

// Repository for menu items (local only)
class MenuRepository(
    private val localDataSource: MenuLocalDataSource
) {

    // Get full menu list
    fun getAllProducts(): List<Product> {
        return localDataSource.getMenu()
    }

    // Get by category (Coffee / Snacks / Dessert)
    fun getProductsByCategory(category: String): List<Product> {
        return localDataSource.getMenu().filter { it.category == category }
    }

    // Get single product by id
    fun getProductById(id: String): Product? {
        return localDataSource.getMenu().find { it.id == id }
    }
}
