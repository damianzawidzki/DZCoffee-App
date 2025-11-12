package com.example.dzcoffee.data.repo

data class MenuItem(
    var id: String = "",
    var name: String = "",
    var category: String = "Coffee",
    var price: Double = 0.0,
    var ingredients: String = "",
    var imageUrl: String? = null,
    var isCoffee: Boolean = false,
    var allowOptions: Boolean = false,
    var isActive: Boolean = true
)