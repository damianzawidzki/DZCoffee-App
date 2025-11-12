package com.example.dzcoffee.data.repo

import com.example.dzcoffee.data.datasource.MenuLocalDataSource
import com.example.dzcoffee.data.model.Product

class ProductRepository(
    private val dataSource: MenuLocalDataSource = MenuLocalDataSource()
) {
    // Simple local menu – no network
    fun getAllProducts(): List<Product> = dataSource.getMenu()
}
