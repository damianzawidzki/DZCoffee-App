package com.example.dzcoffee.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dzcoffee.data.model.Product
import com.example.dzcoffee.data.repo.ProductRepository

class ProductsViewModel(
    private val repo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    init {
        loadMenu()
    }

    // Load static menu from local data source
    private fun loadMenu() {
        _products.value = repo.getAllProducts()
    }
}
